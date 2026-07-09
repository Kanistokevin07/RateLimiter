package com.ratelimiter.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(RedisConfig.class);

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private final RedisCommands<String, String> redisCommands;

    public RedisConfig(String redisUrl) {

        logger.info("Initializing Redis connection...");

        this.redisClient = RedisClient.create(redisUrl);
        this.connection = redisClient.connect();
        this.redisCommands = connection.sync();
        this.pubSubConnection = redisClient.connectPubSub();

        logger.info("Redis connection established successfully.");
    }

    public RedisCommands<String, String> redisCommands() {
        return redisCommands;
    }

    public StatefulRedisPubSubConnection<String, String> pubSubConnection() {
        return pubSubConnection;
    }

    public void shutdown() {

        logger.info("Closing Redis connection.");

        connection.close();
        redisClient.shutdown();
        pubSubConnection.close();

        logger.info("Redis connection closed.");
    }
}