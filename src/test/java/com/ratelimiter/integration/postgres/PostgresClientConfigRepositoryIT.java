package com.ratelimiter.integration.postgres;

import com.ratelimiter.config.DatabaseConfig;
import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.postgres.PostgresClientConfigRepository;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class PostgresClientConfigRepositoryIT {

    private DatabaseConfig databaseConfig;
    private DataSource dataSource;
    private PostgresClientConfigRepository repository;

    @BeforeAll
    static void setupTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @BeforeEach
    void setup() throws Exception {

        databaseConfig = new DatabaseConfig(
                "jdbc:postgresql://localhost:5432/rate_limiter",
                "postgres",
                "root"
        );

        dataSource = databaseConfig.dataSource();

        repository = new PostgresClientConfigRepository(dataSource);

        try (Connection connection = dataSource.getConnection()) {

            Statement stmt = connection.createStatement();

            stmt.executeUpdate("DELETE FROM client_config");

        }

    }

    @AfterEach
    void cleanup() {
        databaseConfig.shutdown();
    }

    private ClientConfig client1() {

        return new ClientConfig(
                "client-1",
                AlgorithmType.TOKEN_BUCKET,
                100,
                10,
                60,
                true
        );
    }

    @Test
    void shouldSaveClient() {

        repository.save(client1());

        Optional<ClientConfig> result =
                repository.findByClientId("client-1");

        assertTrue(result.isPresent());

        assertEquals(client1(), result.get());

    }

    @Test
    void shouldReturnClientById() {

        repository.save(client1());

        ClientConfig result =
                repository.findByClientId("client-1").get();

        assertEquals("client-1", result.clientId());

    }

    @Test
    void shouldReturnEmptyWhenClientDoesNotExist() {

        assertTrue(
                repository.findByClientId("abc").isEmpty()
        );

    }

    @Test
    void shouldReturnAllClients() {

        repository.save(client1());

        repository.save(
                new ClientConfig(
                        "client-2",
                        AlgorithmType.TOKEN_BUCKET,
                        50,
                        5,
                        60,
                        true
                )
        );

        List<ClientConfig> clients =
                repository.findAll();

        assertEquals(2, clients.size());

    }

    @Test
    void shouldUpdateClient() {

        repository.save(client1());

        ClientConfig updated =
                new ClientConfig(
                        "client-1",
                        AlgorithmType.TOKEN_BUCKET,
                        500,
                        25,
                        120,
                        false
                );

        repository.update(updated);

        ClientConfig result =
                repository.findByClientId("client-1").get();

        assertEquals(500, result.capacity());
        assertEquals(25, result.refillTokensPerSecond());
        assertEquals(120, result.windowSizeSeconds());
        assertFalse(result.enabled());

    }

    @Test
    void shouldDeleteClient() {

        repository.save(client1());

        repository.delete("client-1");

        assertTrue(
                repository.findByClientId("client-1").isEmpty()
        );

    }

    @Test
    void shouldReturnEmptyList() {

        assertTrue(
                repository.findAll().isEmpty()
        );

    }

}