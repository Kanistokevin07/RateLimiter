package com.ratelimiter.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

    private static final Properties properties = new Properties();

    static {

        try (InputStream inputStream =
                     AppProperties.class.getClassLoader()
                             .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "application.properties not found."
                );
            }

            properties.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load application.properties.",
                    e
            );
        }
    }

    public static String get(String key) {

        String envKey = key
                .toUpperCase()
                .replace('.', '_');

        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return properties.getProperty(key);
    }
}