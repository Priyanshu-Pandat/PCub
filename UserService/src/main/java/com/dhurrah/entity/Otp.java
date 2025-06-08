//package com.dhurrah.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//public class Otp {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String identifier; // Can be phone number or email
//    private String otp;
//    private LocalDateTime createdAt;
//
//    public Otp(String identifier, String otp) {
//        this.identifier = identifier;
//        this.otp = otp;
//    }
//
//    @PrePersist
//    public void prePersist() {
//        this.createdAt = LocalDateTime.now();
//    }
//}
