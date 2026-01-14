package com.yashmerino.ecommerce.kafka.events;

import com.yashmerino.ecommerce.utils.PaymentStatus;

/**
 * Payment result event read from Kafka topic.
 *
 * @param orderId is the order's ID.
 * @param paymentId is the payment's ID.
 * @param status is the payment's status.
 * @param errorMessage is the error message if payment failed.
 */
public record PaymentResultEvent(
        Long orderId,
        Long paymentId,
        PaymentStatus status,
        String errorMessage
) {}
