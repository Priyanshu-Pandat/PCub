package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.dtos.RidePreviewDto;
import org.springframework.stereotype.Service;

@Service
public interface RidePreviewService {
    RidePreviewDto generateRidePreview(String sourcePlaceId, String destinationPlaceId);
}
