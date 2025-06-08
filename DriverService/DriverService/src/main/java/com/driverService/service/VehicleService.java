package com.driverService.service;

import com.driverService.model.VehicleInfoDTO;
import com.driverService.model.VehicleTypeDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface VehicleService {
    void uploadVehicleDetails(VehicleInfoDTO vehicleInfoDTO,Long driverId);
    void setVehicleType(@Valid VehicleTypeDTO vehicleTypeDTO, Long vehicleId);


}
