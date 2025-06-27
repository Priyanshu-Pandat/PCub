package com.driverService.controller;

import com.driverService.model.ApiResponse;
import com.driverService.model.DriverDistance;
import com.driverService.model.DriverLocationUpdateDTO;
import com.driverService.model.DriverStatusUpdateRequestDTO;
import com.driverService.service.DriverLocationService;
import com.driverService.service.RideSessionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/driver")
@Slf4j  // Lombok annotation to enable logging
public class DriverLocationController {

    @Autowired
    private DriverLocationService driverLocationService;
    @Autowired
    private RideSessionService rideSessionService; // uses Redis

    @PostMapping("/update-location")
      public ResponseEntity<ApiResponse> updateLocation(
                                   @RequestHeader("X-User-Id") Long driverId,
                                   @Valid @RequestBody  DriverLocationUpdateDTO dto) {
        DriverLocationUpdateDTO position = driverLocationService.updateDriverLocation(driverId, dto);

        String rideId = rideSessionService.getRideIdForDriver(driverId);
        log.info("Received DTO: lat={}, lon={}",
                dto.getLatitude(), dto.getLongitude());

        if (rideId != null) {
            // kuch krna h location update krande ke liye
        }
          return ResponseEntity.ok(
                  new ApiResponse(true, position ,"Driver status updated successfully")
          );
      }


    @PostMapping("/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @RequestHeader("X-User-Id") Long driverId,
            @Valid @RequestBody DriverStatusUpdateRequestDTO dto) {

        log.info("Status update request for driver: {}", driverId);
        driverLocationService.updateDriverStatus(driverId, dto);
        return ResponseEntity.ok(
                new ApiResponse(true,null ,"Driver status updated successfully")
        );
    }


}

