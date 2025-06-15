package com.pcub.Ride_Service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationServiceException extends RuntimeException{
    private int statusCode;

    public LocationServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public LocationServiceException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}


