package com.driverService.service;

import com.driverService.exception.DriverNotAvailableException;
import com.driverService.exception.DriverNotFoundException;
import com.driverService.exception.ServiceUnavailableException;
import com.driverService.model.DriverAvailabilityStatus;
import com.driverService.model.DriverDistance;
import com.driverService.model.DriverLocationUpdateDTO;
import com.driverService.model.DriverStatusUpdateRequestDTO;
import com.driverService.repository.DriverRepo;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverLocationService {

//    private static final String DRIVER_LOCATION_KEY = "drivers:location";
//    private static final String DRIVER_STATUS_KEY = "drivers:status";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DriverRepo driverRepository;

    private static final String DRIVER_LOCATION_GEO_KEY = "drivers:geo";
    private static final String DRIVER_STATUS_HASH = "drivers:status";
    private static final String DRIVER_VEHICLE_HASH = "drivers:vehicle";



    private final Queue<PendingLocationUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();



    @Timed(value = "driver.status.update")
    public void updateDriverStatus(Long driverId, DriverStatusUpdateRequestDTO dto) {
        // Validate driver exists
        driverRepository.findById(driverId).orElseThrow(() ->
                new DriverNotFoundException("Driver not found: " + driverId));

        if (dto.getAvailabilityStatus() == DriverAvailabilityStatus.ONLINE) {
            // Transactional update
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForHash().put(DRIVER_STATUS_HASH, driverId.toString(), "ONLINE");
                    operations.opsForHash().put(DRIVER_VEHICLE_HASH, driverId.toString(), dto.getVehicleType().name());
                    operations.opsForGeo().add(DRIVER_LOCATION_GEO_KEY,
                            new Point(dto.getLongitude(), dto.getLatitude()),
                            driverId.toString());
                    return operations.exec();
                }
            });
            log.info("Driver {} is now ONLINE at ({},{})", driverId, dto.getLatitude(), dto.getLongitude());
        } else {
            redisTemplate.opsForHash().put(DRIVER_STATUS_HASH, driverId.toString(), "OFFLINE");
            redisTemplate.opsForGeo().remove(DRIVER_LOCATION_GEO_KEY, driverId.toString());
            log.info("Driver {} is now OFFLINE", driverId);
        }
    }

    @Timed(value = "driver.location.update")
    public DriverLocationUpdateDTO updateDriverLocation(Long driverId, DriverLocationUpdateDTO dto) {
        try {
            // Check if driver is online
            String status = (String) redisTemplate.opsForHash().get(DRIVER_STATUS_HASH, driverId.toString());
            if (!"ONLINE".equals(status)) {
                throw new DriverNotAvailableException("Driver is not online");
            }

            // Update location
            Point point = new Point(dto.getLongitude(), dto.getLatitude());
            redisTemplate.opsForGeo().add(DRIVER_LOCATION_GEO_KEY, point, driverId.toString());

            log.debug("Updated location for driver {}: {}", driverId, point);
            return new DriverLocationUpdateDTO( dto.getLatitude(), dto.getLongitude());

        } catch (Exception e) {
            log.error("Location update failed for driver {}, queuing for retry", driverId, e);
            pendingUpdates.add(new PendingLocationUpdate(driverId, dto));
            throw new ServiceUnavailableException("Location update queued for retry");
        }
    }

    @Timed(value = "driver.nearby.search")
    public List<DriverDistance> findNearbyDrivers(
            double latitude, double longitude,
            double radius, String vehicleType, int limit) {

        Circle area = new Circle(new Point(longitude, latitude),
                new Distance(radius, Metrics.KILOMETERS));

        // Include distance in results for proper sorting
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()  // This ensures we get distance information
                .includeCoordinates()
                .sortAscending()    // Sort by distance from center point
                .limit(limit);      // Limit results

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVER_LOCATION_GEO_KEY, area, args);

        List<DriverDistance> driverDistances = new ArrayList<>();

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            String driverId = result.getContent().getName();
            double distance = result.getDistance().getValue();

            // Check vehicle type if specified
            if (vehicleType != null) {
                String driverVehicle = (String) redisTemplate.opsForHash()
                        .get(DRIVER_VEHICLE_HASH, driverId);
                if (!vehicleType.equalsIgnoreCase(driverVehicle)) {
                    continue;
                }
            }

            driverDistances.add(new DriverDistance(Long.parseLong(driverId), distance));
        }

        return driverDistances;
    }

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void processPendingUpdates() {
        while (!pendingUpdates.isEmpty()) {
            PendingLocationUpdate update = pendingUpdates.poll();
            try {
                updateDriverLocation(update.driverId(), update.dto());
                log.info("Successfully processed queued update for driver {}", update.driverId());
            } catch (Exception e) {
                log.warn("Failed to process queued update for driver {}, will retry", update.driverId());
                pendingUpdates.add(update);
                break;
            }
        }
    }

    private record PendingLocationUpdate(Long driverId, DriverLocationUpdateDTO dto) {}
}


