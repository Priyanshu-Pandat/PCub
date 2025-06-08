package com.driverService.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverProfileDTO {

    //Page 1
    @NotBlank(message = "Name is required")
    private String fullName;

    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @NotBlank(message = "Gender required")
    private String gender;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(
            regexp = "^.+\\.(jpg|jpeg|png)$",
            message = "Profile image must be a JPG or PNG"
    )
    private String profilePhoto;

}
