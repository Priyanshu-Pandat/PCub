package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.RideServiceApplication.LocationServiceClient;
import com.pcub.Ride_Service.dtos.ResolvedLocationDto;
import com.pcub.Ride_Service.dtos.RidePreviewDto;
import com.pcub.Ride_Service.dtos.VehicleOptionDto;
import com.pcub.Ride_Service.modals.ApiResponse;
import com.pcub.Ride_Service.modals.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RidePreviewServiceImpl implements RidePreviewService{
    @Autowired
    private LocationServiceClient locationClient;
    @Override
    public RidePreviewDto generateRidePreview(String sourcePlaceId, String destinationPlaceId) {
        ResponseEntity<ApiResponse<Map<String, ResolvedLocationDto>>> response =
                locationClient.resolvePlaceIds(sourcePlaceId, destinationPlaceId);

        Map<String, ResolvedLocationDto> coordinatesMap = response.getBody().getData();

        ResolvedLocationDto source = coordinatesMap.get("source");
        ResolvedLocationDto destination = coordinatesMap.get("destination");
        double distanceKm = 4.3; // temp mocked
        int durationMin = 12;    // temp mocked
        // Step 3: Dynamic pricing and ETA
        List<VehicleOptionDto> options = new ArrayList<>();
        options.add(calculate(VehicleType.BIKE, distanceKm, durationMin, source));
        options.add(calculate(VehicleType.AUTO, distanceKm, durationMin, source));
        options.add(calculate(VehicleType.CAB_ECONOMY, distanceKm, durationMin, source));
        options.add(calculate(VehicleType.CAR_PREMIUM, distanceKm, durationMin, source));
        options.add(calculate(VehicleType.E_RICKSHAW, distanceKm, durationMin, source));

        return new RidePreviewDto(source, destination, distanceKm, durationMin, options);
    }

    private VehicleOptionDto calculate(VehicleType type, double km, int min, ResolvedLocationDto source) {
        double base = switch (type) {
            case BIKE -> 20;
            case AUTO -> 30;
            case E_RICKSHAW -> 25;
            case CAB_ECONOMY -> 50;
            case CAR_PREMIUM -> 70;
            default -> 40;
        };
        double price = base + km * 6 + min * 1.5;

        // Later: calculate real ETA from nearest driver location
        int eta = switch (type) {
            case BIKE -> 3;
            case AUTO -> 4;
            case CAB_ECONOMY -> 5;
            case CAR_PREMIUM -> 7;
            case E_RICKSHAW -> 2;

            default -> 6;
        };

        return new VehicleOptionDto(type, price, eta);
    }
}


