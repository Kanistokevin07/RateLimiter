package com.ratelimiter.integration.postgres;

import com.ratelimiter.config.DatabaseConfig;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionIT {

    private static DatabaseConfig databaseConfig;
    private static DataSource dataSource;

    @BeforeAll
    static void setupTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @BeforeAll
    static void setup() {

        databaseConfig = new DatabaseConfig(
                "jdbc:postgresql://localhost:5432/rate_limiter",
                "postgres",
                "root"
        );

        dataSource = databaseConfig.dataSource();
    }

    @AfterAll
    static void tearDown() {
        databaseConfig.shutdown();
    }

    @Test
    void shouldCreateConnection() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            assertNotNull(connection);
            assertFalse(connection.isClosed());

        }
    }

    @Test
    void shouldExecuteSqlQuery() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement("SELECT 1");

            ResultSet rs = statement.executeQuery();

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        }
    }
}