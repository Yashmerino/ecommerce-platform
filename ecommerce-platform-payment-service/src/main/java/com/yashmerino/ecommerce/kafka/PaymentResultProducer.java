package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.outbox.OutboxService;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Kafka component that sends payment result event to topic.
 */
@Component
@RequiredArgsConstructor
public class PaymentResultProducer {

    /**
     * Kafka template.
     */
    private final OutboxService outboxService;

    /**
     * Sends a successful event on kafka topic.
     *
     * @param orderId is the order ID.
     * @param paymentId is the payment ID.
     */
    public void sendSucceeded(Long orderId, Long paymentId) {
        PaymentResultEvent event = new PaymentResultEvent(
            UUID.randomUUID().toString(), orderId, paymentId, PaymentStatus.SUCCEEDED, null
        );
        outboxService.enqueue(event.eventId(), "payment.result", paymentId.toString(), event);
    }

    /**
     * Sends a failed event on kafka topic.
     *
     * @param orderId is the order ID.
     * @param paymentId is the payment ID.
     * @param errorMessage is the error message.
     */
    public void sendFailed(Long orderId, Long paymentId, String errorMessage) {
        PaymentResultEvent event = new PaymentResultEvent(
            UUID.randomUUID().toString(), orderId, paymentId, PaymentStatus.FAILED, errorMessage
        );
        outboxService.enqueue(event.eventId(), "payment.result", paymentId.toString(), event);
    }
}
