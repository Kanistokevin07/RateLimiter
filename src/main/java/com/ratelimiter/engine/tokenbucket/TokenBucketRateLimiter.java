package com.ratelimiter.engine.tokenbucket;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.repository.BucketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenBucketRateLimiter implements RateLimiter {

    private static final Logger logger =
            LoggerFactory.getLogger(TokenBucketRateLimiter.class);

    private final BucketRepository bucketRepository;

    public TokenBucketRateLimiter(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    @Override
    public RateLimitDecision allowRequest(
            String clientId,
            ClientConfig clientConfig,
            int tokensRequested) {

        logger.debug(
                "Executing Token Bucket algorithm for client '{}'.",
                clientId
        );

        long currentTime = System.currentTimeMillis() / 1000;

        BucketState bucket = bucketRepository
                .findByClientId(clientId)
                .orElseGet(() -> {
                    logger.debug(
                            "No existing bucket found for client '{}'. Creating a new bucket.",
                            clientId
                    );
                    return new BucketState(
                            clientConfig.capacity(),
                            currentTime
                    );
                });

        logger.debug(
                "Current bucket state for client '{}': availableTokens={}",
                clientId,
                bucket.getAvailableTokens()
        );

        bucket.refill(
                clientConfig.capacity(),
                clientConfig.refillTokensPerSecond(),
                currentTime
        );

        logger.debug(
                "Bucket refilled for client '{}'. AvailableTokens={}",
                clientId,
                bucket.getAvailableTokens()
        );

        boolean allowed = bucket.canConsume(tokensRequested);

        if (allowed) {
            bucket.consume(tokensRequested);

            logger.info(
                    "Request ALLOWED for client '{}'. TokensRequested={}, RemainingTokens={}",
                    clientId,
                    tokensRequested,
                    bucket.getAvailableTokens()
            );
        } else {

            logger.warn(
                    "Request DENIED for client '{}'. TokensRequested={}, RemainingTokens={}",
                    clientId,
                    tokensRequested,
                    bucket.getAvailableTokens()
            );
        }

        bucketRepository.save(clientId, bucket);

        long retryAfterMillis = allowed
                ? 0
                : 1000 / clientConfig.refillTokensPerSecond();

        return new RateLimitDecision(
                allowed,
                clientConfig.capacity(),
                bucket.getAvailableTokens(),
                (currentTime + 1) * 1000,
                retryAfterMillis
        );
    }
}