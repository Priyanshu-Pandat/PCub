package com.dhurrah.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Log4j2
public class OtpService {
//    @Autowired
//    private OtpRepository otpRepository;

       @Autowired
       private JavaMailSender mailSender; // Injected mail sender
//
//
//    private final int EXPIRE_MINUTES = 1;
//
//
//    @Transactional
//    public void sendOtp(String identifier) {
//        String otp = String.valueOf(new Random().nextInt(8999) + 1000);
//
//        log.info("Generated OTP for {}: {}", identifier, otp);
//        // Log OTP for debugging
//        otpRepository.deleteByIdentifier(identifier);
//        otpRepository.save(new Otp(identifier, otp));
//        if (isEmail(identifier)) {
//            sendOtpEmail(identifier, otp);
//        }
//    }
//
//        public boolean verifyOtp(String identifier, String userOtp) {
//
//        Optional<Otp> otpOptional = otpRepository.findTopByIdentifierOrderByCreatedAtDesc(identifier);
//       log.info("this is work : {}", otpOptional);
//            if (otpOptional.isPresent()) {
//                Otp savedOtp = otpOptional.get();
//
//                boolean isExpired = Duration.between(savedOtp.getCreatedAt(), LocalDateTime.now()).toMinutes() > EXPIRE_MINUTES;
//                boolean isMatch = savedOtp.getOtp().equals(userOtp);
//
//                log.info("OTP verification for {}: expired={}, match={}", identifier, isExpired, isMatch);
//
//                return !isExpired && isMatch;
//            }
//
//            return false;
//    }
//    private boolean isEmail(String identifier) {
//        return identifier.contains("@");
//    }
//    private void sendOtpEmail(String email, String otp) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(email);
//            message.setSubject("PCUB Login - One Time Password (OTP)");
//            message.setFrom("team.pcub@gmail.com");
//
//            // Using String.format() for formatting the message
//            String emailContent = String.format(
//                    "Dear User,\n\nYour One Time Password (OTP) for logging into PCUB is: %s\n\n" +
//                            "Please use this OTP to complete your login. This code is valid for 5 minutes.\n\n" +
//                            "If you did not request this OTP, please ignore this email.\n\n" +
//                            "Regards,\nPCUB Support Team", otp);
//
//            message.setText(emailContent);
//            mailSender.send(message);
//            log.info("OTP email sent to {}", email);
//        } catch (Exception e) {
//            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
//            throw new RuntimeException("Failed to send OTP email");
//        }
//    }
@Autowired
private RedisTemplate<String, String> redisTemplate;

    private static final int EXPIRE_MINUTES = 2;
    private static final SecureRandom secureRandom = new SecureRandom();

    public void sendOtp(String identifier) {
        String otp = String.valueOf(secureRandom.nextInt(9000) + 1000);

        log.info("Generated OTP for {}: {}", identifier, otp);

        // Store OTP in Redis with TTL
        redisTemplate.opsForValue().set(identifier, otp, Duration.ofMinutes(EXPIRE_MINUTES));

        if (isEmail(identifier)) {
            sendOtpEmail(identifier, otp);
        }
    }

    public boolean verifyOtp(String identifier, String userOtp) {
        String savedOtp = redisTemplate.opsForValue().get(identifier);

        if (savedOtp == null) {
            log.warn("OTP expired or not found for identifier: {}", identifier);
            return false;
        }

        boolean isMatch = savedOtp.equals(userOtp);
        log.info("OTP verification for {}: match={}", identifier, isMatch);

        return isMatch;
    }

    private boolean isEmail(String identifier) {

        return identifier.contains("@");
    }

    private void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("PCubLogin - One Time Password (OTP)");
            message.setFrom("team.pcub@gmail.com");

            // Using String.format() for formatting the message
            String emailContent = String.format(
                    "Dear User,\n\nYour One Time Password (OTP) for logging into PCub is: %s\n\n" +
                            "Please use this OTP to complete your login. This code is valid for 1 minutes.\n\n" +
                            "If you did not request this OTP, please ignore this email.\n\n" +
                            "Regards,\nPCub Support Team", otp);

            message.setText(emailContent);
            mailSender.send(message);
            log.info("OTP email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send OTP email");
        }
    }

}




