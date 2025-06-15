package com.pcub.Ride_Service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FareCalculationException extends RuntimeException {
    public FareCalculationException(String message) {
        super(message);
    }

    public FareCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
