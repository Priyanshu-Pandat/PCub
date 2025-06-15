package com.pcub.Ride_Service.exception;

public class DistanceServiceException  extends  RuntimeException{
    private int statusCode;
    public DistanceServiceException(String message, int statusCode) {
        super(message);
        this.statusCode=statusCode;
    }
    public DistanceServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

}
