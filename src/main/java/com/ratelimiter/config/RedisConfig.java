package com.ratelimiter.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(RedisConfig.class);

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> redisCommands;

    public RedisConfig(String redisUrl) {

        logger.info("Initializing Redis connection...");

        this.redisClient = RedisClient.create(redisUrl);
        this.connection = redisClient.connect();
        this.redisCommands = connection.sync();

        logger.info("Redis connection established successfully.");
    }

    public RedisCommands<String, String> redisCommands() {
        return redisCommands;
    }

    public void shutdown() {

        logger.info("Closing Redis connection.");

        connection.close();
        redisClient.shutdown();

        logger.info("Redis connection closed.");
    }
}