package com.driverService.controller;

import com.driverService.model.ApiResponse;
import com.driverService.model.DriverDistance;
import com.driverService.model.DriverLocationUpdateDTO;
import com.driverService.model.DriverStatusUpdateRequestDTO;
import com.driverService.service.DriverLocationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
@Slf4j  // Lombok annotation to enable logging
public class DriverLocationController {

    @Autowired
    private DriverLocationService driverLocationService;



    @PostMapping("/status")
    @CircuitBreaker(name = "driverService", fallbackMethod = "statusUpdateFallback")
    public ResponseEntity<ApiResponse> updateStatus(
            @RequestHeader("X-User-Id") Long driverId,
            @Valid @RequestBody DriverStatusUpdateRequestDTO dto) {

        log.info("Status update request for driver: {}", driverId);
        driverLocationService.updateDriverStatus(driverId, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Driver status updated successfully", null)
        );
    }
    @PostMapping("/location")
    public ResponseEntity<ApiResponse> updateLocation(
            @RequestHeader("X-User-Id") Long driverId,
            @Valid @RequestBody DriverLocationUpdateDTO dto) {

        DriverLocationUpdateDTO position = driverLocationService.updateDriverLocation(driverId, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Location updated", position)
        );
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius,
            @RequestParam(required = false) String vehicleType,
            @RequestParam(defaultValue = "10") int limit) {

        List<DriverDistance> drivers = driverLocationService.findNearbyDrivers(
                latitude, longitude, radius, vehicleType, limit);

        return ResponseEntity.ok(
                new ApiResponse(true, "Nearby drivers found", drivers)
        );
    }

    // Fallback method for circuit breaker
    private ResponseEntity<ApiResponse> statusUpdateFallback(
            Long driverId, DriverStatusUpdateRequestDTO dto, Exception e) {
        log.error("Fallback triggered for driver {}: {}", driverId, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse(false, "Service unavailable. Your request will be processed later.", null));
    }
}
