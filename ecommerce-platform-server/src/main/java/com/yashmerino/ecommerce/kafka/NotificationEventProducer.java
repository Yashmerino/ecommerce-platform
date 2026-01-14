package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.utils.ContactType;
import com.yashmerino.ecommerce.utils.NotificationType;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka's notification events producer.
 */
@Service
@AllArgsConstructor
public class NotificationEventProducer {

    /**
     * Payment repository.
     */
    private final PaymentRepository paymentRepository;

    /**
     * Kafka template used to send events.
     */
    private final KafkaTemplate<String, NotificationRequestedEvent> kafkaTemplate;

    /**
     * Sends a payment notification request event to Kafka topic.
     *
     * @param event is the Kafka event object.
     */
    public void sendPaymentNotificationRequested(PaymentResultEvent event) {
        Payment payment = this.paymentRepository.findById(event.paymentId()).orElse(null);
        if (payment == null) {
            return;
        }
        User user = payment.getOrder().getUser();

        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentId", payment.getId());
        payload.put("orderId", event.orderId());
        payload.put("amount", payment.getAmount());

        NotificationRequestedEvent notificationRequestedEvent =
                new NotificationRequestedEvent(
                        PaymentStatus.SUCCEEDED.equals(event.status()) ? NotificationType.PAYMENT_SUCCESS : NotificationType.PAYMENT_FAILED,
                        ContactType.EMAIL,
                        user.getEmail(),
                        payload
                );

        kafkaTemplate.send("notification.requested", notificationRequestedEvent);
    }

    /**
     * Sends a welcome notification request event to Kafka topic.
     */
    public void sendWelcomeNotificationRequested(final String email) {
        NotificationRequestedEvent notificationRequestedEvent =
                new NotificationRequestedEvent(
                        NotificationType.USER_REGISTERED,
                        ContactType.EMAIL,
                        email,
                        null
                );

        kafkaTemplate.send("notification.requested", notificationRequestedEvent);
    }
}
