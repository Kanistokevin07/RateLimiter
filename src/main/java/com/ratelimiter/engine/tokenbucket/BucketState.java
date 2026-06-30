package com.ratelimiter.engine.tokenbucket;

/**
 * Represents the runtime state of a client's token bucket.
 *
 * This class owns the bucket's state and behavior.
 * It is responsible for maintaining its own invariants.
 */
public class BucketState {

    private long availableTokens;
    private long lastRefillTimestamp;

    public BucketState(long availableTokens, long lastRefillTimestamp) {
        this.availableTokens = availableTokens;
        this.lastRefillTimestamp = lastRefillTimestamp;
    }

    /**
     * Refills the bucket based on the elapsed time.
     *
     * @param capacity Maximum bucket capacity
     * @param refillRatePerSecond Tokens added per second
     * @param currentTimestamp Current timestamp (in seconds)
     */
    public void refill(long capacity,
                       long refillRatePerSecond,
                       long currentTimestamp) {

        if (currentTimestamp <= lastRefillTimestamp) {
            return;
        }

        long elapsedTime = currentTimestamp - lastRefillTimestamp;
        long tokensToAdd = elapsedTime * refillRatePerSecond;

        availableTokens = Math.min(
                capacity,
                availableTokens + tokensToAdd
        );

        lastRefillTimestamp = currentTimestamp;
    }

    /**
     * Checks whether the bucket has enough tokens.
     */
    public boolean canConsume(long tokensRequested) {
        return availableTokens >= tokensRequested;
    }

    /**
     * Consumes tokens from the bucket.
     *
     * @throws IllegalStateException if insufficient tokens are available.
     */
    public void consume(long tokensRequested) {

        if (!canConsume(tokensRequested)) {
            throw new IllegalStateException(
                    "Insufficient tokens available."
            );
        }

        availableTokens -= tokensRequested;
    }

    public long getAvailableTokens() {
        return availableTokens;
    }

    public long getLastRefillTimestamp() {
        return lastRefillTimestamp;
    }
}