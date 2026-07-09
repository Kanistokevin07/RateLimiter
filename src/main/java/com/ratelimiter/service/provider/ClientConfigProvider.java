package com.ratelimiter.service.provider;

import com.ratelimiter.model.ClientConfig;

public interface ClientConfigProvider {
    ClientConfig get(String clientId);
    void invalidate(String clientId);
}