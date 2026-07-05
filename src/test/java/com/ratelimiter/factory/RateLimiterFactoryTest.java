package com.ratelimiter.factory;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.exception.ConfigurationException;
import com.ratelimiter.model.enums.AlgorithmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateLimiterFactoryTest {

    @Mock
    private RateLimiter tokenBucketRateLimiter;

    private RateLimiterFactory rateLimiterFactory;

    @BeforeEach
    void setUp() {

        rateLimiterFactory =
                new RateLimiterFactory(
                        Map.of(
                                AlgorithmType.TOKEN_BUCKET,
                                tokenBucketRateLimiter
                        )
                );
    }

    @Test
    @DisplayName("Should return registered RateLimiter")
    void shouldReturnRegisteredRateLimiter() {

        RateLimiter result =
                rateLimiterFactory.getRateLimiter(
                        AlgorithmType.TOKEN_BUCKET
                );

        assertSame(tokenBucketRateLimiter, result);
    }

    @Test
    @DisplayName("Should throw ConfigurationException when algorithm is not registered")
    void shouldThrowConfigurationException() {

        RateLimiterFactory emptyFactory =
                new RateLimiterFactory(Map.of());

        ConfigurationException exception =
                assertThrows(
                        ConfigurationException.class,
                        () -> emptyFactory.getRateLimiter(
                                AlgorithmType.TOKEN_BUCKET
                        )
                );

        assertEquals(
                "No RateLimiter registered for algorithm: TOKEN_BUCKET",
                exception.getMessage()
        );
    }
}