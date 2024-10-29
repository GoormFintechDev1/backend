package com.example.backend.exception.auth;


import com.example.backend.exception.base_exceptions.ValidationException;

public class ValidateEmailException extends ValidationException {
    public ValidateEmailException(String message) {
        super(message);
    }
}
