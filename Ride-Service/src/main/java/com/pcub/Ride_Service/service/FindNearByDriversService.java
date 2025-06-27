package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.modals.DriverDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import io.micrometer.core.annotation.Timed;

import java.util.ArrayList;
import java.util.List;
@Service
public class FindNearByDriversService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String DRIVER_STATUS_HASH = "drivers:status";
    private static final String DRIVER_VEHICLE_HASH = "drivers:vehicle";
    private static final String DRIVER_LAST_UPDATED = "drivers:lastUpdated";
    private static final long DRIVER_TIMEOUT_SECONDS = 120;


    @Timed(value = "driver.nearby.search")
    public List<DriverDistance> findNearbyDrivers(
            double latitude, double longitude,
            double radius, String vehicleType, int limit) {

        if (vehicleType == null || vehicleType.isBlank()) {
            throw new IllegalArgumentException("Vehicle type must be provided for nearby search");
        }

        String geoKey = "drivers:geo:" + vehicleType;
        Circle area = new Circle(new Point(longitude, latitude),
                new Distance(radius, Metrics.KILOMETERS));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(limit);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(geoKey, area, args);

        List<DriverDistance> driverDistances = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            String driverId = result.getContent().getName();
            double distance = result.getDistance().getValue();
            driverDistances.add(new DriverDistance(Long.parseLong(driverId), distance));
        }

        return driverDistances;
    }

}
