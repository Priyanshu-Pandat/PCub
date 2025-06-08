package com.driverService.repository;

import com.driverService.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepo extends JpaRepository<DriverEntity ,Long> {
    Optional<DriverEntity> findByNumber(String number );
    Optional<DriverEntity>  findByIdNumber(String idNumber);
    Optional<DriverEntity>  findByLicenseNumber(String licenseNumber );




}
