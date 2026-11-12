package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.inbox.InboxEvent;
import com.yashmerino.ecommerce.inbox.InboxEventRepository;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    private final InboxEventRepository inboxEventRepository;

    /**
     * When payment result read - update payment status and send notification event to Kafka notification topic.
     *
     * @param event is the event read from Kafka topic.
     */
    @KafkaListener(
        topics = "payment.result",
        groupId = "main-server"
    )
    @Transactional
    public void onPaymentRequested(PaymentResultEvent event) {
        String eventId = event.eventId() != null
                ? event.eventId()
                : "payment-result:" + event.paymentId() + ":" + event.status();
        if (inboxEventRepository.existsById(eventId)) {
            log.info("Ignoring duplicate payment result event {}", eventId);
            return;
        }

        inboxEventRepository.saveAndFlush(new InboxEvent(eventId, "payment.result"));

        Payment payment = paymentRepository.findById(event.paymentId())
                .orElseThrow(() -> new IllegalStateException("payment_not_found:" + event.paymentId()));
        payment.setStatus(event.status());
        paymentRepository.save(payment);
        notificationEventProducer.sendPaymentNotificationRequested(event);
        log.info("Payment status updated to {} for payment ID {} (order ID {})", event.status(), event.paymentId(), event.orderId());
    }
}
