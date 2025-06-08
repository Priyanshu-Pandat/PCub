package com.driverService.validation;

import com.driverService.model.DriverStatusUpdateRequestDTO;
import com.driverService.model.DriverAvailabilityStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OnlineStatusValidator implements ConstraintValidator<ValidOnlineStatus, DriverStatusUpdateRequestDTO> {

    @Override
    public boolean isValid(DriverStatusUpdateRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.getAvailabilityStatus() == DriverAvailabilityStatus.ONLINE) {
            boolean isValid = dto.getLatitude() != null && dto.getLongitude() != null;
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Latitude and Longitude are required when going ONLINE")
                        .addConstraintViolation();
            }
            return isValid;
        }
        return true;
    }
}
