package com.pcub.Ride_Service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideBookingRequest {
    private String sourceAddress;
    private double sourceLat;
    private double sourceLng;

    private String destinationAddress;
    private double destinationLat;
    private double destinationLng;
    private int  price;
    private String vehicleType;
}
