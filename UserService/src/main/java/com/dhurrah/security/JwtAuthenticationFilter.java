//package com.dhurrah.security;
//
//import com.dhurrah.jwtutil.JWTUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JWTUtil jwtUtil;
//
//    private static final String[] PUBLIC_URLS = {
//            "/api/otp",
//            "/api/otp/",
//            "/api/otp/**",
//            "/v3/api-docs",
//            "/v3/api-docs/**",
//            "/v2/api-docs",
//            "/swagger-resources",
//            "/swagger-resources/**",
//            "/swagger-ui",
//            "/swagger-ui/**",
//            "/swagger-ui.html",
//            "/webjars/**"
//    };
//
//    private boolean isPublic(String uri) {
//        for (String publicUrl : PUBLIC_URLS) {
//            if (uri.startsWith(publicUrl.replace("**", ""))) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String uri = request.getRequestURI();
//
//        if (isPublic(uri)) {
//            filterChain.doFilter(request, response); // ✅ Allow public requests
//            return;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            if (!jwtUtil.validateToken(token)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Invalid Token ❌");
//                return;
//            }
//
//            String phone = jwtUtil.extractPhone(token);
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    phone, null, new ArrayList<>());
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Missing Token 🔒");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
