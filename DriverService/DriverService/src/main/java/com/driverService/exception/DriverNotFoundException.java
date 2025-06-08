package com.driverService.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverNotFoundException extends RuntimeException{
    private String errorCode;


    public DriverNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode=errorCode;
    }
}
