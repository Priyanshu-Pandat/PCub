//package com.dhurrah.security;
//
//import com.dhurrah.entity.User;
//import com.dhurrah.exceptions.UserException;
//import com.dhurrah.jwtutil.JWTUtil;
//import com.dhurrah.repositores.UserRepo;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.log4j.Log4j2;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//@Log4j2
//@Component
//public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Autowired
//    private JWTUtil jwtUtil;
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//        OAuth2User oauthUser = oauthToken.getPrincipal();
//        log.info("OAuth2 user attributes: {}", oauthUser.getAttributes());
//
//        String email = (String) oauthUser.getAttributes().get("email");
//        String name = (String) oauthUser.getAttributes().get("name");
//
//        User user = userRepo.findByEmail(email).orElse(null);
//        boolean isNewUser = false;
//
//        if (user == null) {
//            user = new User();
//            user.setEmail(email);
//            user.setName(name);
//            userRepo.save(user);
//            isNewUser = true;
//        }
//
//        String jwt = jwtUtil.generateToken(email, Math.toIntExact(user.getUserId()));
//        String encodedToken = URLEncoder.encode(jwt, StandardCharsets.UTF_8);
//
//        // Change URL as per your frontend port and path
//        response.sendRedirect("http://localhost:3000/google-success?token=" + encodedToken + "&isNewUser=" + isNewUser);
//    }
//}
