package com.driverService.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverPosition {
    private Long driverId;
    private double latitude;
    private double longitude;
}
