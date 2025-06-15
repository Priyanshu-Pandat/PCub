package com.pcub.Ride_Service.modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String errorCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;
    public ErrorDetails(String errorCode, String message, String path, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }
}
