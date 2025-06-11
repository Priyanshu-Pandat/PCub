package com.pcub.Ride_Service.controller;

import com.pcub.Ride_Service.dtos.RidePreviewDto;
import com.pcub.Ride_Service.modals.ApiResponse;
import com.pcub.Ride_Service.service.RidePreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RidePreviewService ridePreviewService;

    @GetMapping("/initiateRidePreview")
    public ResponseEntity<ApiResponse<RidePreviewDto>> getRidePreview(
            @RequestParam String sourcePlaceId,
            @RequestParam String destinationPlaceId) {

        RidePreviewDto preview = ridePreviewService.generateRidePreview(sourcePlaceId, destinationPlaceId);
        return ResponseEntity.ok(new ApiResponse<>(true, preview, "Preview ready"));
    }
}

