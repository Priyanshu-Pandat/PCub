package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.RideServiceApplication.LocationServiceClient;
import com.pcub.Ride_Service.dtos.DistanceDurationDto;
import com.pcub.Ride_Service.dtos.ResolvedLocationDto;
import com.pcub.Ride_Service.dtos.FinalFareEstimateResponse;
import com.pcub.Ride_Service.dtos.FareWithVehicleAndReason;
import com.pcub.Ride_Service.exception.FareCalculationException;
import com.pcub.Ride_Service.exception.LocationServiceException;
import com.pcub.Ride_Service.exception.RideServiceException;
import com.pcub.Ride_Service.modals.ApiResponse;
import com.pcub.Ride_Service.modals.DynamicPricingConditions;
import com.pcub.Ride_Service.modals.FareCalculationResult;
import com.pcub.Ride_Service.modals.VehicleType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class RidePreviewServiceImpl implements RidePreviewService {

    private static final Logger logger = LoggerFactory.getLogger(RidePreviewServiceImpl.class);

    @Autowired
    private LocationServiceClient locationClient;
    @Autowired
    private DistanceService distanceService;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private ZoneService zoneService;

    @Override
    public FinalFareEstimateResponse generateRidePreview(String sourcePlaceId, String destinationPlaceId) {
        try {
            // Resolve coordinates
            ResponseEntity<ApiResponse<Map<String, ResolvedLocationDto>>> response =
                    locationClient.resolvePlaceIds(sourcePlaceId, destinationPlaceId);
            log.info("map api called and get response:{}", response);

            if (response.getBody() == null || response.getBody().getData() == null) {
                throw new LocationServiceException("Failed to resolve location coordinates", 502);
            }


           Map<String, ResolvedLocationDto> coordinatesMap = response.getBody().getData();
            //Map<String, ResolvedLocationDto> coordinatesMap = new HashMap<>();

       //from 56 to 62 are hardcoded and 54 also hardcoded
//            coordinatesMap.put("source", new ResolvedLocationDto(
//                    sourcePlaceId, 26.9239, 75.8267
//            ));
//            coordinatesMap.put("destination", new ResolvedLocationDto(
//                    destinationPlaceId, 26.9062, 75.8163
//            ));
            ResolvedLocationDto source = coordinatesMap.get("source");
            ResolvedLocationDto destination = coordinatesMap.get("destination");

            if (source == null || destination == null) {
                throw new LocationServiceException("Invalid source or destination coordinates", 400);
            }

            // Calculate distance and duration
            DistanceDurationDto dt = distanceService.getDistanceAndDuration(source.getLatitude(),source.getLongitude(),
                    destination.getLatitude(),destination.getLongitude());

            double distanceKm = dt.getDistanceKm();
            log.info("distance service called and get : {}",distanceKm);

            int durationMin = dt.getDurationMin();
            DynamicPricingConditions conditions = getDynamicConditions(source,destination);

            // Generate fare options for all vehicle types
            List<FareWithVehicleAndReason> options = new ArrayList<>();
            for (VehicleType vehicleType : VehicleType.values()) {
                try {
                    FareWithVehicleAndReason fareOption = calculateFareForVehicle(
                            vehicleType, distanceKm, durationMin, source,destination,conditions
                    );
                    options.add(fareOption);
                } catch (FareCalculationException e) {
                    throw e; // Re-throw fare calculation exceptions
                } catch (Exception e) {
                    logger.warn("Failed to calculate fare for vehicle type: {}", vehicleType, e);
                    logger.error("Fare calculation failed for vehicle {}: {}", vehicleType, e.getMessage());
                    continue;
                }
            }

            String generalReason = "Review fare details for pricing factors.";
            return new FinalFareEstimateResponse(source, destination, distanceKm, durationMin, options, generalReason);

        }  catch (LocationServiceException e) {
            // Re-throw location service exceptions
            throw e;
        } catch (Exception e) {
            logger.error("Error generating ride preview", e);
            throw new RideServiceException("Failed to generate ride preview: " + e.getMessage(), 500, e);
        }
    }

    /**
     * Calculate fare for a specific vehicle type with dynamic pricing
     */
    private FareWithVehicleAndReason calculateFareForVehicle(VehicleType type, double km, int min, ResolvedLocationDto source,
                                                             ResolvedLocationDto destination,DynamicPricingConditions conditions) {
        // Base fare calculation
        double baseFare = getBaseFare(type);
        double perKmRate = getPerKmRate(type);
        double perMinRate = getPerMinRate(type);
        double standardFare = baseFare + (km * perKmRate) + (min * perMinRate);



        // Apply surcharges and get reasons
        FareCalculationResult result = applySurcharges(standardFare, conditions);

        // Apply minimum fare constraint
        double minimumFare = getMinimumFare(type);
        double finalFare = Math.max(result.getFinalFare(), minimumFare);

        // Add minimum fare reason if applicable
        List<String> finalReasons = new ArrayList<>(result.getReasons());
        if (finalFare > result.getFinalFare()) {
            finalReasons.add("Minimum Fare Applied");
        }
        int estimatedPrice = (int) Math.round(finalFare);
        int eta = getEtaForVehicle(type);

        return new FareWithVehicleAndReason(type, (int)finalFare, eta, finalReasons);
    }

    /**
     * Get all dynamic pricing conditions for the given location
     */
    private DynamicPricingConditions getDynamicConditions(ResolvedLocationDto source,ResolvedLocationDto destination) {
        try {
            boolean isRainy = weatherService.isBadWeather(source.getLatitude(), source.getLongitude()) ||
                              weatherService.isBadWeather(destination.getLatitude(), destination.getLongitude());

            log.info("weather service called with response:{}",isRainy);
            boolean isZoneSurcharge = zoneService.isExpensiveZone(source.getLatitude(), source.getLongitude()) ||
                                      zoneService.isExpensiveZone(destination.getLatitude(), destination.getLongitude());

            boolean isNight = zoneService.isNightTime();
            boolean isPeakHour = zoneService.isPeakTime();

            return new DynamicPricingConditions(isRainy, isZoneSurcharge, isNight, isPeakHour);
        } catch (Exception e) {
            logger.warn("Failed to get dynamic conditions, using defaults", e);
            return new DynamicPricingConditions(false, false, false, false);
        }
    }

    /**
     * Apply cumulative surcharges and track reasons
     */
    private FareCalculationResult applySurcharges(double initialFare, DynamicPricingConditions conditions) {
        double multiplier = 1.0;
        List<String> reasons = new ArrayList<>();

        if (conditions.isRainy()) {
            multiplier += 0.11; // 20% extra for rain
            reasons.add("Weather Surcharge");
        }

        if (conditions.isZoneSurcharge()) {
            multiplier += 0.15; // 18% extra for special zones
            reasons.add("Zone Surcharge");
        }

        if (conditions.isNight()) {
            multiplier += 0.15; // 20% extra for night
            reasons.add("Night Charge");
        }

        if (conditions.isPeakHour()) {
            multiplier += 0.07; // 7% extra for peak hours
            reasons.add("Peak Hour Charge");
        }

        if (reasons.isEmpty()) {
            reasons.add("Standard Fare");
        }

        double finalFare = initialFare * multiplier;
        double adjustedFare = applyPsychologicalPricing(finalFare);


        return new FareCalculationResult((int)adjustedFare, reasons);
    }

    // Pricing configuration methods
    private double getMinimumFare(VehicleType type) {
        return switch (type) {
            case BIKE -> 22.0;
            case AUTO, E_RICKSHAW -> 30.0;
            case CAB_ECONOMY -> 50.0;
            case CAR_PREMIUM -> 90.0;
        };
    }

    private double getBaseFare(VehicleType type) {
        return switch (type) {
            case BIKE -> 15.0;
            case AUTO -> 20.0;
            case E_RICKSHAW -> 17.0;
            case CAB_ECONOMY -> 35.0;
            case CAR_PREMIUM -> 50.0;
        };
    }

    private double getPerKmRate(VehicleType type) {
        return switch (type) {
            case BIKE -> 5.0;
            case  E_RICKSHAW -> 5.5;
            case AUTO -> 6.0;
            case CAB_ECONOMY -> 9.0;
            case CAR_PREMIUM -> 12.0;
        };
    }

    private double getPerMinRate(VehicleType type) {
        return switch (type) {
            case BIKE -> 0.6;
            case AUTO, E_RICKSHAW -> 0.7;
            case CAB_ECONOMY -> 1.0;
            case CAR_PREMIUM -> 1.2;
        };
    }

    private int getEtaForVehicle(VehicleType type) {
        return switch (type) {
            case BIKE -> 3;
            case AUTO -> 4;
            case CAB_ECONOMY -> 5;
            case CAR_PREMIUM -> 7;
            case E_RICKSHAW -> 2;
        };
    }
    private int applyPsychologicalPricing(double originalPrice) {
        int rounded = (int) originalPrice;
        if (originalPrice % 10 == 9) {
            return (int) originalPrice; // already ends with 9
        }
        int reduced = (int) (originalPrice - 1);
        if (reduced % 10 == 9) {
            return reduced; // like 100 → 99
        }
        return (int) originalPrice; // don't reduce if not safe
}
}