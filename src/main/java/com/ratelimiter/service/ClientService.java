package com.ratelimiter.service;

import com.ratelimiter.exception.ClientNotFoundException;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.repository.ClientConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClientService {

    private static final Logger logger =
            LoggerFactory.getLogger(ClientService.class);

    private final ClientConfigRepository repository;

    public ClientService(ClientConfigRepository repository) {
        this.repository = repository;
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
    }

    public void deleteClient(String clientId) {
        logger.info(
                "Deleting client '{}'.",
                clientId
        );
        repository.delete(clientId);
    }
}