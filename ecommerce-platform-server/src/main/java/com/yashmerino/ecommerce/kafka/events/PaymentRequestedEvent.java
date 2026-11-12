package com.yashmerino.ecommerce.kafka.events;

import java.math.BigDecimal;

/**
 * Event sent to Kafka topic.
 *
 * @param paymentId is the payment's ID from main server.
 * @param orderId is the order's ID from main server.
 * @param amount is the amount of the payment.
 * @param stripeToken is the stripe token.
 * @param idempotencyKey is the key used to deduplicate the payment request.
 */
public record PaymentRequestedEvent(
        String eventId,
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String stripeToken,
        String idempotencyKey
) {}
