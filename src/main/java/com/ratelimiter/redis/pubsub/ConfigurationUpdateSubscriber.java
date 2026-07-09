package com.ratelimiter.redis.pubsub;

import com.ratelimiter.redis.pubsub.RedisChannels;
import com.ratelimiter.service.provider.ClientConfigProvider;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationUpdateSubscriber
        extends RedisPubSubAdapter<String, String> {

    private static final Logger logger =
            LoggerFactory.getLogger(ConfigurationUpdateSubscriber.class);

    private final ClientConfigProvider configurationProvider;

    public ConfigurationUpdateSubscriber(
            ClientConfigProvider configurationProvider
    ) {
        this.configurationProvider = configurationProvider;
    }

    @Override
    public void message(String channel, String clientId) {

        if (!RedisChannels.CLIENT_CONFIG_UPDATES.equals(channel)) {
            return;
        }

        logger.info(
                "Received configuration update for client '{}'. Invalidating cache.",
                clientId
        );

        configurationProvider.invalidate(clientId);
    }

    @Override
    public void subscribed(String channel, long count) {
        logger.info(
                "Subscribed to Redis channel '{}'.",
                channel
        );
    }

    @Override
    public void unsubscribed(String channel, long count) {
        logger.info(
                "Unsubscribed from Redis channel '{}'.",
                channel
        );
    }
}