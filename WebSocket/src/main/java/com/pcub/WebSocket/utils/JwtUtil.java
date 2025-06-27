package com.pcub.WebSocket.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET;

    private JwtParser parser;

    @PostConstruct
    public void init() {
        parser = Jwts.parser().setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        try {
            parser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getDriverIdFromToken(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();
        return String.valueOf(claims.get("userId")); // or .getSubject()
    }
}
