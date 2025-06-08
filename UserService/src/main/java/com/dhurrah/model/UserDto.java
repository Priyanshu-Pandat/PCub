package com.dhurrah.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Age is required")
    private Integer age;
    @Email(message = "Email is required")
    private String email;
    @NotNull(message = "PhoneNumber is required")
    private String phoneNumber;

    // getters and setters
}
