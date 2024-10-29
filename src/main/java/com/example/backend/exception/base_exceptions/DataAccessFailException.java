package com.example.backend.exception.base_exceptions;

public class DataAccessFailException extends RuntimeException {
    public DataAccessFailException(String message) {
        super(message);
    }
}
