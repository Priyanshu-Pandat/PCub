package com.pcub.Ride_Service.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ZoneService {


    private static final double AIRPORT_LAT = 26.8242;
    private static final double AIRPORT_LON = 75.8122;

    // Allow a tiny tolerance in coordinates (e.g., 20 meters)
    private static final double EPSILON = 0.02; // ~20 meters
    public boolean isExpensiveZone(double lat, double lon) {
        return Math.abs(lat - AIRPORT_LAT) < EPSILON &&
                Math.abs(lon - AIRPORT_LON) < EPSILON;
    }


    public boolean isNightTime() {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        return now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(6, 0));
    }

    public boolean isPeakTime() {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        return (now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(10, 0))) ||
                (now.isAfter(LocalTime.of(17, 0)) && now.isBefore(LocalTime.of(20, 0)));
    }

}
