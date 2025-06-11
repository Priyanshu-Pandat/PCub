package com.pcub.Location_Service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

import org.springframework.http.*;

@Slf4j
@Service
public class MapMyIndiaAuthService {

    @Value("${mapmyindia.client.id}")
    private String clientId;

    @Value("${mapmyindia.client.secret}")
    private String clientSecret;
    private final String authUrl = "https://outpost.mapmyindia.com/api/security/oauth/token";


    private final String redisTokenKey = "mapmyindia:access_token";

    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public MapMyIndiaAuthService(RestTemplateBuilder restTemplateBuilder,
                                 StringRedisTemplate redisTemplate,
                                 ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public String getAccessToken() {
        String cachedToken = redisTemplate.opsForValue().get(redisTokenKey);
        if (cachedToken != null) {
            log.info("✅ Token fetched from Redis");
            return cachedToken;
        }

        log.info("🔄 Token not found in Redis. Requesting new token from MapmyIndia...");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            int expiresIn = jsonNode.get("expires_in").asInt();

            redisTemplate.opsForValue().set(redisTokenKey, accessToken, Duration.ofSeconds(expiresIn));

            log.info("✅ New token generated and cached in Redis");
            return accessToken;

        } catch (Exception e) {
            log.error("❌ Failed to parse token response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse MapmyIndia token response", e);
        }
    }
}
