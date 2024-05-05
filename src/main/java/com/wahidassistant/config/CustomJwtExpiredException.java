package com.wahidassistant.config;

public class CustomJwtExpiredException extends RuntimeException {
    public CustomJwtExpiredException(String message) {
        super(message);
    }
}
