package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka's payment events producer.
 */
@Service
public class PaymentEventProducer {

    /**
     * Kafka template used to send events.
     */
    private final KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate;

    /**
     * Constructor to inject dependencies.
     *
     * @param kafkaTemplate is the kafka template used to send events.
     */
    public PaymentEventProducer(
            KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a payment request event to Kafka topic.
     *
     * @param event is the Kafka event object.
     */
    public void sendPaymentRequested(PaymentRequestedEvent event) {
        kafkaTemplate.send("payment.requested", event.orderId().toString(), event);
    }
}
