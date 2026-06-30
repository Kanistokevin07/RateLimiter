package com.ratelimiter.repository.redis;

import com.ratelimiter.engine.tokenbucket.BucketState;
import com.ratelimiter.repository.BucketRepository;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Map;
import java.util.Optional;

public class RedisBucketRepository implements BucketRepository {

    private static final String BUCKET_KEY_PREFIX = "bucket:";

    private static final String AVAILABLE_TOKENS = "availableTokens";
    private static final String LAST_REFILL_TIMESTAMP = "lastRefillTimestamp";

    private final RedisCommands<String, String> redis;

    public RedisBucketRepository(RedisCommands<String, String> redis) {
        this.redis = redis;
    }

    @Override
    public Optional<BucketState> findByClientId(String clientId) {

        String key = BUCKET_KEY_PREFIX + clientId;

        Map<String, String> bucket = redis.hgetall(key);

        if (bucket.isEmpty()) {
            return Optional.empty();
        }

        BucketState bucketState = new BucketState(
                Long.parseLong(bucket.get(AVAILABLE_TOKENS)),
                Long.parseLong(bucket.get(LAST_REFILL_TIMESTAMP))
        );

        return Optional.of(bucketState);
    }

    @Override
    public void save(String clientId, BucketState bucketState) {

        String key = BUCKET_KEY_PREFIX + clientId;

        redis.hset(key, Map.of(
                AVAILABLE_TOKENS,
                String.valueOf(bucketState.getAvailableTokens()),

                LAST_REFILL_TIMESTAMP,
                String.valueOf(bucketState.getLastRefillTimestamp())
        ));
    }
}