package com.ratelimiter.controller;

public record RateLimitRequest(
        String clientId,
        int tokensRequested
) {
}