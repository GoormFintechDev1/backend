package com.example.backend.exception.redis;

import javax.naming.ServiceUnavailableException;

public class RedisConnectionException extends ServiceUnavailableException {
    public RedisConnectionException(String message) {
        super(message);
    }
}
