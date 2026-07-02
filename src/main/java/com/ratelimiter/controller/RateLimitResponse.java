package com.ratelimiter.controller;

public record RateLimitResponse(
        boolean allowed,
        long limit,
        long remaining,
        long resetTimeEpochMillis,
        long retryAfterMillis
) {
}