package com.ratelimiter.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(DatabaseConfig.class);

    private final HikariDataSource dataSource;

    public DatabaseConfig(
            String jdbcUrl,
            String username,
            String password
    ) {

        logger.info("Initializing PostgreSQL connection pool...");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setAutoCommit(true);

        this.dataSource = new HikariDataSource(config);

        logger.info("PostgreSQL connection pool initialized successfully.");
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public void shutdown() {

        logger.info("Closing PostgreSQL connection pool.");
        dataSource.close();
        logger.info("PostgreSQL connection pool closed.");
    }
}