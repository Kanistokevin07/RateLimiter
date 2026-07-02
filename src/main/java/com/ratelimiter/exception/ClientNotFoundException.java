package com.ratelimiter.exception;

public class ClientNotFoundException extends RateLimiterException {

    public ClientNotFoundException(String clientId) {
        super("Client not found: " + clientId);
    }
}