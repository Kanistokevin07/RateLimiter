package com.ratelimiter.redis.pubsub;

import com.ratelimiter.redis.pubsub.RedisChannels;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationUpdatePublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(ConfigurationUpdatePublisher.class);

    private final RedisCommands<String, String> redisCommands;

    public ConfigurationUpdatePublisher(
            RedisCommands<String, String> redisCommands
    ) {
        this.redisCommands = redisCommands;
    }

    public void publish(String clientId) {

        logger.info(
                "Publishing configuration update for client '{}'.",
                clientId
        );

        redisCommands.publish(
                RedisChannels.CLIENT_CONFIG_UPDATES,
                clientId
        );
    }
}