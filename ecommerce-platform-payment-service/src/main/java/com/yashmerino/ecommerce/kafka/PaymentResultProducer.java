package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka component that sends payment result event to topic.
 */
@Component
@RequiredArgsConstructor
public class PaymentResultProducer {

    /**
     * Kafka template.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a successful event on kafka topic.
     *
     * @param orderId is the order ID.
     * @param paymentId is the payment ID.
     */
    public void sendSucceeded(Long orderId, Long paymentId) {
        kafkaTemplate.send(
                "payment.result",
            new PaymentResultEvent(orderId, paymentId, PaymentStatus.SUCCEEDED, null)
        );
    }

    /**
     * Sends a failed event on kafka topic.
     *
     * @param orderId is the order ID.
     * @param paymentId is the payment ID.
     * @param errorMessage is the error message.
     */
    public void sendFailed(Long orderId, Long paymentId, String errorMessage) {
        kafkaTemplate.send(
            "payment.result",
            new PaymentResultEvent(orderId, paymentId, PaymentStatus.FAILED, errorMessage)
        );
    }
}
