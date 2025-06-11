package com.pcub.Location_Service.DTOs;

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

