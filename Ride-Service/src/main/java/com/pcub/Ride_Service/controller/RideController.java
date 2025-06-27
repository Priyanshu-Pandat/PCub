package com.pcub.Ride_Service.controller;

import com.pcub.Ride_Service.dtos.FinalFareEstimateResponse;
import com.pcub.Ride_Service.modals.ApiResponse;
import com.pcub.Ride_Service.modals.DriverDistance;
import com.pcub.Ride_Service.service.FindNearByDriversService;
import com.pcub.Ride_Service.service.RidePreviewService;
import com.pcub.Ride_Service.service.RidePreviewServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ride")
@Log4j2

public class RideController {
    @Autowired
    private RidePreviewService ridePreviewService;
    @Autowired
    private FindNearByDriversService findNearByDriversService;


    @GetMapping("/ridepreviewStoD")
    public ResponseEntity<ApiResponse<FinalFareEstimateResponse>> getRidePreview(
            @RequestParam String sourcePlaceId,
            @RequestParam String destinationPlaceId
    ) {
        log.info("controller is working fine with :{}{}",sourcePlaceId,destinationPlaceId);
        FinalFareEstimateResponse response = ridePreviewService.generateRidePreview(sourcePlaceId, destinationPlaceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(defaultValue = "10") int limit) {

        List<DriverDistance> drivers = findNearByDriversService.findNearbyDrivers(
                latitude, longitude, radius, vehicleType, limit);

        return ResponseEntity.ok(
                new ApiResponse(true, drivers, "ALL NEAR BY DRIVERS")
        );
    }



}
