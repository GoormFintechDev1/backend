package com.example.backend.exception.auth;


import com.example.backend.exception.base_exceptions.ValidationException;

public class ValidateNickNameException extends ValidationException {
    public ValidateNickNameException(String message) {
        super(message);
    }
}
