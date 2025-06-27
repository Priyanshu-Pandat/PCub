package com.pcub.WebSocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideSessionService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getRideIdForDriver(Long driverId) {
        return (String) redisTemplate.opsForHash().get("driver:ride-map", driverId.toString());
    }
}
