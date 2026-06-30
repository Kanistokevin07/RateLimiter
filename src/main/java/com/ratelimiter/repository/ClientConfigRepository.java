package com.ratelimiter.repository;

import com.ratelimiter.model.ClientConfig;

import java.util.List;
import java.util.Optional;

public interface ClientConfigRepository {

    Optional<ClientConfig> findByClientId(String clientId);
    void save(ClientConfig clientConfig);
    void update(ClientConfig clientConfig);
    void delete(String clientId);
    List<ClientConfig> findAll();
}