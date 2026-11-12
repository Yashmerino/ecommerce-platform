ALTER TABLE payments
    ADD COLUMN idempotency_key VARCHAR(255) NULL,
    ADD CONSTRAINT uk_payments_idempotency_key UNIQUE (idempotency_key);