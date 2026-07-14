package com.ratelimiter.service.provider;

import com.ratelimiter.exception.ClientNotFoundException;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.repository.ClientConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class CachedClientConfigProvider implements ClientConfigProvider {

    private static final Logger logger =
            LoggerFactory.getLogger(CachedClientConfigProvider.class);

    private final ClientConfigRepository repository;

    private final ConcurrentHashMap<String, ClientConfig> cache =
            new ConcurrentHashMap<>();

    public CachedClientConfigProvider(ClientConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClientConfig get(String clientId) {
        ClientConfig cached = cache.get(clientId);

        if (cached != null) {
            logger.info("Cache HIT for client '{}'.", clientId);
            return cached;
        }

        logger.info("Cache MISS for client '{}'. Loading from database.", clientId);

        ClientConfig config = repository
                .findByClientId(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        cache.put(clientId, config);
        return config;
    }

    @Override
    public void invalidate(String clientId) {

        cache.remove(clientId);
        logger.info(
                "Invalidated cache for client '{}'.",
                clientId
        );
    }
}