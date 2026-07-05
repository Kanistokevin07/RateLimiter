package com.ratelimiter.engine.tokenbucket;

import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.BucketRepository;
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
class TokenBucketRateLimiterTest {

    @Mock
    private BucketRepository bucketRepository;

    @InjectMocks
    private TokenBucketRateLimiter rateLimiter;

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
    @DisplayName("Should allow request when bucket has sufficient tokens")
    void shouldAllowRequest() {

        BucketState bucket =
                new BucketState(10, System.currentTimeMillis() / 1000);

        when(bucketRepository.findByClientId("client123"))
                .thenReturn(Optional.of(bucket));

        RateLimitDecision decision =
                rateLimiter.allowRequest(
                        "client123",
                        clientConfig,
                        3
                );

        assertTrue(decision.allowed());
        assertEquals(7, decision.remaining());

        verify(bucketRepository).save(eq("client123"), any(BucketState.class));
    }

    @Test
    @DisplayName("Should reject request when bucket has insufficient tokens")
    void shouldRejectRequest() {

        BucketState bucket =
                new BucketState(2, System.currentTimeMillis() / 1000);

        when(bucketRepository.findByClientId("client123"))
                .thenReturn(Optional.of(bucket));

        RateLimitDecision decision =
                rateLimiter.allowRequest(
                        "client123",
                        clientConfig,
                        5
                );

        assertFalse(decision.allowed());
        assertEquals(2, decision.remaining());

        verify(bucketRepository).save(eq("client123"), any(BucketState.class));
    }

    @Test
    @DisplayName("Should create a new bucket when client has no existing bucket")
    void shouldCreateNewBucket() {

        when(bucketRepository.findByClientId("client123"))
                .thenReturn(Optional.empty());

        RateLimitDecision decision =
                rateLimiter.allowRequest(
                        "client123",
                        clientConfig,
                        2
                );

        assertTrue(decision.allowed());
        assertEquals(8, decision.remaining());

        verify(bucketRepository).save(eq("client123"), any(BucketState.class));
    }

    @Test
    @DisplayName("Should always persist bucket state after processing request")
    void shouldAlwaysSaveBucket() {

        BucketState bucket =
                new BucketState(10, System.currentTimeMillis() / 1000);

        when(bucketRepository.findByClientId(anyString()))
                .thenReturn(Optional.of(bucket));

        rateLimiter.allowRequest(
                "client123",
                clientConfig,
                1
        );

        verify(bucketRepository, times(1))
                .save(eq("client123"), any(BucketState.class));
    }

}