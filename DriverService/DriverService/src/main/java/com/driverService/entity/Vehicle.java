package com.driverService.entity;

import com.driverService.model.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    //page 3
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    // Page 5
    private String rcNumber;
    private String rcImageFront;
    private String rcImageBack;
    private String fuelType;
    private String ownershipType;

    @ManyToOne
    @JoinColumn(name = "driverId")
    private DriverEntity driver;
}

