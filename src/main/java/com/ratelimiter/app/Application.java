package com.ratelimiter.app;

import com.ratelimiter.config.DatabaseConfig;
import com.ratelimiter.config.RedisConfig;
import com.ratelimiter.controller.RateLimitController;
import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.engine.tokenbucket.TokenBucketRateLimiter;
import com.ratelimiter.repository.BucketRepository;
import com.ratelimiter.repository.ClientConfigRepository;
import com.ratelimiter.repository.postgres.PostgresClientConfigRepository;
import com.ratelimiter.repository.redis.RedisBucketRepository;
import com.ratelimiter.service.RateLimitService;
import io.javalin.Javalin;

public class Application {

    public static void main(String[] args) {

        // ---------- Infrastructure ----------

        RedisConfig redisConfig =
                new RedisConfig("redis://localhost:6379");

        DatabaseConfig databaseConfig =
                new DatabaseConfig(
                        "jdbc:postgresql://localhost:5432/rate_limiter",
                        "postgres",
                        "root"
                );

        // ---------- Repositories ----------

        BucketRepository bucketRepository =
                new RedisBucketRepository(redisConfig.redisCommands());

        ClientConfigRepository clientConfigRepository =
                new PostgresClientConfigRepository(
                        databaseConfig.dataSource()
                );

        // ---------- Engine ----------

        RateLimiter tokenBucketRateLimiter =
                new TokenBucketRateLimiter(bucketRepository);

        // ---------- Service ----------

        RateLimitService rateLimitService =
                new RateLimitService(
                        clientConfigRepository,
                        tokenBucketRateLimiter
                );

        // ---------- Controller ----------

        RateLimitController rateLimitController =
                new RateLimitController(rateLimitService);

        // ---------- HTTP Server ----------

        Javalin app = Javalin.create();

        app.post(
                "/api/v1/rate-limit",
                rateLimitController::handleRateLimit
        );

        app.start(7070);

        System.out.println("Rate Limiter started on port 7070");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            redisConfig.shutdown();
            databaseConfig.shutdown();
        }));
    }
}