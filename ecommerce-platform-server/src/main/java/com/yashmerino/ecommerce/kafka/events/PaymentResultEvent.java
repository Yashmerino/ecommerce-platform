package com.yashmerino.ecommerce.kafka.events;

import com.yashmerino.ecommerce.utils.PaymentStatus;

/**
 * Payment result event read from Kafka topic.
 *
 * @param orderId is the order's ID.
 * @param status is the payment's status.
 * @param errorMessage is the error message if payment failed.
 */
public record PaymentResultEvent(
        Long orderId,
        PaymentStatus status,
        String errorMessage
) {}
