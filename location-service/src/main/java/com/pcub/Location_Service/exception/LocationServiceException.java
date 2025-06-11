package com.pcub.Location_Service.exception;


public class LocationServiceException extends RuntimeException {
    private final int statusCode;

    public LocationServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

