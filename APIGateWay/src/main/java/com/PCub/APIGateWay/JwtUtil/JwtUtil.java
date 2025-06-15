package com.PCub.APIGateWay.JwtUtil;

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
    private  String SECRET ; // 🔐 Strong secret in prod
    private JwtParser parser;

    @PostConstruct
    public void init() {
        // now SECRET will be injected and not null
        parser = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            parser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get username (or email) from token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Get user ID (assuming stored as "userId" claim)
    public Integer extractUserId(String token) {
        Claims claims = extractClaims(token);
        return ((Number) claims.get("userId")).intValue();
    }

    private Claims extractClaims(String token) {
        return parser.parseClaimsJws(token).getBody();  // ✅ corrected here
    }
}
