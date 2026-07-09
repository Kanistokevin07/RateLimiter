package com.ratelimiter.service;

import com.ratelimiter.exception.ClientNotFoundException;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.redis.pubsub.ConfigurationUpdatePublisher;
import com.ratelimiter.repository.BucketRepository;
import com.ratelimiter.repository.ClientConfigRepository;
import com.ratelimiter.service.provider.ClientConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientService {

    private static final Logger logger =
            LoggerFactory.getLogger(ClientService.class);

    private final ClientConfigRepository repository;
    private final ConfigurationUpdatePublisher publisher;
    private final BucketRepository bucketRepository;

    public ClientService(ClientConfigRepository repository, ConfigurationUpdatePublisher publisher,
                         BucketRepository bucketRepository) {
        this.repository = repository;
        this.publisher = publisher;
        this.bucketRepository = bucketRepository;
    }

    public ClientConfig getClient(String clientId) {

        logger.info("Fetching client '{}'.", clientId);
        return repository.findByClientId(clientId)
                .orElseThrow(() ->
                        new ClientNotFoundException(
                                "Client not found: " + clientId
                        ));
    }

    public List<ClientConfig> getAllClients() {
        logger.info("Fetching all clients.");
        return repository.findAll();
    }

    public void createClient(ClientConfig clientConfig) {
        logger.info(
                "Creating client '{}'.",
                clientConfig.clientId()
        );
        repository.save(clientConfig);
    }

    public void updateClient(ClientConfig clientConfig) {
        logger.info(
                "Updating client '{}'.",
                clientConfig.clientId()
        );
        repository.update(clientConfig);
        publisher.publish(clientConfig.clientId());
    }

    public void deleteClient(String clientId) {
        logger.info(
                "Deleting client '{}'.",
                clientId
        );
        repository.delete(clientId);
        publisher.publish(clientId);
    }

    public void resetBucket(String clientId) {
        logger.info(
                "Reset bucket requested for client '{}'.",
                clientId
        );
        repository.findByClientId(clientId)
                .orElseThrow(() ->
                        new ClientNotFoundException(clientId));

        bucketRepository.deleteBucket(clientId);
        logger.info(
                "Bucket reset completed for client '{}'.",
                clientId
        );
    }
}