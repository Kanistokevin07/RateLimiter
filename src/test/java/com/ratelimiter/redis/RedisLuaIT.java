package com.ratelimiter.redis;

import com.ratelimiter.config.RedisConfig;
import com.ratelimiter.util.LuaScriptLoader;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class RedisLuaIT {

    private RedisConfig redisConfig;
    private RedisCommands<String, String> redis;

    @BeforeEach
    void setup() {

        redisConfig =
                new RedisConfig("redis://localhost:6379");

        redis =
                redisConfig.redisCommands();
    }

    @AfterEach
    void cleanup() {

        redis.flushall();

        redisConfig.shutdown();
    }

    @Test
    void shouldExecuteLuaScript() {

        String script =
                LuaScriptLoader.load("lua/token_bucket.lua");

        String result =
                redis.eval(
                        script,
                        ScriptOutputType.VALUE
                );

        assertThat(result)
                .isEqualTo("Hello from Redis Lua!");

        System.out.println(result);
    }
}