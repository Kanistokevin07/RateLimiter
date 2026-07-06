package com.ratelimiter.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class LuaScriptLoader {

    private LuaScriptLoader() {
    }

    public static String load(String fileName) {

        try (InputStream inputStream =
                     LuaScriptLoader.class
                             .getClassLoader()
                             .getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "Lua script not found: " + fileName
                );
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load Lua script.",
                    e
            );
        }
    }
}