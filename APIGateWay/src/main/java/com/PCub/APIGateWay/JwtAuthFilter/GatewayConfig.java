package com.PCub.APIGateWay.JwtAuthFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/user/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://USER-SERVICE"))

                .route("driver-service", r -> r.path("/driver/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://DRIVER-SERVICE"))

                .route("public-routes", r -> r.path("/auth/google/**", "/api/otp/**")
                        .uri("lb://USER-SERVICE"))
                .route("location-service", r -> r.path("/location/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://LOCATION-SERVICE"))
                .route("ride-service", r -> r.path("/ride/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://RIDE-SERVICE"))
                .build();

    }
}

