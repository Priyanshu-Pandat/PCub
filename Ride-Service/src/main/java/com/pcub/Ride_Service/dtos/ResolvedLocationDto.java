package com.pcub.Ride_Service.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolvedLocationDto {
    private String placeId;
    private double latitude;
    private double longitude;
}

