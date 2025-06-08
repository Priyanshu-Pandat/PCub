//package com.dhurrah.controller;
//
//import com.dhurrah.entity.User;
//import com.dhurrah.exceptions.UserException;
//import com.dhurrah.jwtutil.JWTUtil;
//import com.dhurrah.repositores.UserRepo;
//import com.dhurrah.service.UserService;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth/google")
//@Log4j2
//public class GoogleOauthController {
//    @Autowired
//    private JWTUtil jwtUtil;
//    @Autowired
//    private UserRepo userRepo;
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/success")
//    public void googleSuccess(OAuth2AuthenticationToken authenticationToken, HttpServletResponse response) throws IOException{
//        log.info("you are in success:{}",authenticationToken);
//        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
//        String email = (String) attributes.get("email");
//        String name = (String) attributes.get("name");
//        User user = userRepo.findByEmail(email).orElseThrow(
//                () -> new UserException("User not found with ID: " , "USER_NOT_FOUND"));
//
//        boolean isNewUser = false;
//            if (user == null) {
//                // Create new user in DB
//                isNewUser = true ;
//                user = new User();
//                user.setEmail(email);
//                user.setName(name);
//                userRepo.save(user);
//            }
//        String jwt = jwtUtil.generateToken(email, Math.toIntExact(user.getUserId()));
//        String encodedToken = URLEncoder.encode(jwt, StandardCharsets.UTF_8);
//        response.sendRedirect("http://localhost:3000/google-success?token=" + encodedToken + "&isNewUser=" + isNewUser);
//        }
//    @GetMapping("/auth/google/failure")
//    public void googleFailure(HttpServletResponse response) throws IOException {
//        System.out.println("❌ Google Login Failed!");
//        response.sendRedirect("http://localhost:3000/google-failed");
//    }
//
//}
//
//
