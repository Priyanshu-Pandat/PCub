package com.driverService.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentsInfoDTO {

    //page 2
    @NotBlank(message = "required")
    private String idType;
    @NotBlank(message = "required")
    private String idNumber;

    @Pattern(
            regexp = "^.+\\.(jpg|jpeg|png)$",
            message = "Id Image must be a JPG or PNG"
    )
    private String idPhoto;

}
