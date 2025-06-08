package com.dhurrah.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;

////@Configuration
////
////public class SecurityConfig {
////
////    @Autowired
////    private JwtAuthenticationFilter jwtFilter;
////    @Autowired
////    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
////
////    private static final String[] SWAGGER_WHITELIST = {
////            "/api/otp/**",
////            "/v3/api-docs/**",
////            "/swagger-ui/**",
////            "/swagger-ui.html",
////            "/swagger-resources/**",
////            "/webjars/**",
////            "/favicon.ico",
////            "/auth/**",
////            "/oauth2/**",
////            "/login/**"
////    };
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .cors(Customizer.withDefaults())
////                .csrf(csrf -> csrf.disable())
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
////                        .anyRequest().authenticated()
////                )
////                .oauth2Login(oauth2 -> oauth2.loginPage("/login")
////                        .successHandler(oauth2LoginSuccessHandler) // Use custom success handler
////
////                )
////                .sessionManagement(session -> session
////                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ✅ allow session for OAuth2
////                )
////                .addFilterBefore(jwtFilter,
////                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
//}
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Sabhi requests ko allow karo
//                .csrf(csrf -> csrf.disable()) // CSRF disable karo (optional, depends on your use-case)
//                .httpBasic(Customizer.withDefaults()); // ya httpBasic hata bhi sakte ho agar nahi chahiye
//
//        return http.build();
//    }
//}
