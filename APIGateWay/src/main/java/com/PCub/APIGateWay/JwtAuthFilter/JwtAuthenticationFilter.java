//package com.PCub.APIGateWay.JwtAuthFilter;
//
//import com.PCub.APIGateWay.JwtUtil.JwtUtil;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.JwtException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter implements GatewayFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // Public endpoints accessible without token
//    private static final List<String> openPaths = List.of(
//            "/auth/google/**",
//            "/user/api/otp/send",
//            "/user/api/otp/verify",
//            "/driver/verifyOtp",
//            "/driver/sendOtp",
//            "/user/api/otp",
//            "/user/api/otp/",
//            "/user/api/otp/**",
//            "/v3/api-docs",
//            "/v3/api-docs/**",
//            "/v2/api-docs",
//            "/swagger-resources",
//            "/swagger-resources/**",
//            "/swagger-ui",
//            "/swagger-ui/**",
//            "/swagger-ui.html",
//            "/webjars/**",
//            "/ws**",
//            "/ws/driver"
//    );
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getPath().toString();
//
//        // If public path, skip token validation
//        if (openPaths.stream().anyMatch(path::startsWith)) {
//            return chain.filter(exchange);
//        }
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//        String token = extractToken(authHeader);
//
//        if (token == null) {
//            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
//        }
//
//        try {
//            if (!jwtUtil.validateToken(token)) {
//                System.out.println("JWT Validation failed for token: " + token);
//                return onError(exchange, "Invalid or Tampered Token", HttpStatus.UNAUTHORIZED);
//            }
//
//            Integer userId = jwtUtil.extractUserId(token);
//
//            // Properly mutate request and exchange to add header
//            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                    .header("X-User-Id", String.valueOf(userId))
//                    .build();
//
//            ServerWebExchange mutatedExchange = exchange.mutate()
//                    .request(mutatedRequest)
//                    .build();
//
//            return chain.filter(mutatedExchange);
//
//        } catch (ExpiredJwtException e) {
//            return onError(exchange, "Token Expired", HttpStatus.UNAUTHORIZED);
//        } catch (JwtException e) {
//            return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
//        } catch (Exception e) {
//            return onError(exchange, "Authentication Failed", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private String extractToken(String header) {
//        if (header != null && header.startsWith("Bearer ")) {
//            return header.substring(7);
//        }
//        return null;
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
//        exchange.getResponse().setStatusCode(status);
//        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
//
//        String responseBody = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.value());
//        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
//
//        return exchange.getResponse().writeWith(Mono.just(
//                exchange.getResponse().bufferFactory().wrap(bytes)
//        ));
//    }
//}
package com.PCub.APIGateWay.JwtAuthFilter;

import com.PCub.APIGateWay.JwtUtil.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Public paths that bypass token validation (supports wildcards via regex)
    private static final List<String> openPaths = List.of(
            "^/auth/google/.*",
            "^/user/api/otp/.*",
            "^/driver/sendOtp",
            "^/driver/verifyOtp",
            "^/v3/api-docs.*",
            "^/v2/api-docs.*",
            "^/swagger-resources.*",
            "^/swagger-ui.*",
            "^/webjars/.*",
            "^/ws.*" // WebSocket handshake endpoints (including /info, etc.)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String upgradeHeader = exchange.getRequest().getHeaders().getUpgrade();

        // ✅ Skip filtering for WebSocket handshake requests
        if ("websocket".equalsIgnoreCase(upgradeHeader)) {
            return chain.filter(exchange);
        }

        // ✅ Skip filtering for publicly allowed paths (based on regex)
        if (openPaths.stream().anyMatch(path::matches)) {
            return chain.filter(exchange);
        }

        // 🔐 Extract and validate Authorization token
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = extractToken(authHeader);

        if (token == null) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid or Tampered Token", HttpStatus.UNAUTHORIZED);
            }

            Integer userId = jwtUtil.extractUserId(token);

            // ✅ Add userId to request header for downstream microservices
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange);

        } catch (ExpiredJwtException e) {
            return onError(exchange, "Token Expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return onError(exchange, "Authentication Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔎 Extract token from Bearer header
    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // ❌ Generate a standard error response
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.value());
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(bytes)
        ));
    }
}

