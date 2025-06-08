package com.driverService.jwtutil;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


@Component
public class JWTUtil {

    // You can generate a better secret key using Keys.secretKeyFor(SignatureAlgorithm.HS512)
    @Value("${jwt.secret}")
    private String secretKey;
    private SecretKey secret;

    @PostConstruct
    public void init() {
        this.secret = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    private final long expiryMs = 24 * 60 * 60 * 1000; // 24 hours

    public String generateToken(String number, Integer userId) {
        return Jwts.builder()
                .setSubject(number) // This is standard
                .claim("userId", userId) // Custom claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(secret, SignatureAlgorithm.HS512)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret) // Use the 'secret' Key directly
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractPhone(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret) // Use the 'secret' Key directly
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
