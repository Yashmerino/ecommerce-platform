package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka payment events listener.
 */
@Component
@RequiredArgsConstructor
@Slf4j
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
        try {
            notificationEventProducer.sendPaymentNotificationRequested(event);
        } catch (Exception e) {
            log.error("Payment couldn't be processed.", e);
        }
    }
}
