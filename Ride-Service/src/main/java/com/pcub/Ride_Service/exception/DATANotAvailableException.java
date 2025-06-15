package com.pcub.Ride_Service.exception;

public class DATANotAvailableException extends RuntimeException {
    public DATANotAvailableException(String message) {
        super(message);
    }
    public DATANotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
