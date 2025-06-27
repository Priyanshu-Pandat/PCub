package com.driverService.service;

import com.driverService.exception.DriverNotAvailableException;
import com.driverService.exception.DriverNotFoundException;
import com.driverService.exception.ServiceUnavailableException;
import com.driverService.model.DriverAvailabilityStatus;
import com.driverService.model.DriverDistance;
import com.driverService.model.DriverLocationUpdateDTO;
import com.driverService.model.DriverStatusUpdateRequestDTO;
import com.driverService.repository.DriverRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class DriverLocationService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DriverRepo driverRepository;

    private static final String DRIVER_STATUS_HASH = "drivers:status";
    private static final String DRIVER_VEHICLE_HASH = "drivers:vehicle";
    private static final String DRIVER_LAST_UPDATED = "drivers:lastUpdated";
    private static final long DRIVER_TIMEOUT_SECONDS = 120;

    private final Queue<PendingLocationUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();

    // ✅ Driver sets ONLINE / OFFLINE
    @Timed(value = "driver.status.update")
    public DriverLocationUpdateDTO updateDriverStatus(Long driverId, DriverStatusUpdateRequestDTO dto) {
        driverRepository.findById(driverId).orElseThrow(() ->
                new DriverNotFoundException("Driver not found: " + driverId));

        String vehicleType = dto.getVehicleType().name();
        String geoKey = "drivers:geo:" + vehicleType;

        if (dto.getAvailabilityStatus() == DriverAvailabilityStatus.ONLINE) {
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForHash().put(DRIVER_STATUS_HASH, driverId.toString(), "ONLINE");
                    operations.opsForHash().put(DRIVER_VEHICLE_HASH, driverId.toString(), vehicleType);
                    operations.opsForGeo().add(geoKey,
                            new Point(dto.getLongitude(), dto.getLatitude()),
                            driverId.toString());
                    operations.opsForHash().put(DRIVER_LAST_UPDATED, driverId.toString(),
                            String.valueOf(Instant.now().getEpochSecond()));
                    return operations.exec();
                }
            });
            log.info("Driver {} is ONLINE at ({},{})", driverId, dto.getLatitude(), dto.getLongitude());
        } else {
            removeDriverFromRedis(driverId);
            log.info("Driver {} is now OFFLINE", driverId);
        }
        return new DriverLocationUpdateDTO(dto.getLatitude(), dto.getLongitude());
    }

    // ✅ Driver sends location update
    @Timed(value = "driver.location.update")
    @CircuitBreaker(name = "redisLocationCB", fallbackMethod = "fallbackLocationUpdate")
    public DriverLocationUpdateDTO updateDriverLocation(Long driverId, DriverLocationUpdateDTO dto) {
        try {
            String status = (String) redisTemplate.opsForHash().get(DRIVER_STATUS_HASH, driverId.toString());
            if (!"ONLINE".equals(status)) {
                throw new DriverNotAvailableException("Driver is not online");
            }

            String vehicleType = (String) redisTemplate.opsForHash().get(DRIVER_VEHICLE_HASH, driverId.toString());
            if (vehicleType == null) {
                throw new IllegalStateException("Vehicle type missing for driver " + driverId);
            }

            String geoKey = "drivers:geo:" + vehicleType;
            Point point = new Point(dto.getLongitude(), dto.getLatitude());

            redisTemplate.opsForGeo().add(geoKey, point, driverId.toString());
            redisTemplate.opsForHash().put(DRIVER_LAST_UPDATED, driverId.toString(),
                    String.valueOf(Instant.now().getEpochSecond()));

            log.debug("Updated location for driver {}: {}", driverId, point);
            return new DriverLocationUpdateDTO(dto.getLatitude(), dto.getLongitude());

        } catch (Exception e) {
            log.error("Location update failed for driver {}, queued", driverId, e);
            pendingUpdates.add(new PendingLocationUpdate(driverId, dto));
            throw new ServiceUnavailableException("Location update queued for retry");
        }
    }

    // ✅ Retry failed updates
    @Scheduled(fixedRate = 30000)
    public void processPendingUpdates() {
        while (!pendingUpdates.isEmpty()) {
            PendingLocationUpdate update = pendingUpdates.poll();
            try {
                updateDriverLocation(update.driverId(), update.dto());
                log.info("Successfully retried location update for driver {}", update.driverId());
            } catch (Exception e) {
                log.warn("Failed again for driver {}, re-queuing", update.driverId());
                pendingUpdates.add(update);
                break;
            }
        }
    }

    // ✅ Auto-remove inactive drivers
    @Scheduled(fixedRate = 60000)
    public void removeInactiveDrivers() {
        Map<Object, Object> lastUpdatedMap = redisTemplate.opsForHash().entries(DRIVER_LAST_UPDATED);
        long now = Instant.now().getEpochSecond();

        for (Map.Entry<Object, Object> entry : lastUpdatedMap.entrySet()) {
            String driverId = (String) entry.getKey();
            long lastUpdated = Long.parseLong((String) entry.getValue());

            if (now - lastUpdated > DRIVER_TIMEOUT_SECONDS) {
                log.warn("Driver {} inactive for {}s, removing...", driverId, now - lastUpdated);
                removeDriverFromRedis(Long.parseLong(driverId));
            }
        }
    }

    // 🧹 Remove driver from Redis
    private void removeDriverFromRedis(Long driverId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String vehicleType = (String) redisTemplate.opsForHash().get(DRIVER_VEHICLE_HASH, driverId.toString());
                if (vehicleType == null) vehicleType = "UNKNOWN";

                String geoKey = "drivers:geo:" + vehicleType;

                operations.multi();
                operations.opsForHash().put(DRIVER_STATUS_HASH, driverId.toString(), "OFFLINE");
                operations.opsForGeo().remove(geoKey, driverId.toString());
                operations.opsForHash().delete(DRIVER_VEHICLE_HASH, driverId.toString());
                operations.opsForHash().delete(DRIVER_LAST_UPDATED, driverId.toString());
                return operations.exec();
            }
        });
    }
    public DriverLocationUpdateDTO fallbackLocationUpdate(Long driverId, DriverLocationUpdateDTO dto, Throwable t) {
        log.warn("Redis failure, fallback location update");
        pendingUpdates.add(new PendingLocationUpdate(driverId, dto));
        return dto;
    }


    private record PendingLocationUpdate(Long driverId, DriverLocationUpdateDTO dto) {}
}
