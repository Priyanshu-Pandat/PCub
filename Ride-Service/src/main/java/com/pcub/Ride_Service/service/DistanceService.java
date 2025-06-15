package com.pcub.Ride_Service.service;

import com.pcub.Ride_Service.dtos.DistanceDurationDto;
import com.pcub.Ride_Service.exception.DistanceServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DistanceService {

    @Value("${ors.api.key}")
    private String orsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Get distance in kilometers using ORS API
     */
    public DistanceDurationDto getDistanceAndDuration(double lat1, double lon1, double lat2, double lon2) {
        try {
            String url = String.format(
                    "https://api.openrouteservice.org/v2/directions/driving-car?start=%f,%f&end=%f,%f",
                    lon1, lat1, lon2, lat2
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", orsApiKey);
            headers.set("Accept", String.valueOf(MediaType.parseMediaType("application/geo+json"))); // ✅ Important

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            log.info("Calling ORS distance API with URL: {}", url);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new DistanceServiceException("ORS response invalid or failed", response.getStatusCodeValue());
            }

            List<Map<String, Object>> features = (List<Map<String, Object>>) response.getBody().get("features");
            if (features == null || features.isEmpty()) {
                throw new DistanceServiceException("No features in ORS response", 400);
            }

            Map<String, Object> properties = (Map<String, Object>) features.get(0).get("properties");
            List<Map<String, Object>> segments = (List<Map<String, Object>>) properties.get("segments");

            if (segments == null || segments.isEmpty()) {
                throw new DistanceServiceException("No segments in ORS response", 400);
            }

            Map<String, Object> summary = (Map<String, Object>) segments.get(0);
            double distanceInMeters = ((Number) summary.get("distance")).doubleValue();
            double durationInSeconds = ((Number) summary.get("duration")).doubleValue();

            double distanceKm = distanceInMeters / 1000.0;
            int durationMin = (int) Math.ceil(durationInSeconds / 60.0);

            log.info("Parsed distance: {} km, duration: {} min", distanceKm, durationMin);
            return new DistanceDurationDto(distanceKm, durationMin);

        } catch (DistanceServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception while calling ORS API", e);
            throw new DistanceServiceException("Failed to get distance/duration from ORS", e);
        }
    }
}