package com.ratelimiter.service;

import com.ratelimiter.exception.ClientNotFoundException;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.ClientConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientConfigRepository repository;

    @InjectMocks
    private ClientService clientService;

    private final ClientConfig clientConfig =
            new ClientConfig(
                    "client123",
                    AlgorithmType.TOKEN_BUCKET,
                    10,
                    2,
                    60,
                    true
            );

    @Test
    @DisplayName("Should return client when client exists")
    void shouldReturnClient() {

        // Arrange
        when(repository.findByClientId("client123"))
                .thenReturn(Optional.of(clientConfig));

        // Act
        ClientConfig result =
                clientService.getClient("client123");

        // Assert
        assertEquals(clientConfig, result);

        verify(repository)
                .findByClientId("client123");
    }

    @Test
    @DisplayName("Should throw exception when client does not exist")
    void shouldThrowClientNotFoundException() {

        // Arrange
        when(repository.findByClientId("client123"))
                .thenReturn(Optional.empty());

        // Act & Assert
        ClientNotFoundException exception =
                assertThrows(
                        ClientNotFoundException.class,
                        () -> clientService.getClient("client123")
                );

        assertEquals(
                "Client not found: client123",
                exception.getMessage()
        );

        verify(repository)
                .findByClientId("client123");
    }

    @Test
    @DisplayName("Should return all clients")
    void shouldReturnAllClients() {

        // Arrange
        when(repository.findAll())
                .thenReturn(List.of(clientConfig));

        // Act
        List<ClientConfig> result =
                clientService.getAllClients();

        // Assert
        assertEquals(1, result.size());
        assertEquals(clientConfig, result.get(0));

        verify(repository)
                .findAll();
    }

    @Test
    @DisplayName("Should create client")
    void shouldCreateClient() {

        // Act
        clientService.createClient(clientConfig);

        // Assert
        verify(repository)
                .save(clientConfig);
    }

    @Test
    @DisplayName("Should update client")
    void shouldUpdateClient() {

        // Act
        clientService.updateClient(clientConfig);

        // Assert
        verify(repository)
                .update(clientConfig);
    }

    @Test
    @DisplayName("Should delete client")
    void shouldDeleteClient() {

        // Act
        clientService.deleteClient("client123");

        // Assert
        verify(repository)
                .delete("client123");
    }

}