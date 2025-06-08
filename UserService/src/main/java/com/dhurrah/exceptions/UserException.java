package com.dhurrah.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class UserException extends RuntimeException {
    private String errorCode;

    public UserException(String message, String errorCode) {
        super(message);
        this.errorCode=errorCode;
    }


}
