package com.driverService.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    String upload(MultipartFile file);
}
