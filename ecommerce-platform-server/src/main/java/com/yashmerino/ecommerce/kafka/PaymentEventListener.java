package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
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
     * Payment repository.
     */
    private final PaymentRepository paymentRepository;

    /**
     * When payment result read - update payment status and send notification event to Kafka notification topic.
     *
     * @param event is the event read from Kafka topic.
     */
    @KafkaListener(
        topics = "payment.result",
        groupId = "main-server"
    )
    public void onPaymentRequested(PaymentResultEvent event) {
        try {
            // Update payment status on server (using paymentId sent from server to payment-service)
            Payment payment = paymentRepository.findById(event.paymentId()).orElse(null);
            if (payment != null) {
                payment.setStatus(event.status());
                paymentRepository.save(payment);
                log.info("Payment status updated to {} for payment ID {} (order ID {})", event.status(), event.paymentId(), event.orderId());
            } else {
                log.warn("Payment not found for payment ID {} (order ID {})", event.paymentId(), event.orderId());
            }

            // Send notification
            notificationEventProducer.sendPaymentNotificationRequested(event);
        } catch (Exception e) {
            log.error("Payment couldn't be processed.", e);
        }
    }
}
