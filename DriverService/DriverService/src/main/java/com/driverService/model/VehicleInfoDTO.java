package com.driverService.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleInfoDTO {

    //  Page 3



    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", message = "Invalid RC number format")
    private String rcNumber;

    @Pattern(regexp = "^.+\\.(jpg|jpeg|png)$", message = "RC Image must be a JPG or PNG")
    private String rcImageFront;

    @Pattern(regexp = "^.+\\.(jpg|jpeg|png)$", message = "RC Image must be a JPG or PNG")
    private String rcImageBack;
    @NotBlank(message = "required")
    private String ownershipType;


}
