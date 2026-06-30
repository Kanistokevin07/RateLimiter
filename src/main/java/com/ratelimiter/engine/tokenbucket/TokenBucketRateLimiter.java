package com.ratelimiter.engine.tokenbucket;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.repository.BucketRepository;

public class TokenBucketRateLimiter implements RateLimiter {

    private final BucketRepository bucketRepository;

    public TokenBucketRateLimiter(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    @Override
    public RateLimitDecision allowRequest(
            String clientId,
            ClientConfig clientConfig,
            int tokensRequested) {

        long currentTime = System.currentTimeMillis() / 1000;

        BucketState bucket = bucketRepository
                .findByClientId(clientId)
                .orElse(new BucketState(
                        clientConfig.capacity(),
                        currentTime
                ));

        bucket.refill(
                clientConfig.capacity(),
                clientConfig.refillTokensPerSecond(),
                currentTime
        );

        boolean allowed = bucket.canConsume(tokensRequested);

        if (allowed) {
            bucket.consume(tokensRequested);
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