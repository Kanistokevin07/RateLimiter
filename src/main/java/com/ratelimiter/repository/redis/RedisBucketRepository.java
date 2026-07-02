package com.ratelimiter.repository.redis;

import com.ratelimiter.engine.tokenbucket.BucketState;
import com.ratelimiter.exception.BucketPersistenceException;
import com.ratelimiter.repository.BucketRepository;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class RedisBucketRepository implements BucketRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(RedisBucketRepository.class);

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

        logger.debug(
                "Loading bucket from Redis for client '{}'.",
                clientId
        );

        try {

            Map<String, String> bucket = redis.hgetall(key);

            if (bucket.isEmpty()) {

                logger.debug(
                        "No bucket found in Redis for client '{}'.",
                        clientId
                );

                return Optional.empty();
            }

            BucketState bucketState = new BucketState(
                    Long.parseLong(bucket.get(AVAILABLE_TOKENS)),
                    Long.parseLong(bucket.get(LAST_REFILL_TIMESTAMP))
            );

            logger.debug(
                    "Bucket loaded successfully for client '{}'. AvailableTokens={}",
                    clientId,
                    bucketState.getAvailableTokens()
            );

            return Optional.of(bucketState);

        } catch (Exception e) {

            logger.error(
                    "Failed to load bucket for client '{}'.",
                    clientId,
                    e
            );

            throw new BucketPersistenceException(
                    "Failed to load bucket for client: " + clientId,
                    e
            );
        }
    }

    @Override
    public void save(String clientId, BucketState bucketState) {

        String key = BUCKET_KEY_PREFIX + clientId;

        logger.debug(
                "Saving bucket to Redis for client '{}'.",
                clientId
        );

        try {

            redis.hset(key, Map.of(
                    AVAILABLE_TOKENS,
                    String.valueOf(bucketState.getAvailableTokens()),
                    LAST_REFILL_TIMESTAMP,
                    String.valueOf(bucketState.getLastRefillTimestamp())
            ));

            logger.debug(
                    "Bucket saved successfully for client '{}'. AvailableTokens={}",
                    clientId,
                    bucketState.getAvailableTokens()
            );

        } catch (Exception e) {

            logger.error(
                    "Failed to save bucket for client '{}'.",
                    clientId,
                    e
            );

            throw new BucketPersistenceException(
                    "Failed to save bucket for client: " + clientId,
                    e
            );
        }
    }
}