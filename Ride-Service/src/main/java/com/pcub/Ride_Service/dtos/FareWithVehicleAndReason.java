package com.pcub.Ride_Service.dtos;
import com.pcub.Ride_Service.modals.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareWithVehicleAndReason {
    private VehicleType vehicleType; // bike / auto / car
    private int estimatedPrice;
    private int estimatedArrivalTime;
    private List<String> reasons;



}
