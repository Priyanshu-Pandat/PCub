package com.driverService.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BankDetailsId;

    private String bankHolderName;
    private String bankName;
    private String accountNumber;
    private String ifscCode;

    @OneToOne
    @JoinColumn(name = "driver_id")
    private DriverEntity driver;
}

