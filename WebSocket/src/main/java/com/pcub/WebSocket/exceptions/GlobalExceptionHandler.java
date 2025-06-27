package com.pcub.WebSocket.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceException(DriverNotFoundException driverException) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().builder()
                .errorMessage(driverException.getMessage())
                .errorCode(driverException.getErrorCode())
                .build(), HttpStatus.NOT_FOUND );
    }
}
