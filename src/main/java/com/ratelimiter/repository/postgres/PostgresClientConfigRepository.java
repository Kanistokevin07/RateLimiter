package com.ratelimiter.repository.postgres;

import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.model.enums.AlgorithmType;
import com.ratelimiter.repository.ClientConfigRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresClientConfigRepository implements ClientConfigRepository {

    private final DataSource dataSource;

    public PostgresClientConfigRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<ClientConfig> findByClientId(String clientId) {

        String sql = """
                SELECT *
                FROM client_config
                WHERE client_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, clientId);

            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return Optional.empty();
            }

            ClientConfig config = new ClientConfig(
                    rs.getString("client_id"),
                    AlgorithmType.valueOf(rs.getString("algorithm")),
                    rs.getLong("capacity"),
                    rs.getLong("refill_rate"),
                    rs.getLong("window_size_seconds"),
                    rs.getBoolean("enabled")
            );

            return Optional.of(config);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch client configuration.", e);
        }
    }

    @Override
    public void save(ClientConfig clientConfig) {

        String sql = """
                INSERT INTO client_config
                (client_id, algorithm, capacity, refill_rate, window_size_seconds, enabled)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, clientConfig.clientId());
            statement.setString(2, clientConfig.algorithmType().name());
            statement.setLong(3, clientConfig.capacity());
            statement.setLong(4, clientConfig.refillTokensPerSecond());
            statement.setLong(5, clientConfig.windowSizeSeconds());
            statement.setBoolean(6, clientConfig.enabled());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save client configuration.", e);
        }
    }

    @Override
    public void update(ClientConfig clientConfig) {

        String sql = """
                UPDATE client_config
                SET algorithm = ?,
                    capacity = ?,
                    refill_rate = ?,
                    window_size_seconds = ?,
                    enabled = ?
                WHERE client_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, clientConfig.algorithmType().name());
            statement.setLong(2, clientConfig.capacity());
            statement.setLong(3, clientConfig.refillTokensPerSecond());
            statement.setLong(4, clientConfig.windowSizeSeconds());
            statement.setBoolean(5, clientConfig.enabled());
            statement.setString(6, clientConfig.clientId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update client configuration.", e);
        }
    }

    @Override
    public void delete(String clientId) {

        String sql = """
                DELETE FROM client_config
                WHERE client_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, clientId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete client configuration.", e);
        }
    }

    @Override
    public List<ClientConfig> findAll() {

        String sql = """
                SELECT *
                FROM client_config
                """;

        List<ClientConfig> clients = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                ClientConfig config = new ClientConfig(
                        rs.getString("client_id"),
                        AlgorithmType.valueOf(rs.getString("algorithm")),
                        rs.getLong("capacity"),
                        rs.getLong("refill_rate"),
                        rs.getLong("window_size_seconds"),
                        rs.getBoolean("enabled")
                );

                clients.add(config);
            }

            return clients;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch client configurations.", e);
        }
    }
}