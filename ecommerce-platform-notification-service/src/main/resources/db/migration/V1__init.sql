CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME,
    updated_at DATETIME,
    contact VARCHAR(255) NOT NULL,
    contact_type VARCHAR(50) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payload TEXT,
    retry_count INT NOT NULL DEFAULT 0,
    last_error VARCHAR(500),
    sent_at DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
