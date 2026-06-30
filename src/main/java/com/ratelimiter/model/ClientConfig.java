package com.ratelimiter.model;

import com.ratelimiter.model.enums.AlgorithmType;

public record ClientConfig(String clientId, AlgorithmType algorithmType, long capacity,
                           long refillTokensPerSecond, long windowSizeSeconds, boolean enabled) {
}
