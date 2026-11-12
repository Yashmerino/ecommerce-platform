CREATE TABLE outbox_events (
    event_id VARCHAR(36) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    message_key VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload LONGTEXT NULL,
    created_at DATETIME(6) NOT NULL,
    published_at DATETIME(6) NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    next_attempt_at DATETIME(6) NOT NULL,
    last_error TEXT NULL,
    PRIMARY KEY (event_id),
    KEY idx_outbox_pending (published_at, next_attempt_at, created_at)
) ENGINE=InnoDB;

CREATE TABLE inbox_events (
    event_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    processed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (event_id)
) ENGINE=InnoDB;