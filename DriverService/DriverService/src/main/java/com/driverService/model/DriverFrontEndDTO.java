package com.driverService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverFrontEndDTO {
    private Long driverId;

    // Page 1
    private String fullName;
    private Integer age;
    private String gender;
    private String number;
    private String address;
    private String profilePhoto;

    // Page 2
    private String idType;
    private String idNumber;
    private String idPhoto;

    // Page 4
    private String licenseNumber;
    private String licenceImageFront;
    private String licenceImageBack;

    // Page 6
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String relationship;

    private int currentStep;
    private boolean isProfileComplete;

    private DriverAvailabilityStatus availabilityStatus;
    private Double currentLatitude;
    private Double currentLongitude;

    private VerifiedStatus isVerified;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    // Vehicles list (optional)
    private List<VehicleFrontEndDTO> vehicles;

    // Bank details (optional)
    private BankDetailsDTO bankDetails;
}

