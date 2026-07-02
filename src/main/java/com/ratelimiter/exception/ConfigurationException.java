package com.ratelimiter.exception;

public class ConfigurationException extends RateLimiterException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}