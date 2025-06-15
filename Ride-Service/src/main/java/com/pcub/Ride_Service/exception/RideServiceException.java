package com.pcub.Ride_Service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class RideServiceException extends RuntimeException{
    private final int statusCode;

    public RideServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public RideServiceException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
