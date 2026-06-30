package com.ratelimiter.repository;

import com.ratelimiter.engine.tokenbucket.BucketState;

import java.util.Optional;

public interface BucketRepository {

    Optional<BucketState> findByClientId(String clientId);
    void save(String clientId, BucketState bucketState);

}