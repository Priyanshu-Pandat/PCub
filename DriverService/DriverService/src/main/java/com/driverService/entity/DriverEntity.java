package com.driverService.entity;

import com.driverService.model.DriverAvailabilityStatus;
import com.driverService.model.VehicleType;
import com.driverService.model.VehicleTypeDTO;
import com.driverService.model.VerifiedStatus;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "driverId")
@ToString(exclude = "vehicles")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long driverId;

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




    // A driver has multiple vehicle
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;
    @Enumerated(EnumType.STRING)
    @Column(name = "current_vehicle_type")
    private VehicleType currentVehicleType;



    // A driver has only one bank Account ;
    @OneToOne(mappedBy = "driver",cascade = CascadeType.ALL)
    private BankDetails bankDetails;


    @Enumerated(EnumType.STRING)
    private DriverAvailabilityStatus availabilityStatus;
    private Double currentLatitude;
    private Double currentLongitude;

    @Enumerated(EnumType.STRING)
    private VerifiedStatus isVerified;

    @CreatedDate
    private LocalDateTime createdAt;

    // Changed name to lastUpdatedAt for clarity
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}