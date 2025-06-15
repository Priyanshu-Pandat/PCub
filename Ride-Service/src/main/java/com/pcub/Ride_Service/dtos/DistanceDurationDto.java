package com.pcub.Ride_Service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceDurationDto {
    private double distanceKm;
    private int durationMin;
}
