package com.pcub.Location_Service.exception;

import com.pcub.Location_Service.modal.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(LocationServiceException ex) {
        ApiResponse<Object> response = new ApiResponse<>(false, null, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(false, null, "Internal server error");
        ex.printStackTrace(); // Optional: use logger
        return ResponseEntity.status(500).body(response);
    }
}

