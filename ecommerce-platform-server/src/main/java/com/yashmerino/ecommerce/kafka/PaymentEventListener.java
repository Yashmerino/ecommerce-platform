package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka payment events listener.
 */
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    /**
     * Notification event producer.
     */
    private final NotificationEventProducer notificationEventProducer;

    /**
     * When payment result read - send notification event to Kafka notification topic.
     *
     * @param event is the event read from Kafka topic.
     */
    @KafkaListener(
        topics = "payment.result",
        groupId = "main-server"
    )
    public void onPaymentRequested(PaymentResultEvent event) {
        notificationEventProducer.sendNotificationRequested(event);
    }
}
