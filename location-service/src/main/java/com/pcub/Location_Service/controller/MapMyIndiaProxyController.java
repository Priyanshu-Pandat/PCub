package com.pcub.Location_Service.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcub.Location_Service.service.MapMyIndiaAuthService;
import com.pcub.Location_Service.modal.ApiResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/location")
public class MapMyIndiaProxyController {

    @Autowired
    private MapMyIndiaAuthService authService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mapmyindia.restKey}")
    private String restKey;


    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam("query") String query) {
        log.info("📍 Autocomplete API called with query: '{}'", query);

        if (query == null || query.trim().isEmpty()) {
            log.warn("❗ Empty or null query received.");
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Query parameter 'query' is required"));
        }

        String token = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = "https://atlas.mapmyindia.com/api/places/search/json?query=" +
                UriUtils.encode(query, StandardCharsets.UTF_8);

        try {
            log.info("🔍 Trying original query: {}", query);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String body = response.getBody();

            if (body != null && !body.contains("\"suggestedLocations\":[]")) {
                log.info("✅ Results found for original query.");
                return ResponseEntity.ok(new ApiResponse<>(true, body, "Success"));
            }
        } catch (Exception e) {
            log.error("❌ Error with original query '{}': {}", query, e.getMessage());
        }

        // Fallback: remove all spaces
        String fallbackQuery = query.replaceAll("\\s+", "");
        if (!fallbackQuery.equalsIgnoreCase(query)) {
            String fallbackUrl = "https://atlas.mapmyindia.com/api/places/search/json?query=" +
                    UriUtils.encode(fallbackQuery, StandardCharsets.UTF_8);
            try {
                log.info("🔄 Trying fallback query: {}", fallbackQuery);
                ResponseEntity<String> fallbackResponse = restTemplate.exchange(fallbackUrl, HttpMethod.GET, request, String.class);
                String fallbackBody = fallbackResponse.getBody();

                if (fallbackBody != null && !fallbackBody.contains("\"suggestedLocations\":[]")) {
                    log.info("✅ Results found with fallback query.");
                    return ResponseEntity.ok(new ApiResponse<>(true, fallbackBody, "Fallback query used"));
                }
            } catch (Exception e) {
                log.error("❌ Error with fallback query '{}': {}", fallbackQuery, e.getMessage());
            }
        }

        log.warn("🚫 No results for both original and fallback queries.");
        return ResponseEntity.ok(new ApiResponse<>(true, "{\"suggestedLocations\":[]}", "No results found"));
    }


    @GetMapping("/reverse")
    public ResponseEntity<?> reverse(@RequestParam double lat, @RequestParam double lng) {
        String token = authService.getAccessToken();
        String url = String.format("https://apis.mapmyindia.com/advancedmaps/v1/%s/rev_geocode?lat=%f&lng=%f", token, lat, lng);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return ResponseEntity.ok(new ApiResponse<>(true, response.getBody(), "Reverse geocode success"));
        } catch (Exception e) {
            log.error("Error in reverse geocoding: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Reverse geocoding failed"));
        }
    }


    @GetMapping("/eLoc-details")
    public ResponseEntity<?> getLatLongFromELoc(@RequestParam("sourceELoc") String eLoc1S,
                                                @RequestParam("destinationELoc") String eLoc2D) {
        log.info("Getting coordinates for sourceELoc: {} and destinationELoc: {}", eLoc1S, eLoc2D);

        try {
            // Step 1: Get OAuth token for eLoc API calls
            String token = authService.getAccessToken(); // OAuth token for eLoc APIs
            log.info("Token: {}", token);

            // URLs to get address from eLoc (use token here)
            String sourceELocUrl = "https://explore.mappls.com/apis/O2O/entity/" + eLoc1S;
            String destinationELocUrl = "https://explore.mappls.com/apis/O2O/entity/" + eLoc2D;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Call eLoc APIs to get addresses
            ResponseEntity<String> responseForSource = restTemplate.exchange(sourceELocUrl, HttpMethod.GET, entity, String.class);
            ResponseEntity<String> responseForDestination = restTemplate.exchange(destinationELocUrl, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode sourceNode = mapper.readTree(responseForSource.getBody());
            JsonNode destinationNode = mapper.readTree(responseForDestination.getBody());

            String sourceAddress = sourceNode.path("address").asText();
            String destinationAddress = destinationNode.path("address").asText();

            log.info("Cleaned Source address: {}", sourceAddress);
            log.info("Cleaned Destination address: {}", destinationAddress);

            // Step 2: Use Forward Geocode API to get lat/lng from address
            // NOTE: Use restKey (your API key) here in the URL, NOT token
            String sourceGeoUrl = "https://apis.mappls.com/advancedmaps/v1/" + restKey + "/geo_code?addr=" + UriUtils.encode(sourceAddress, StandardCharsets.UTF_8);
            String destinationGeoUrl = "https://apis.mappls.com/advancedmaps/v1/" + restKey + "/geo_code?addr=" + UriUtils.encode(destinationAddress, StandardCharsets.UTF_8);

            // No Authorization header needed for this API (just key in URL)
            ResponseEntity<String> geoResponseSource = restTemplate.getForEntity(sourceGeoUrl, String.class);
            ResponseEntity<String> geoResponseDestination = restTemplate.getForEntity(destinationGeoUrl, String.class);

            JsonNode geoSourceNode = mapper.readTree(geoResponseSource.getBody());
            JsonNode geoDestinationNode = mapper.readTree(geoResponseDestination.getBody());

            // Extract lat & lng from forward geocode results
            JsonNode sourceResult = geoSourceNode.path("results").get(0);
            JsonNode destinationResult = geoDestinationNode.path("results").get(0);

            String sourceLat = sourceResult.path("lat").asText();
            String sourceLng = sourceResult.path("lng").asText();
            String destinationLat = destinationResult.path("lat").asText();
            String destinationLng = destinationResult.path("lng").asText();

            Map<String, Object> result = new HashMap<>();
            result.put("sourceAddress", sourceAddress);
            result.put("destinationAddress", destinationAddress);
            result.put("sourceLat", sourceLat);
            result.put("sourceLng", sourceLng);
            result.put("destinationLat", destinationLat);
            result.put("destinationLng", destinationLng);

            log.info("✅ Successfully fetched lat/lng for both eLocs");

            return ResponseEntity.ok(new ApiResponse<>(true, result, "eLoc details with lat/lng retrieved"));

        } catch (Exception e) {
            log.error("❌ Failed to process eLoc or geocode API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to process eLoc or geocode API"));
        }
    }
}