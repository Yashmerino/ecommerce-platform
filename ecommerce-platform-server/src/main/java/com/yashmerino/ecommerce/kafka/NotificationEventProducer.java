package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.utils.ContactType;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
     * Sends a notification request event to Kafka topic.
     *
     * @param event is the Kafka event object.
     */
    public void sendNotificationRequested(PaymentResultEvent event) {
        Payment payment = this.paymentRepository.findByOrderId(event.orderId());
        User user = payment.getOrder().getUser();

        NotificationRequestedEvent notificationRequestedEvent = new NotificationRequestedEvent(payment.getId(), event.orderId(), payment.getAmount(), ContactType.EMAIL, user.getEmail(), event.status());

        kafkaTemplate.send("notification.requested", event.orderId().toString(), notificationRequestedEvent);
    }
}
