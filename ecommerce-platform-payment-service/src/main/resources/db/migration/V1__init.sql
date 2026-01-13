CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME,
    updated_at DATETIME,
    order_id BIGINT,
    stripe_payment_id VARCHAR(255),
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
