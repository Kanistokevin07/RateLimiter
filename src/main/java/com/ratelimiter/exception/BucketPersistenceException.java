package com.ratelimiter.exception;

public class BucketPersistenceException extends RateLimiterException {

    public BucketPersistenceException(String message) {
        super(message);
    }

    public BucketPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}