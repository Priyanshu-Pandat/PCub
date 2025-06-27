package com.pcub.Ride_Service.modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDistance {
    Long driverID;
    double distance;
}
