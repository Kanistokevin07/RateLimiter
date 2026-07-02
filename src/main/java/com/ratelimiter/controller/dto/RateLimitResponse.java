package com.ratelimiter.controller.dto;

public record RateLimitResponse(
        boolean allowed,
        long limit,
        long remaining,
        long resetTimeEpochMillis,
        long retryAfterMillis
) {
}