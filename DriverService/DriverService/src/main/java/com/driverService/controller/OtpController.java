package com.driverService.controller;

import com.driverService.entity.DriverEntity;
import com.driverService.jwtutil.JWTUtil;
import com.driverService.model.LoginResponse;
import com.driverService.model.OtpDriverReq;
import com.driverService.model.VerifyOtpDTO;
import com.driverService.repository.DriverRepo;
import com.driverService.service.OtpService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/driver")
@Log4j2
public class OtpController {
    @Autowired
    private OtpService otpService;
     @Autowired
     private DriverRepo driverRepo;
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/sendOtp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody  OtpDriverReq otpDriverReq) {
        log.info("call driver controller");
        otpService.sendOtp(otpDriverReq.getNumber());
        return ResponseEntity.ok("OTP Sent SuccessFully");
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpDTO verifyOtp) {
        log.info("otp verify Controller calling ");
        boolean isVerified = otpService.verifyOtp(verifyOtp.getNumber(),verifyOtp.getOtp());
        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
        }
        Optional<DriverEntity> driverIsExist = driverRepo.findByNumber(verifyOtp.getNumber());
        DriverEntity newDriver;
        boolean isNewUser = false;
        if(driverIsExist.isEmpty()) {
            newDriver = new DriverEntity();
            isNewUser = true;
            newDriver.setNumber(verifyOtp.getNumber());
            newDriver.setCreatedAt(LocalDateTime.now());
            newDriver = driverRepo.save(newDriver);
        }
        else {
            newDriver = driverIsExist.get();
        }
        log.info("User login success. Phone: {}, isNewUser: {}", verifyOtp.getNumber(), isNewUser);
        String token = jwtUtil.generateToken(verifyOtp.getNumber(), Math.toIntExact(newDriver.getDriverId()));
        int currentStep = newDriver.getCurrentStep();

        return new ResponseEntity<>(new LoginResponse(token, isNewUser,currentStep), HttpStatus.OK);

    }
}
