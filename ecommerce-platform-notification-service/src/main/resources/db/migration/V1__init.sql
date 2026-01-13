CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    contact VARCHAR(255) NOT NULL,
    contact_type VARCHAR(50) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payload LONGTEXT NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    last_error VARCHAR(500),
    sent_at DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
