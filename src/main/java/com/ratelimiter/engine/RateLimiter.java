package com.ratelimiter.engine;

import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;

public interface RateLimiter {
    RateLimitDecision allowRequest(String clientId, ClientConfig clientConfig, int tokensRequested);
}
