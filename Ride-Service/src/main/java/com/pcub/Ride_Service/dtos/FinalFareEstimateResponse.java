package com.pcub.Ride_Service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareEstimateFinalResponse {
    private ResolvedLocationDto source;
    private ResolvedLocationDto destination;
    private double distanceInKm;
    private int durationInMin;
    private List<FareWithVehicleAndReason> vehicleOptions;
}

