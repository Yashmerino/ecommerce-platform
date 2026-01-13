package com.yashmerino.ecommerce.kafka.events;

import com.yashmerino.ecommerce.utils.ContactType;

import java.math.BigDecimal;

/**
 * Event sent to Kafka topic.
 *
 * @param paymentId is the payment's ID from main server.
 * @param orderId is the order's ID from main server.
 * @param amount is the amount of the payment.
 * @param contactType is the contact type. Ex: email, sms
 * @param contact is the contact data.
 */
public record NotificationRequestedEvent(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        ContactType contactType,
        String contact
) {}
