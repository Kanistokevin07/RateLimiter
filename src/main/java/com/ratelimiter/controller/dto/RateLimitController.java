package com.ratelimiter.controller;

import com.ratelimiter.controller.dto.RateLimitRequest;
import com.ratelimiter.controller.dto.RateLimitResponse;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.service.RateLimitService;
import io.javalin.http.Context;

public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    public void handleRateLimit(Context ctx) {

        RateLimitRequest request =
                ctx.bodyAsClass(RateLimitRequest.class);

        RateLimitDecision decision =
                rateLimitService.allowRequest(
                        request.clientId(),
                        request.tokensRequested()
                );

        RateLimitResponse response = new RateLimitResponse(
                decision.allowed(),
                decision.limit(),
                decision.remaining(),
                decision.resetTimeEpochMillis(),
                decision.retryAfterMillis()
        );

        ctx.header("X-RateLimit-Limit", String.valueOf(decision.limit()));
        ctx.header("X-RateLimit-Remaining", String.valueOf(decision.remaining()));
        ctx.header("X-RateLimit-Reset", String.valueOf(decision.resetTimeEpochMillis()));

        if (!decision.allowed()) {
            ctx.header("Retry-After", String.valueOf(decision.retryAfterMillis()));
            ctx.status(429);
        } else {
            ctx.status(200);
        }

        ctx.json(response);
    }
}