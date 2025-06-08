package com.driverService.repository;

import com.driverService.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle,Long> {
    Optional<Vehicle> findTopByDriver_DriverIdOrderByVehicleIdDesc(Long driverId);
    Optional<Vehicle> findByRcNumber(String rcNumber);




}
