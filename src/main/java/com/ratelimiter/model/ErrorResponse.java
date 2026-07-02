package com.ratelimiter.model;

public record ErrorResponse(
        int status,
        String error,
        long timestamp
) {
}