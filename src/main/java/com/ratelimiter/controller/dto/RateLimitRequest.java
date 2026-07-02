package com.ratelimiter.controller.dto;

public record RateLimitRequest(
        String clientId,
        int tokensRequested
) {
}