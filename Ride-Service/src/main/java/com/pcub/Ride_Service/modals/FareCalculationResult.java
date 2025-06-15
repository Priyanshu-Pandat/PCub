package com.pcub.Ride_Service.modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareCalculationResult {
     private int finalFare;
     List<String> reasons;

}
