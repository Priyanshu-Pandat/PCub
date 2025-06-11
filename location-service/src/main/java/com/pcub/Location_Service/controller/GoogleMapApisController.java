package com.pcub.Location_Service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcub.Location_Service.DTOs.ResolvedLocationDto;
import com.pcub.Location_Service.DTOs.SuggestionDto;
import com.pcub.Location_Service.modal.ApiResponse;
import com.pcub.Location_Service.service.GoogleMapApisService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/location")
public class GoogleMapApisController {

    @Value("${google.maps.api.key}")
    private String apiKey;
    @Autowired
    private  GoogleMapApisService googleMapApisService;


    // method for autosuggestions
    @GetMapping("/autoSearch")
    public ResponseEntity<ApiResponse<List<SuggestionDto>>> getSuggestions(@RequestParam @NotBlank String input) {
        log.info("Fetching suggestions for input: {}", input);

        List<SuggestionDto> suggestions = googleMapApisService.getPlaceSuggestions(input);
        return ResponseEntity.ok(new ApiResponse<>(true, suggestions, "Suggestions fetched successfully"));
    }


    @GetMapping("/resolvePlaceId")
    public ResponseEntity<ApiResponse<Map<String, ResolvedLocationDto>>> resolvePlaceIdToLatLng(
            @RequestParam @NotBlank String sourcePlaceId,
            @RequestParam @NotBlank String destinationPlaceId) {

        log.info("Fetching lat/lng for sourcePlaceId={} and destinationPlaceId={}", sourcePlaceId, destinationPlaceId);

        ResolvedLocationDto sourceCoordinates = googleMapApisService.resolvePlaceIdToCoordinates(sourcePlaceId);
        ResolvedLocationDto destinationCoordinates = googleMapApisService.resolvePlaceIdToCoordinates(destinationPlaceId);

        Map<String, ResolvedLocationDto> coordinatesMap = new HashMap<>();
        coordinatesMap.put("source", sourceCoordinates);
        coordinatesMap.put("destination", destinationCoordinates);

        return ResponseEntity.ok(
                new ApiResponse<>(true, coordinatesMap, "Coordinates resolved successfully for both source and destination")
        );
    }
}
