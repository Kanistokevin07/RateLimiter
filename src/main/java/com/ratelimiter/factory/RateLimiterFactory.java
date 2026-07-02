package com.ratelimiter.factory;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.exception.ConfigurationException;
import com.ratelimiter.model.enums.AlgorithmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RateLimiterFactory {
    private final Map<AlgorithmType, RateLimiter> rateLimiters;
    private static final Logger logger =
            LoggerFactory.getLogger(RateLimiterFactory.class);

    public RateLimiterFactory(Map<AlgorithmType, RateLimiter> rateLimiters){
        this.rateLimiters = rateLimiters;
    }

    public RateLimiter getRateLimiter(AlgorithmType algorithmType){
        RateLimiter rateLimiter = rateLimiters.get(algorithmType);

        if(rateLimiter == null){
            throw new ConfigurationException(
                    "No RateLimiter registered for algorithm: " + algorithmType
            );
        }

        return rateLimiter;
    }
}
