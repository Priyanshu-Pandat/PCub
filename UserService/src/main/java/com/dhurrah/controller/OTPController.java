package com.dhurrah.controller;

import com.dhurrah.entity.User;
import com.dhurrah.jwtutil.JWTUtil;
import com.dhurrah.model.LoginResponse;
import com.dhurrah.model.OTPRequest;
import com.dhurrah.model.VerifyOTP;
import com.dhurrah.repositores.UserRepo;
import com.dhurrah.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/api/otp")
@Tag(name = "Authentication", description = "OTP APIs") // Swagger annotation
@Log4j2
public class OTPController {
    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/send")
    @Operation(summary = "Send OTP") // Swagger annotation
    public ResponseEntity<?> sendOtp(@Valid  @RequestBody OTPRequest req) {
       @Valid String identifier = req.getIdentifier();
        log.info("controller me req aa gyi : {} ",req.getIdentifier());
      String otp =   otpService.sendOtp(identifier);

        return ResponseEntity.ok(Map.of("success",true ,
                "message","OTP Sent SuccessFully and the otp is :" + otp));

    }

    @PostMapping(value = "/verify",produces = "application/json")
    @Operation(summary = "Verify OTP") // Swagger annotation
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOTP req) {
        @Valid String identifier = req.getIdentifier();
        @Valid String otp = req.getOtp();
        boolean isVerified = otpService.verifyOtp(identifier, otp);

        if (!isVerified) {
            log.info("OTP verification failed for identifier: {}", req.getIdentifier());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
        }

        Optional<User> userOpt = userRepo.findByPhoneNumber(req.getIdentifier());
        if (userOpt.isEmpty()) {
            userOpt = userRepo.findByEmail(req.getIdentifier()); // Check by email too
        }
        User user;
        boolean isNewUser = false;

        if (userOpt.isEmpty()) {
            user = new User();
            if (req.getIdentifier().contains("@")) {
                user.setEmail(req.getIdentifier());
            } else {
                user.setPhoneNumber(req.getIdentifier());
            }

            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepo.save(user);
            isNewUser = true;
        } else {
            user = userOpt.get();
        }
        log.info("User login success. Phone: {}, isNewUser: {}", req.getIdentifier(), isNewUser);

        String token = jwtUtil.generateToken(req.getIdentifier(), Math.toIntExact(user.getUserId()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginResponse(token, user.isProfileCompleted()));

    }
}
