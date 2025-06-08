package com.driverService.model;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseInfoDTO {
    // PAGE 3
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{7}$", message = "Invalid license number format")
    private String licenseNumber;

    @Pattern(regexp = "^.+\\.(jpg|jpeg|png)$", message = "Front license image must be a JPG or PNG")
    private String licenceImageFront;

    @Pattern(regexp = "^.+\\.(jpg|jpeg|png)$", message = "Back license image must be a JPG or PNG")
    private String licenceImageBack;
}
