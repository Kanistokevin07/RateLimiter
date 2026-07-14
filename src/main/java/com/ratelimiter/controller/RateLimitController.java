package com.ratelimiter.controller;

import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.service.RateLimitService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimitController {

    private final RateLimitService rateLimitService;
    private static final Logger logger =
            LoggerFactory.getLogger(RateLimitController.class);

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    public void handleRateLimit(Context ctx) {

        RateLimitRequest request =
                ctx.bodyAsClass(RateLimitRequest.class);

        logger.info(
                "Request handled by container: {}",
                System.getenv("HOSTNAME")
        );

        logger.info(
                "Received rate limit request for client '{}' requesting {} token(s).",
                request.clientId(),
                request.tokensRequested()
        );

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

        logger.info(
                "Processed request for client '{}'. Allowed={}, RemainingTokens={}",
                request.clientId(),
                decision.allowed(),
                decision.remaining()
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

    public void registerRoutes(Javalin app) {
        app.post(
                "/rate-limit",
                this::handleRateLimit
        );
    }
}