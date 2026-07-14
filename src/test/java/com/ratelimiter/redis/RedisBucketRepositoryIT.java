package com.ratelimiter.redis;

import com.ratelimiter.config.RedisConfig;
import com.ratelimiter.engine.tokenbucket.BucketState;
import com.ratelimiter.repository.redis.RedisBucketRepository;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RedisBucketRepositoryIT {

    private RedisConfig redisConfig;
    private RedisCommands<String,String> redis;
    private RedisBucketRepository repository;
    LuaScriptExecutor luaExecutor =
            new LuaScriptExecutor(redisConfig.redisCommands());

    @BeforeEach
    void setup(){

        redisConfig =
                new RedisConfig("redis://localhost:6379");

        redis =
                redisConfig.redisCommands();

        redis.flushdb();

        repository =
                new RedisBucketRepository(redis, luaExecutor);

    }

    @AfterEach
    void cleanup(){

        redisConfig.shutdown();

    }

    @Test
    void shouldSaveBucket(){

        BucketState bucket =
                new BucketState(100,100);

        repository.save("client-1",bucket);

        Optional<BucketState> result =
                repository.findByClientId("client-1");

        assertTrue(result.isPresent());

        assertEquals(
                100,
                result.get().getAvailableTokens()
        );

        assertEquals(
                100,
                result.get().getLastRefillTimestamp()
        );

    }

    @Test
    void shouldReturnEmptyWhenBucketDoesNotExist(){

        assertTrue(
                repository.findByClientId("unknown").isEmpty()
        );

    }

    @Test
    void shouldOverwriteBucket(){

        repository.save(
                "client-1",
                new BucketState(100,1)
        );

        repository.save(
                "client-1",
                new BucketState(25,99)
        );

        BucketState bucket =
                repository.findByClientId("client-1").get();

        assertEquals(
                25,
                bucket.getAvailableTokens()
        );

        assertEquals(
                99,
                bucket.getLastRefillTimestamp()
        );

    }

}