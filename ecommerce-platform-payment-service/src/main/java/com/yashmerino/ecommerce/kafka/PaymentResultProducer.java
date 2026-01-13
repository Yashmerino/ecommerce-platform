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
     */
    public void sendSucceeded(Long orderId) {
        kafkaTemplate.send(
                "payment.result",
            new PaymentResultEvent(orderId, PaymentStatus.SUCCEEDED, null)
        );
    }

    public void sendFailed(Long orderId, String errorMessage) {
        kafkaTemplate.send(
            "payment.result",
            new PaymentResultEvent(orderId, PaymentStatus.FAILED, errorMessage)
        );
    }
}
