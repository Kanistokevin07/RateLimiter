package com.ratelimiter.service;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.exception.ClientNotFoundException;
import com.ratelimiter.factory.RateLimiterFactory;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.ClientConfigRepository;
import com.ratelimiter.service.provider.ClientConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimitService {

    private final ClientConfigProvider clientConfigProvider;
    private final RateLimiterFactory rateLimiterFactory;
    private static final Logger logger =
            LoggerFactory.getLogger(RateLimitService.class);

    public RateLimitService(
            ClientConfigProvider clientConfigProvider,
            RateLimiterFactory rateLimiterFactory
    ) {
        this.clientConfigProvider = clientConfigProvider;
        this.rateLimiterFactory = rateLimiterFactory;
    }

    public RateLimitDecision allowRequest(String clientId, int tokensRequested) {

        logger.info(
                "Processing rate limit request for client '{}'.",
                clientId
        );

        ClientConfig clientConfig = clientConfigProvider
                .get(clientId);

        logger.debug(
                "Loaded configuration for client '{}': algorithm={}",
                clientId,
                clientConfig.algorithmType()
        );

        if (!clientConfig.enabled()) {
            throw new IllegalStateException("Client is disabled.");
        }

        RateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(clientConfig.algorithmType());

        return rateLimiter.allowRequest(
                clientId,
                clientConfig,
                tokensRequested
        );

    }
}