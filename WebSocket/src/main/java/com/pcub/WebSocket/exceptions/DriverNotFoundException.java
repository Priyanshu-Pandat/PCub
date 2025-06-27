package com.pcub.WebSocket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverNotFoundException extends RuntimeException{
    private String errorCode;


    public DriverNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode=errorCode;
    }
}

