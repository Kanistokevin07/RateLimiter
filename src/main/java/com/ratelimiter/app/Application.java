package com.ratelimiter.app;

import com.ratelimiter.config.AppProperties;
import com.ratelimiter.controller.ClientController;
import com.ratelimiter.redis.LuaScriptExecutor;
import com.ratelimiter.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.ratelimiter.config.DatabaseConfig;
import com.ratelimiter.config.RedisConfig;
import com.ratelimiter.controller.RateLimitController;
import com.ratelimiter.engine.tokenbucket.TokenBucketRateLimiter;
import com.ratelimiter.exception.GlobalExceptionHandler;
import com.ratelimiter.repository.BucketRepository;
import com.ratelimiter.repository.ClientConfigRepository;
import com.ratelimiter.repository.postgres.PostgresClientConfigRepository;
import com.ratelimiter.repository.redis.RedisBucketRepository;
import java.util.Map;
import java.util.TimeZone;

import com.ratelimiter.factory.RateLimiterFactory;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.service.RateLimitService;
import io.javalin.Javalin;

public class Application {

    private static final Logger logger =
            LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));


        // ---------- Infrastructure ----------
        logger.info("Starting Rate Limiter application...");

        RedisConfig redisConfig =
                new RedisConfig(AppProperties.get("redis.url"));

        logger.info("Successfully connected to Redis.");

        DatabaseConfig databaseConfig =
                new DatabaseConfig(
                        AppProperties.get("db.url"),
                        AppProperties.get("db.username"),
                        AppProperties.get("db.password")
                );

        // lua executor

        LuaScriptExecutor luaScriptExecutor = new LuaScriptExecutor(redisConfig.redisCommands());

        // ---------- Repositories ----------

        BucketRepository bucketRepository =
                new RedisBucketRepository(redisConfig.redisCommands(), luaScriptExecutor);

        ClientConfigRepository clientConfigRepository =
                new PostgresClientConfigRepository(
                        databaseConfig.dataSource()
                );

        logger.info("Successfully connected to PostgreSQL.");

        // ---------- Engine ----------

        TokenBucketRateLimiter tokenBucketRateLimiter =
                new TokenBucketRateLimiter(bucketRepository);

        RateLimiterFactory rateLimiterFactory =
                new RateLimiterFactory(
                        Map.of(
                                AlgorithmType.TOKEN_BUCKET,
                                tokenBucketRateLimiter
                        )
                );

        // ---------- Service ----------

        RateLimitService rateLimitService =
                new RateLimitService(
                        clientConfigRepository,
                        rateLimiterFactory
                );

        // ---------- Controller ----------

        RateLimitController rateLimitController =
                new RateLimitController(rateLimitService);

        // ---------- HTTP Server ----------


        logger.info("Starting Javalin server on port 7070...");

        Javalin app = Javalin.create();
        GlobalExceptionHandler.register(app);

        ClientService clientService =
                new ClientService(clientConfigRepository);

        ClientController clientController =
                new ClientController(clientService);

        clientController.registerRoutes(app);
        rateLimitController.registerRoutes(app);

        app.start(Integer.parseInt(
                AppProperties.get("server.port")
        ));

        logger.info("Rate Limiter is up and running.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            redisConfig.shutdown();
            databaseConfig.shutdown();
        }));
    }
}