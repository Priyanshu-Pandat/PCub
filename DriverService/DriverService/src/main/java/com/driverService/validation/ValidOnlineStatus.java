package com.driverService.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OnlineStatusValidator.class)
public @interface ValidOnlineStatus {
    String message() default "Latitude and Longitude are required when going ONLINE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
