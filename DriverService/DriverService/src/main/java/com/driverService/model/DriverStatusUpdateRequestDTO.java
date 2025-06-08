package com.driverService.model;
import com.driverService.validation.ValidOnlineStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@ValidOnlineStatus
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusUpdateRequestDTO {

    @NotNull(message = "Availability status is required")
    private DriverAvailabilityStatus availabilityStatus;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    private Double longitude;
    @NotNull
    private VehicleType vehicleType;
}