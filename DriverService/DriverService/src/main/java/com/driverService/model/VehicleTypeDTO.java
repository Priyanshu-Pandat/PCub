package com.driverService.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeDTO {

    //Page 2
    @Enumerated(EnumType.STRING)
    @NotNull(message = "vehicle type required")
    private VehicleType vehicleType;
    @NotBlank(message = "required")
    private  String fuelType ;
}
