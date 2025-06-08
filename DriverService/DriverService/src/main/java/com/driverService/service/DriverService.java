package com.driverService.service;

import com.driverService.model.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DriverService {
    void createLoginDriver(DriverProfileDTO driverDTO, Long driverId);
    void  updateDriverInfo(Long driverId);
    DriverFrontEndDTO getDriverById(Long driverId);
    List<DriverProfileDTO> getAllDriver();

    void uploadLicenseInfo(@Valid LicenseInfoDTO licenseInfoDTO, Long driverId);
    void uploadDocumentInfo(@Valid DocumentsInfoDTO documentsInfoDTO,Long driverId);
    void uploadEmergencyContactInfo(@Valid EmergencyContactDTO emergencyContactDTO,Long driverId);
    void updateDriverStatus(@Valid DriverStatusUpdateRequestDTO driverStatusUpdateRequestDTO , Long driverId);
}
