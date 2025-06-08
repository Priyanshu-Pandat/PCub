package com.driverService.service;

import com.driverService.entity.DriverEntity;
import com.driverService.entity.Vehicle;
import com.driverService.exception.DriverNotFoundException;
import com.driverService.repository.DriverRepo;
import com.driverService.repository.VehicleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class DriverDocumentServiceImpl implements DriverDocumentService{
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private VehicleRepo vehicleRepo;
    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public String handleUpload(MultipartFile file, String documentType, Long driverId) {
        DriverEntity driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with ID: " + driverId, "DRIVER_NOT_FOUND"));

        String imageUrl = imageUploadService.upload(file);

        switch (documentType.toUpperCase()) {
            case "PROFILE_PHOTO":
                driver.setProfilePhoto(imageUrl);
                driver.setCurrentStep(1);
                break;

            case "AADHAAR":
                driver.setIdPhoto(imageUrl);
                driver.setIdType("AADHAAR");
                break;

            case "PAN":
                driver.setIdPhoto(imageUrl);
                driver.setIdType("PAN");
                break;

            case "LICENSE_FRONT":
                driver.setLicenceImageFront(imageUrl);
                break;

            case "LICENSE_BACK":
                driver.setLicenceImageBack(imageUrl);
                break;

            case "RC_FRONT":
            case "RC_BACK":
                Vehicle vehicle = vehicleRepo.findTopByDriver_DriverIdOrderByVehicleIdDesc(driverId)
                        .orElseThrow(() -> new DriverNotFoundException("No vehicle found for Driver ID: " + driverId, "VEHICLE_NOT_FOUND"));

                if (documentType.equalsIgnoreCase("RC_FRONT")) {
                    vehicle.setRcImageFront(imageUrl);
                } else {
                    vehicle.setRcImageBack(imageUrl);
                }

                vehicleRepo.save(vehicle); // Save only if vehicle doc
                break;

            default:
                throw new IllegalArgumentException("Invalid Document Type: " + documentType);
        }

        driverRepo.save(driver); // Always save driver for profile/license/aadhaar/pan
        log.info("Upload method saved the file for documentType: {}", documentType);
        return imageUrl;
    }

}
