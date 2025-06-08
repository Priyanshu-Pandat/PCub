package com.dhurrah.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler  extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceException(UserException userException){
        return new ResponseEntity<>(new ErrorResponse().builder()
                .errorMessage(userException.getMessage())
                .errorCode(userException.getErrorCode())
                                        .build(), HttpStatus.NOT_FOUND);

    }
}
