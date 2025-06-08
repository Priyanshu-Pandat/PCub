package com.driverService.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDTO {

    //page 6
    @NotBlank(message = "required")
    private String emergencyContactName;

    @NotBlank(message = "required")
    private String emergencyContactNumber;

    @NotBlank(message = "required")
    private String relationship;
}

