package com.ratelimiter.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LuaScriptLoaderTest {

    @Test
    void shouldLoadLuaScript() {

        String script =
                LuaScriptLoader.load(
                        "lua/token_bucket.lua"
                );

        assertThat(script)
                .isNotBlank();

        System.out.println(script);
    }
}