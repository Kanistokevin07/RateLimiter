CREATE TABLE IF NOT EXISTS client_config (

    client_id VARCHAR(255) PRIMARY KEY,

    algorithm VARCHAR(50) NOT NULL,

    capacity BIGINT NOT NULL,

    refill_rate BIGINT NOT NULL,

    window_size_seconds BIGINT NOT NULL,

    enabled BOOLEAN NOT NULL
);