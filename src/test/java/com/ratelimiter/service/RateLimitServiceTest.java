package com.ratelimiter.service;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.exception.ClientNotFoundException;
import com.ratelimiter.factory.RateLimiterFactory;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.ClientConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private ClientConfigRepository repository;

    @Mock
    private RateLimiterFactory rateLimiterFactory;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private RateLimitService rateLimitService;

    private final ClientConfig clientConfig =
            new ClientConfig(
                    "client123",
                    AlgorithmType.TOKEN_BUCKET,
                    10,
                    2,
                    60,
                    true
            );

    @Test
    @DisplayName("Should allow request for valid client")
    void shouldAllowRequest() {

        // Arrange
        RateLimitDecision decision =
                new RateLimitDecision(
                        true,
                        10,
                        7,
                        System.currentTimeMillis() + 1000,
                        0
                );

        when(repository.findByClientId("client123"))
                .thenReturn(Optional.of(clientConfig));

        when(rateLimiterFactory.getRateLimiter(AlgorithmType.TOKEN_BUCKET))
                .thenReturn(rateLimiter);

        when(rateLimiter.allowRequest(
                "client123",
                clientConfig,
                3))
                .thenReturn(decision);

        // Act
        RateLimitDecision result =
                rateLimitService.allowRequest(
                        "client123",
                        3
                );

        // Assert
        assertEquals(decision, result);

        verify(repository)
                .findByClientId("client123");

        verify(rateLimiterFactory)
                .getRateLimiter(AlgorithmType.TOKEN_BUCKET);

        verify(rateLimiter)
                .allowRequest(
                        "client123",
                        clientConfig,
                        3
                );
    }

    @Test
    @DisplayName("Should throw ClientNotFoundException when client does not exist")
    void shouldThrowClientNotFoundException() {

        when(repository.findByClientId("client123"))
                .thenReturn(Optional.empty());

        assertThrows(
                ClientNotFoundException.class,
                () -> rateLimitService.allowRequest(
                        "client123",
                        3
                )
        );

        verify(repository)
                .findByClientId("client123");

        verifyNoInteractions(rateLimiterFactory);

        verifyNoInteractions(rateLimiter);
    }

    @Test
    @DisplayName("Should throw exception when client is disabled")
    void shouldThrowExceptionWhenClientDisabled() {

        ClientConfig disabledClient =
                new ClientConfig(
                        "client123",
                        AlgorithmType.TOKEN_BUCKET,
                        10,
                        2,
                        60,
                        false
                );

        when(repository.findByClientId("client123"))
                .thenReturn(Optional.of(disabledClient));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> rateLimitService.allowRequest(
                                "client123",
                                3
                        )
                );

        assertEquals(
                "Client is disabled.",
                exception.getMessage()
        );

        verify(repository)
                .findByClientId("client123");

        verifyNoInteractions(rateLimiterFactory);

        verifyNoInteractions(rateLimiter);
    }
}