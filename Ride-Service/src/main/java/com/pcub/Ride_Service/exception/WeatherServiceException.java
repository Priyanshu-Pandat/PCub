package com.pcub.Ride_Service.exception;

public class WeatherServiceException extends RuntimeException{
    private int statusCode;
    public WeatherServiceException(String message, int statusCode) {
        super(message);
        this.statusCode=statusCode;
    }
    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

}
