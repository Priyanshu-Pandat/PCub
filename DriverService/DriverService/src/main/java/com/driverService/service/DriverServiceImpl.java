package com.driverService.service;

import com.driverService.entity.DriverEntity;
import com.driverService.exception.DriverNotFoundException;
import com.driverService.model.*;
import com.driverService.repository.DriverRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DriverServiceImpl implements DriverService{
    @Autowired
    private DriverRepo driverRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void createLoginDriver(DriverProfileDTO driverDTO, Long driverId) {
        log.info("createdLoginMethod calling for driver profile setUp");
    DriverEntity driver = driverRepo.findById(driverId)
            .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " , "DRIVER_NOT_FOUND"));

    driver.setAge(driverDTO.getAge());
    driver.setGender(driverDTO.getGender());
    driver.setAddress(driverDTO.getAddress());
    driver.setFullName(driverDTO.getFullName());
    driver.setCurrentStep(1);
    driverRepo.save(driver);
    log.info("driver profile details saved : {} ",driver);
    }

    @Override
    public void updateDriverInfo(Long driverId) {

    }
    @Override
    public DriverFrontEndDTO getDriverById(Long driverId) {
        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + driverId, "DRIVER_NOT_FOUND"));

        // Step 1: Map DriverEntity to DTO
        DriverFrontEndDTO dto = modelMapper.map(driver, DriverFrontEndDTO.class);

        // Step 2: Map vehicles (if any)
        if (driver.getVehicles() != null && !driver.getVehicles().isEmpty()) {
            List<VehicleFrontEndDTO> vehicleDTOList = driver.getVehicles().stream()
                    .map(vehicle -> modelMapper.map(vehicle, VehicleFrontEndDTO.class))
                    .collect(Collectors.toList());
            dto.setVehicles(vehicleDTOList); // 🔧 corrected here
        }

        // Step 3: Map bank details (optional)
        if (driver.getBankDetails() != null) {
            BankDetailsDTO bankDetailsDTO = modelMapper.map(driver.getBankDetails(), BankDetailsDTO.class);
            dto.setBankDetails(bankDetailsDTO);
        }

        return dto;
    }


    @Override
    public List<DriverProfileDTO> getAllDriver() {
        return List.of();
    }


    @Override
    public void uploadDocumentInfo(DocumentsInfoDTO documentsInfoDTO, Long driverId) {
        // 1. Driver jise update karna hai
        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " , "DRIVER_NOT_FOUND"));

        // 2. Kya ye ID number kisi aur driver ke paas already hai?
        Optional<DriverEntity> existingDriverWithId = driverRepo.findByIdNumber(documentsInfoDTO.getIdNumber());
        if (existingDriverWithId.isPresent() && !Objects.equals(existingDriverWithId.get().getDriverId(), driverId)) {
            throw new DriverNotFoundException("ID already exists for another driver", "ID_ALREADY_EXISTS");
        }

        // 3. Save the ID
        driver.setIdNumber(documentsInfoDTO.getIdNumber());
        driver.setCurrentStep(2);
        driverRepo.save(driver);

        log.info("Document details saved for driverId: {}", driverId);
    }


    @Override
    public void uploadLicenseInfo(LicenseInfoDTO licenseInfoDTO, Long driverId) {
        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " , "DRIVER_NOT_FOUND"));
        Optional<DriverEntity> existingDriverWithId = driverRepo.findByLicenseNumber(licenseInfoDTO.getLicenseNumber());
        if (existingDriverWithId.isPresent() && !Objects.equals(existingDriverWithId.get().getDriverId(), driverId)) {
            throw new DriverNotFoundException("ID already exists for another driver", "ID_ALREADY_EXISTS");
        }
        driver.setLicenseNumber(licenseInfoDTO.getLicenseNumber());
        driver.setLicenceImageBack(licenseInfoDTO.getLicenceImageBack());
        driver.setLicenceImageFront(licenseInfoDTO.getLicenceImageFront());
        driver.setCurrentStep(4);
        driverRepo.save(driver);

        log.info("license  details saved : {} ",driverId);
    }

    @Override
    public void uploadEmergencyContactInfo(EmergencyContactDTO emergencyContactDTO, Long driverId) {
        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " , "DRIVER_NOT_FOUND"));
        driver.setEmergencyContactName(emergencyContactDTO.getEmergencyContactName());
        driver.setRelationship(emergencyContactDTO.getRelationship());
        driver.setEmergencyContactNumber(emergencyContactDTO.getEmergencyContactNumber());
        driver.setCurrentStep(6);
        driver.setProfileComplete(true);
        driverRepo.save(driver);
        log.info("emergency Contact  details saved : {} ",driverId);
    }

    @Override
    public void updateDriverStatus(DriverStatusUpdateRequestDTO dto, Long driverId) {
        log.info("Driver status update method in service calling");

        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + driverId, "DRIVER_NOT_FOUND"));

        driver.setAvailabilityStatus(dto.getAvailabilityStatus());

        if (dto.getAvailabilityStatus() == DriverAvailabilityStatus.ONLINE) {
            driver.setCurrentLatitude(dto.getLatitude());
            driver.setCurrentLongitude(dto.getLongitude());
        } else {
            driver.setCurrentLatitude(null);
            driver.setCurrentLongitude(null);
        }

        driverRepo.save(driver);
        log.info("Driver status updated with : {}",dto.getAvailabilityStatus());

    }

}
