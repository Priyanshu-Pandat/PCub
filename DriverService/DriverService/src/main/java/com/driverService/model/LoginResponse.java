package com.driverService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
 public class LoginResponse {

    private String token;
    private boolean newUser;
    private int currentStep;

}
