package com.ratelimiter.exception;

public class InvalidRequestException extends RateLimiterException {

    public InvalidRequestException(String message) {
        super(message);
    }
}