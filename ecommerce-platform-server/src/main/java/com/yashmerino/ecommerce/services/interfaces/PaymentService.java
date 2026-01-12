package com.yashmerino.ecommerce.services.interfaces;

/**
 * Interface for payment service.
 */
public interface PaymentService {
    /**
     * Sends an event to Kafka topic to process the payment for an order.
     *
     * @param orderId is the payment's order ID.
     */
    void pay(final Long orderId);
}
