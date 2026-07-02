package com.ratelimiter.service;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.ClientConfigRepository;

public class RateLimitService {

    private final ClientConfigRepository clientConfigRepository;
    private final RateLimiter tokenBucketRateLimiter;

    public RateLimitService(
            ClientConfigRepository clientConfigRepository,
            RateLimiter tokenBucketRateLimiter
    ) {
        this.clientConfigRepository = clientConfigRepository;
        this.tokenBucketRateLimiter = tokenBucketRateLimiter;
    }

    public RateLimitDecision allowRequest(String clientId, int tokensRequested) {

        ClientConfig clientConfig = clientConfigRepository
                .findByClientId(clientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Client not found: " + clientId));

        if (!clientConfig.enabled()) {
            throw new IllegalStateException("Client is disabled.");
        }

        if (clientConfig.algorithmType() == AlgorithmType.TOKEN_BUCKET) {
            return tokenBucketRateLimiter.allowRequest(
                    clientId,
                    clientConfig,
                    tokensRequested
            );
        }

        throw new UnsupportedOperationException(
                "Unsupported algorithm: " + clientConfig.algorithmType()
        );
    }
}