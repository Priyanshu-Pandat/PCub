package com.pcub.Location_Service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Service
public class MapMyIndiaAuthService {

    @Value("${mapmyindia.client_id}")
    private String clientId;

    @Value("${mapmyindia.client_secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getAccessToken() {
        String redisKey = "mapmyindia:access_token";
        String token = redisTemplate.opsForValue().get(redisKey);
        if (token != null) return token;

        fetchNewToken();
        return redisTemplate.opsForValue().get(redisKey);
    }

    private void fetchNewToken() {
        String url = "https://apis.mappls.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !responseBody.containsKey("access_token")) {
                throw new RuntimeException("Failed to fetch access token from MapMyIndia: Invalid response");
            }

            String token = (String) responseBody.get("access_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in");

            // Cache token in Redis for slightly less than actual expiry
            redisTemplate.opsForValue().set("mapmyindia:access_token", token, Duration.ofSeconds(expiresIn - 60));
            System.out.println("✅ MapMyIndia token fetched successfully and cached in Redis.");
        } catch (Exception ex) {
            System.out.println("❌ Error while fetching MapMyIndia token: " + ex.getMessage());
            throw new RuntimeException("Error fetching token", ex);
        }
    }
}
