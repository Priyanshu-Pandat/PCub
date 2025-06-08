package com.driverService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleFrontEndDTO {
    private Long vehicleId;

    // Page 3
    private VehicleType vehicleType;

    // Page 5
    private String rcNumber;
    private String rcImageFront;
    private String rcImageBack;

    private String fuelType;
    private String ownershipType;

    // Agar preview/edit page pe driver ke vehicles dikhane ho toh ye DTO kaam aayega
}
