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
import io.micrometer.core.instrument.Timer;

public class RateLimitService {

    private final ClientConfigProvider clientConfigProvider;
    private final RateLimiterFactory rateLimiterFactory;
    private final MetricsService metricsService;

    private static final Logger logger =
            LoggerFactory.getLogger(RateLimitService.class);

    public RateLimitService(
            ClientConfigProvider clientConfigProvider,
            RateLimiterFactory rateLimiterFactory,
            MetricsService metricsService
    ) {
        this.clientConfigProvider = clientConfigProvider;
        this.rateLimiterFactory = rateLimiterFactory;
        this.metricsService = metricsService;
    }

    public RateLimitDecision allowRequest(
            String clientId,
            int tokensRequested
    ) {

        metricsService.incrementTotalRequests();

        Timer.Sample sample =
                Timer.start();

        try {
            logger.info(
                    "Processing rate limit request for client '{}'.",
                    clientId
            );

            ClientConfig clientConfig =
                    clientConfigProvider.get(clientId);

            logger.debug(
                    "Loaded configuration for client '{}': algorithm={}",
                    clientId,
                    clientConfig.algorithmType()
            );

            if (!clientConfig.enabled()) {
                throw new IllegalStateException(
                        "Client is disabled."
                );
            }

            RateLimiter rateLimiter =
                    rateLimiterFactory.getRateLimiter(
                            clientConfig.algorithmType()
                    );

            RateLimitDecision decision =
                    rateLimiter.allowRequest(
                            clientId,
                            clientConfig,
                            tokensRequested
                    );
            if (decision.allowed()) {
                metricsService.incrementAllowedRequests();
                logger.debug(
                        "Request allowed for client '{}'. Remaining tokens={}",
                        clientId,
                        decision.remaining()
                );
            }
            else {
                metricsService.incrementRejectedRequests();
                logger.debug(
                        "Request rejected for client '{}'. Retry after={} ms",
                        clientId,
                        decision.retryAfterMillis()
                );
            }
            return decision;
        }
        finally {
            sample.stop(
                    metricsService.getRequestLatency()
            );
        }
    }
}