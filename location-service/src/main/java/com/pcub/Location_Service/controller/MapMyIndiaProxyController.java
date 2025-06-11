//package com.pcub.Location_Service.controller;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pcub.Location_Service.service.MapMyIndiaAuthService;
//import com.pcub.Location_Service.modal.ApiResponse;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//import org.springframework.web.util.UriUtils;
//
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Slf4j
//@RestController
//@RequestMapping("/location")
//public class MapMyIndiaProxyController {
//
//    @Autowired
//    private MapMyIndiaAuthService authService;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${mapmyindia.restKey}")
//    private String restKey;
//  //  String token = "f727f23d-fc94-4142-8451-b0ed73d6d663";
//
//
//    @GetMapping("/autocomplete")
//    public ResponseEntity<?> autocomplete(@RequestParam("query") String query) {
//        log.info("📍 Autocomplete API called with query: '{}'", query);
//
//        if (query == null || query.trim().isEmpty()) {
//            log.warn("❗ Empty or null query received.");
//            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Query parameter 'query' is required"));
//        }
//
//        String token = authService.getAccessToken();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        String url = "https://atlas.mapmyindia.com/api/places/search/json?query=" +
//                UriUtils.encode(query, StandardCharsets.UTF_8);
//
//        try {
//            log.info("🔍 Trying original query: {}", query);
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//            String body = response.getBody();
//
//            if (body != null && !body.contains("\"suggestedLocations\":[]")) {
//                log.info("✅ Results found for original query.");
//                return ResponseEntity.ok(new ApiResponse<>(true, body, "Success"));
//            }
//        } catch (Exception e) {
//            log.error("❌ Error with original query '{}': {}", query, e.getMessage());
//        }
//
//        // Fallback: remove all spaces
//        String fallbackQuery = query.replaceAll("\\s+", "");
//        if (!fallbackQuery.equalsIgnoreCase(query)) {
//            String fallbackUrl = "https://atlas.mapmyindia.com/api/places/search/json?query=" +
//                    UriUtils.encode(fallbackQuery, StandardCharsets.UTF_8);
//            try {
//                log.info("🔄 Trying fallback query: {}", fallbackQuery);
//                ResponseEntity<String> fallbackResponse = restTemplate.exchange(fallbackUrl, HttpMethod.GET, request, String.class);
//                String fallbackBody = fallbackResponse.getBody();
//
//                if (fallbackBody != null && !fallbackBody.contains("\"suggestedLocations\":[]")) {
//                    log.info("✅ Results found with fallback query.");
//                    return ResponseEntity.ok(new ApiResponse<>(true, fallbackBody, "Fallback query used"));
//                }
//            } catch (Exception e) {
//                log.error("❌ Error with fallback query '{}': {}", fallbackQuery, e.getMessage());
//            }
//        }
//
//        log.warn("🚫 No results for both original and fallback queries.");
//        return ResponseEntity.ok(new ApiResponse<>(true, "{\"suggestedLocations\":[]}", "No results found"));
//    }
//
//
//    @GetMapping("/reverse")
//    public ResponseEntity<?> reverse(@RequestParam double lat, @RequestParam double lng) {
//       String token = authService.getAccessToken();
//        String url = String.format("https://apis.mapmyindia.com/advancedmaps/v1/%s/rev_geocode?lat=%f&lng=%f", token, lat, lng);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//            return ResponseEntity.ok(new ApiResponse<>(true, response.getBody(), "Reverse geocode success"));
//        } catch (Exception e) {
//            log.error("Error in reverse geocoding: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, null, "Reverse geocoding failed"));
//        }
//    }
//
//
//    @GetMapping("/eLoc-details")
//    public ResponseEntity<?> getLatLongFromELocsUsingRestKey(
//            @RequestParam("sourceELoc") String eLoc1S,
//            @RequestParam("destinationELoc") String eLoc2D) {
//
//
//
//        RestTemplate restTemplate = new RestTemplate();
//
////        try {
////            String sourceUrl = "https://apis.mapmyindia.com/advancedmaps/v1/" + restKey + "/geoCode?eloc="  + eLoc1S;
////            String destUrl = "https://apis.mapmyindia.com/advancedmaps/v1/" + restKey + "/geoCode?eloc=" + eLoc1S;
////
////            ResponseEntity<Map> sourceResponse = restTemplate.getForEntity(sourceUrl, Map.class);
////            ResponseEntity<Map> destResponse = restTemplate.getForEntity(destUrl, Map.class);
////
////            Map<String, Object> result = new HashMap<>();
////            result.put("source", sourceResponse.getBody());
////            result.put("destination", destResponse.getBody());
////
////            return ResponseEntity.ok(result);
////
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                    .body("Error fetching lat/long from eLocs: " + e.getMessage());
////        }
//        Map<String,String> mp = new HashMap<>();
//        mp.put("Source","lat = 26.3434,long=45.5543");
//        mp.put("destination","lat = 26.3434,long=45.5543");
//        return new ResponseEntity<>(mp,HttpStatus.OK);
//
//    }
//
//}