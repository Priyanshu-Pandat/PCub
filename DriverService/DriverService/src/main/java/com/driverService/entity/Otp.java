//package com.driverService.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//public class Otp {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//    private String number;
//    private String  otp ;
//    private LocalDateTime createdAt;
//
//
//    public Otp(String number, String otp) {
//        this.otp=otp;
//        this.number=number;
//        this.createdAt = LocalDateTime.now();
//    }
//}
