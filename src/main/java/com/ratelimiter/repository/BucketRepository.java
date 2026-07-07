package com.ratelimiter.repository;

import com.ratelimiter.engine.tokenbucket.BucketState;
import com.ratelimiter.model.ClientConfig;

import java.util.List;
import java.util.Optional;

public interface BucketRepository {

    Optional<BucketState> findByClientId(String clientId);
    void save(String clientId, BucketState bucketState);

    List<Long> consumeTokens(
            String clientId,
            ClientConfig config,
            int tokensRequested
    );
}