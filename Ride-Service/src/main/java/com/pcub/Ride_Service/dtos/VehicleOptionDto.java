package com.pcub.Ride_Service.dtos;
import com.pcub.Ride_Service.modals.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleOptionDto {
    private VehicleType vehicleType; // bike / auto / car
    private double estimatedPrice;
    private int estimatedArrivalTime; // in minutes

}
