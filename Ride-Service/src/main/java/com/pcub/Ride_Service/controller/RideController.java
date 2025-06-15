package com.pcub.Ride_Service.controller;

import com.pcub.Ride_Service.dtos.FinalFareEstimateResponse;
import com.pcub.Ride_Service.modals.ApiResponse;
import com.pcub.Ride_Service.service.RidePreviewService;
import com.pcub.Ride_Service.service.RidePreviewServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ride")
@Log4j2
public class RideController {
    @Autowired
    private RidePreviewService ridePreviewService;
    @GetMapping("/ridepreviewStoD")
    public ResponseEntity<ApiResponse<FinalFareEstimateResponse>> getRidePreview(
            @RequestParam String sourcePlaceId,
            @RequestParam String destinationPlaceId
    ) {
        log.info("controller is working fine with :{}{}",sourcePlaceId,destinationPlaceId);
        FinalFareEstimateResponse response = ridePreviewService.generateRidePreview(sourcePlaceId, destinationPlaceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
