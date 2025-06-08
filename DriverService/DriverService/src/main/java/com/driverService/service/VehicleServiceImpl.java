package com.driverService.service;

import com.driverService.entity.DriverEntity;
import com.driverService.entity.Vehicle;
import com.driverService.exception.DriverNotFoundException;
import com.driverService.model.VehicleInfoDTO;
import com.driverService.model.VehicleTypeDTO;
import com.driverService.repository.DriverRepo;
import com.driverService.repository.VehicleRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class VehicleServiceImpl implements VehicleService{
    @Autowired
    private VehicleRepo vehicleRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Override
    public void setVehicleType(VehicleTypeDTO vehicleTypeDTO, Long driverId) {
        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " , "DRIVER_NOT_FOUND"));
        Vehicle  vehicle = new Vehicle();
        vehicle.setVehicleType(vehicleTypeDTO.getVehicleType());
        vehicle.setFuelType(vehicleTypeDTO.getFuelType());
        vehicle.setDriver(driver);
        vehicleRepo.save(vehicle);
        driver.setCurrentStep(3);
        driverRepo.save(driver);
        log.info("vehicleType method  called for vehicle info setUp and Saved SuccessFully ");

    }
    @Override
    public void uploadVehicleDetails(VehicleInfoDTO vehicleInfoDTO, Long driverId) {

        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " , "DRIVER_NOT_FOUND")
                );
        Vehicle vehicle = vehicleRepo.findTopByDriver_DriverIdOrderByVehicleIdDesc(driverId)
                .orElseThrow(() -> new DriverNotFoundException("No vehicle found to update for Driver ID: " + driverId, "VEHICLE_NOT_FOUND"));
        // Check if RC number already exists for another driver
        Optional<Vehicle> existingVehicle = vehicleRepo.findByRcNumber(vehicleInfoDTO.getRcNumber());
        if (existingVehicle.isPresent() && !Objects.equals(existingVehicle.get().getDriver().getDriverId(), driverId)) {
            throw new DriverNotFoundException("RC Number already exists for another driver", "RC_ALREADY_EXISTS");
        }

        vehicle.setRcNumber(vehicleInfoDTO.getRcNumber());
        // vehicle.setRcImageBack(vehicleInfoDTO.getRcImageBack());
       // vehicle.setRcImageFront(vehicleInfoDTO.getRcImageFront());
        vehicle.setOwnershipType(vehicleInfoDTO.getOwnershipType());
        driver.setCurrentStep(5);
        vehicle.setDriver(driver);
        vehicleRepo.save(vehicle);
        driverRepo.save(driver);
        log.info("vehicleMethod called for vehicle info setUp and Saved SuccessFully ");
    }

}
