package com.driverService.controller;

import com.driverService.model.*;
import com.driverService.service.DriverDocumentService;
import com.driverService.service.DriverService;
import com.driverService.service.VehicleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DriverDocumentService driverDocumentService;

    //------> Page 1
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createLoginDriver(
            @Valid @RequestBody DriverProfileDTO driverProfileDTO,
            @RequestHeader("X-User-Id") Long driverId) {

        log.info("Driver Controller calling for DriverProfile details for ID: {}", driverId);

        driverService.createLoginDriver(driverProfileDTO, driverId);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Driver profile saved successfully");

        return ResponseEntity.ok(res);
    }

    // ----->    Upload Documents (AADHAAR , PAN , LICENCE , RC , PROFILE )
    @PostMapping("/upload-document")
    public ResponseEntity<String> uploadDriverDocument(@RequestParam("file")MultipartFile file,
                                                       @RequestParam("documentType") String documentType,
                                                       @RequestHeader("X-User-Id") Long driverId) {
        log.info("upload on cloud method calling on Controller");
         String url = driverDocumentService.handleUpload(file,documentType,driverId);

         return ResponseEntity.ok(url);
    }

    // -------> PAGE 2
    @PostMapping("/upload")
    public ResponseEntity<Map<String,Object>> uploadDocumentInfo(@Valid @RequestBody DocumentsInfoDTO documentsInfoDTO,
                                                                 @RequestHeader("X-User-Id") Long driverId){
        log.info("upload document  method calling on Controller");
        driverService.uploadDocumentInfo(documentsInfoDTO,driverId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "documents details saved successfully");
        return ResponseEntity.ok(res);
    }

    // PAGE 3
    @PostMapping("/setVehicleType")
    public ResponseEntity<Map<String,Object>> setVehicleType(@Valid @RequestBody VehicleTypeDTO vehicleTypeDTO,
                                                             @RequestHeader("X-User-Id") Long driverId) {
        log.info("vehicle document  method calling on Controller");
        vehicleService.setVehicleType(vehicleTypeDTO, driverId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "vehicle Type details saved successfully");
        return ResponseEntity.ok(res);
    }

    // --------->  PAGE 4
    @PostMapping("/licenseInfo")
    public ResponseEntity<Map<String,Object>> uploadLicenseInfo (@Valid @RequestBody LicenseInfoDTO licenseInfoDTO,
                                                                 @RequestHeader("X-User-Id") Long driverId) {
        log.info(" calling for upload license  details on Controller");
        driverService.uploadLicenseInfo(licenseInfoDTO,driverId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "license details saved successfully");
        return ResponseEntity.ok(res);
    }

    // PAGE 5
     @PostMapping("/vehicle")
    public ResponseEntity<Map<String, Object>> uploadVehicleDetails(@Valid @RequestBody VehicleInfoDTO vehicleInfoDTO,
                                                                    @RequestHeader("X-User-Id") Long driverId) {
         log.info("Vehicle calling for uploadVehicle details on Controller");
         vehicleService.uploadVehicleDetails(vehicleInfoDTO,driverId);
         Map<String, Object> res = new HashMap<>();
         res.put("success", true);
         res.put("message", "vehicle details saved successfully");
         return ResponseEntity.ok(res);

     }
     // PAGE 6
    @PostMapping("/emergencyContactInfo")
    public ResponseEntity<Map<String, Object>> uploadEmergencyContactInfo(@Valid @RequestBody EmergencyContactDTO emergencyContactDTO,
                                                                          @RequestHeader("X-User-Id") Long driverId){
        log.info(" emergencyContact calling for uploadVehicle details on Controller");
        driverService.uploadEmergencyContactInfo(emergencyContactDTO, driverId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "emergencyContact  details saved successfully");
        return ResponseEntity.ok(res);

    }


    @GetMapping("/getDriver")
    public ResponseEntity<DriverFrontEndDTO> getDriverById(@RequestHeader("X-User-Id") Long driverId) {
        DriverFrontEndDTO driverDTO = driverService.getDriverById(driverId);

        return new ResponseEntity<>(driverDTO, HttpStatus.OK); // ✅ return data + 200 status
    }

}
