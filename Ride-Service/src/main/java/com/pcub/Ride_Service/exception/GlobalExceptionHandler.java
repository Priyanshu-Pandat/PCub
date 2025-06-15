package com.pcub.Ride_Service.exception;

import com.pcub.Ride_Service.modals.ApiResponse;
import com.pcub.Ride_Service.modals.ErrorDetails;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RideServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleRideServiceException(RideServiceException ex, WebRequest request) {
        logger.error("Ride service error: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                "RIDE_SERVICE_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(FareCalculationException.class)
    public ResponseEntity<ApiResponse<Object>> handleFareCalculationException(FareCalculationException ex, WebRequest request) {
        logger.error("Fare calculation error: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                "FARE_CALCULATION_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, "Unable to calculate fare for the requested ride");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DATANotAvailableException.class)
    public ResponseEntity<ApiResponse<Object>> handleVehicleNotAvailableException(DATANotAvailableException ex, WebRequest request) {
        logger.warn("Data not available: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                "DATA_NOT_AVAILABLE",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, "Requested vehicle type is not available");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorDetails errorDetails = new ErrorDetails(
                "VALIDATION_ERROR",
                "Invalid input parameters",
                request.getDescription(false),
                LocalDateTime.now(),
                validationErrors
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, "Please check your input parameters");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        logger.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> constraintErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                constraintErrors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        ErrorDetails errorDetails = new ErrorDetails(
                "CONSTRAINT_VIOLATION",
                "Input constraint violation",
                request.getDescription(false),
                LocalDateTime.now(),
                constraintErrors
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, "Invalid input parameters");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        logger.error("Type mismatch error: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

        ErrorDetails errorDetails = new ErrorDetails(
                "TYPE_MISMATCH",
                message,
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, "Invalid parameter type");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(LocationServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleLocationServiceException(LocationServiceException ex, WebRequest request) {
        logger.error("Location service error: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = new ErrorDetails(
                "LOCATION_SERVICE_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleWeatherServiceException(WeatherServiceException ex, WebRequest request) {
        logger.error("Weather service error: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                "WEATHER_SERVICE_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(DistanceServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDistanceServiceException(DistanceServiceException ex, WebRequest request) {
        logger.error("Distance service error: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                "DISTANCE_SERVICE_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        ApiResponse<Object> response = new ApiResponse<>(false, errorDetails, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
}
