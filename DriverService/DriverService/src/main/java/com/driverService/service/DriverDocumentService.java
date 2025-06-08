package com.driverService.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface DriverDocumentService {
    String handleUpload(MultipartFile file,String documentType,Long driverId);
}
