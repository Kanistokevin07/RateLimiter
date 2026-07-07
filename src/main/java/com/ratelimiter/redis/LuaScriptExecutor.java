package com.ratelimiter.redis;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LuaScriptExecutor {

    private final RedisCommands<String, String> redis;
    private final String scriptSha;

    public LuaScriptExecutor(RedisCommands<String, String> redis) {

        this.redis = redis;

        try (InputStream input =
                     getClass().getClassLoader()
                             .getResourceAsStream("lua/token_bucket.lua")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Lua script not found."
                );
            }

            String script =
                    new String(
                            input.readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            this.scriptSha = redis.scriptLoad(script);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load Lua script.",
                    e
            );
        }
    }

    @SuppressWarnings("unchecked")
    public List<Long> execute(
            String clientId,
            long capacity,
            long refillRate,
            long currentTime,
            int requestedTokens
    ) {

        return (List<Long>) redis.evalsha(
                scriptSha,
                ScriptOutputType.MULTI,

                new String[]{
                        "bucket:" + clientId
                },

                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(currentTime),
                String.valueOf(requestedTokens)
        );
    }
}