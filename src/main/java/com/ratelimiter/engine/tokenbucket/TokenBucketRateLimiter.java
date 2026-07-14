package com.ratelimiter.engine.tokenbucket;

import com.ratelimiter.engine.RateLimiter;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.RateLimitDecision;
import com.ratelimiter.redis.LuaScriptExecutor;
import com.ratelimiter.repository.BucketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
            ClientConfig config,
            int tokensRequested
    ) {

        long currentTime =
                System.currentTimeMillis() / 1000;

        List<Long> result =
                bucketRepository.consumeTokens(
                        clientId,
                        config,
                        tokensRequested
                );

        logger.info("Lua Result = {}", result);

        boolean allowed = result.get(0) == 1;

        long remainingTokens = result.get(1);

        long lastRefill = result.get(2);

        long retryAfterMillis =
                allowed
                        ? 0
                        : 1000 / config.refillTokensPerSecond();

        return new RateLimitDecision(
                allowed,
                config.capacity(),
                remainingTokens,
                (lastRefill + 1) * 1000,
                retryAfterMillis
        );
    }
}