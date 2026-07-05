package com.ratelimiter.engine.tokenbucket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BucketStateTest {

    private BucketState bucket;

    @BeforeEach
    void setUp() {
        bucket = new BucketState(10, 100);
    }

    @Test
    @DisplayName("Should initialize bucket with given values")
    void shouldInitializeBucket() {

        assertEquals(10, bucket.getAvailableTokens());
        assertEquals(100, bucket.getLastRefillTimestamp());
    }

    @Test
    @DisplayName("Should consume tokens when sufficient tokens are available")
    void shouldConsumeTokens() {

        bucket.consume(3);

        assertEquals(7, bucket.getAvailableTokens());
    }

    @Test
    @DisplayName("Should throw exception when consuming more tokens than available")
    void shouldThrowExceptionWhenTokensAreInsufficient() {

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> bucket.consume(20)
                );

        assertEquals(
                "Insufficient tokens available.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Should refill tokens based on elapsed time")
    void shouldRefillTokens() {

        bucket.consume(5);

        bucket.refill(
                10,
                2,
                103
        );

        assertEquals(10, bucket.getAvailableTokens());
        assertEquals(103, bucket.getLastRefillTimestamp());
    }

    @Test
    @DisplayName("Should never exceed bucket capacity after refill")
    void shouldNotExceedCapacity() {

        bucket.refill(
                10,
                100,
                200
        );

        assertEquals(10, bucket.getAvailableTokens());
    }

}