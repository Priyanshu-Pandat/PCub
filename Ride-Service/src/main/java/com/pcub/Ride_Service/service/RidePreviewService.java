package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.dtos.FinalFareEstimateResponse;
import org.springframework.stereotype.Service;

@Service
public interface RidePreviewService {
    FinalFareEstimateResponse generateRidePreview(String sourcePlaceId, String destinationPlaceId);
}
