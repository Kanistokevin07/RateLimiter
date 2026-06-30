package com.ratelimiter.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisConfig {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> redisCommands;

    public RedisConfig(String redisUrl) {

        this.redisClient = RedisClient.create(redisUrl);
        this.connection = redisClient.connect();
        this.redisCommands = connection.sync();
    }

    public RedisCommands<String, String> redisCommands() {
        return redisCommands;
    }

    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }
}