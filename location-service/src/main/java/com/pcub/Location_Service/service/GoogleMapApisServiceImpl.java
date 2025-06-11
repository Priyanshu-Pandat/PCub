package com.pcub.Location_Service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcub.Location_Service.DTOs.ResolvedLocationDto;
import com.pcub.Location_Service.DTOs.SuggestionDto;
import com.pcub.Location_Service.exception.LocationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleMapApisServiceImpl implements GoogleMapApisService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public List<SuggestionDto> getPlaceSuggestions(String input) {
        try {
            String location = "26.9124,75.7873"; //Jaipur lat or long
            int radius = 30000;

            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + URLEncoder.encode(input, StandardCharsets.UTF_8)
                    + "&location=" + location
                    + "&radius=" + radius
                    + "&components=country:in"
                    + "&key=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode predictions = root.path("predictions");

            List<SuggestionDto> suggestions = new ArrayList<>();
            for (JsonNode prediction : predictions) {
                SuggestionDto dto = new SuggestionDto();
                dto.setDescription(prediction.path("description").asText());
                dto.setPlaceId(prediction.path("place_id").asText());
                dto.setMainText(prediction.path("structured_formatting").path("main_text").asText());
                dto.setSecondaryText(prediction.path("structured_formatting").path("secondary_text").asText());
                suggestions.add(dto);
            }

            return suggestions;
        } catch (Exception e) {
            throw new LocationServiceException("Failed to fetch suggestions from Google API", 500);
        }
    }

    public ResolvedLocationDto resolvePlaceIdToCoordinates(String placeId) {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/details/json"
                    + "?place_id=" + URLEncoder.encode(placeId, StandardCharsets.UTF_8)
                    + "&key=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode locationNode = root.path("result").path("geometry").path("location");

            if (locationNode.isMissingNode()) {
                throw new LocationServiceException("Coordinates not found for the given place_id", 404);
            }

            return new ResolvedLocationDto(
                    placeId,
                    locationNode.path("lat").asDouble(),
                    locationNode.path("lng").asDouble()
            );
        } catch (LocationServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new LocationServiceException("Failed to resolve place_id to coordinates", 500);
        }
    }
}
