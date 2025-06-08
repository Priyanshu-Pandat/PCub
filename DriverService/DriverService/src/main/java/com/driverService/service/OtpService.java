package com.driverService.service;


import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class OtpService {
//    @Autowired
//    private OtpRepo otpRepo;
//    private final int EXPIRE_MINUTES = 1;
//    @Transactional
//    public void sendOtp(String number) {
//        String otp = String.valueOf(new Random().nextInt(8999) + 1000);
//
//        log.info("Generated OTP for {}: {}", number, otp);
//        otpRepo.deleteByNumber(number);
//
//        otpRepo.save(new Otp(number,otp));
//    }
//    public boolean verifyOtp(String number, String driverOtp) {
//        Optional<Otp> otpOptional = otpRepo.findByNumber(number);
//        log.info("this is work : {}", otpOptional);
//        if(otpOptional.isPresent()){
//            Otp savedOtp = otpOptional.get();
//            boolean isExpired = Duration.between(savedOtp.getCreatedAt(), LocalDateTime.now()).toMinutes() > EXPIRE_MINUTES;
//            boolean isMatch = savedOtp.getOtp().equals(driverOtp);
//            log.info("OTP verification for {}: expired={}, match={}", number, isExpired, isMatch);
//            return !isExpired && isMatch ;
//        }
//        return false ;
//
//    }
@Autowired
private RedisTemplate<String, String> redisTemplate;

    private static final int OTP_EXPIRE_MINUTES = 1;

    public void sendOtp(String number) {
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);
        log.info("Generated OTP for {}: {}", number, otp);

        // Save to Redis with expiry
        String redisKey = "OTP:" + number;
        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public boolean verifyOtp(String number, String driverOtp) {
        String redisKey = "OTP:" + number;
        String savedOtp = redisTemplate.opsForValue().get(redisKey);

        if (savedOtp == null) {
            log.info("OTP for {} expired or not found", number);
            return false;
        }

        boolean match = savedOtp.equals(driverOtp);
        log.info("OTP match status for {}: {}", number, match);
        return match;
    }
}
