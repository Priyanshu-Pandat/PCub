package com.pcub.Ride_Service.modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareEstimateRequest {
    private String sourcePlaceId;
    private String destinationPlaceId;
    private String promoCode;
}
