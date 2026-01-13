package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;

/**
 * Interface for payment service.
 */
public interface PaymentService {
    /**
     * Processes the payment.
     *
     * @param event is the event from Kafka topic.
     */
    void processPayment(PaymentRequestedEvent event);
}
