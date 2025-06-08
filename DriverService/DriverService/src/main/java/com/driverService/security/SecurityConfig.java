//package com.driverService.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//public class SecurityConfig {
//    @Autowired
//    private JwtAuthenticationFilter jwtFilter;
//
//
//    private static final String[] SWAGGER_WHITELIST = {
//            "/driver/sendOtp/**",
//            "/driver/verify/otp",
//            "/v3/api-docs/**",
//            "/swagger-ui/**",
//            "/swagger-ui.html",
//            "/swagger-resources/**",
//            "/webjars/**",
//            "/favicon.ico",
//            "/auth/**",
//            "/oauth2/**",
//            "/login/**"
//    };
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
//                        .anyRequest().authenticated()
//                )
//
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ✅ allow session for OAuth2
//                )
//                .addFilterBefore(jwtFilter,
//                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
//
