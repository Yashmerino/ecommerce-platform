ALTER TABLE payments
    ADD COLUMN payment_id BIGINT NULL,
    ADD CONSTRAINT uk_payments_payment_id UNIQUE (payment_id);