package com.driverService.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpDTO {
    @NotBlank(message = "Number is required")
    private String number;

    @Pattern(regexp = "^[0-9]{4}$", message = "OTP must be exactly 4 digits")
    private String otp;
}

