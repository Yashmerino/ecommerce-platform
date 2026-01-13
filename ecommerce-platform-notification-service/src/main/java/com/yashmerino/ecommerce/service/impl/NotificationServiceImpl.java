package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.model.Notification;
import com.yashmerino.ecommerce.repository.NotificationRepository;
import com.yashmerino.ecommerce.service.NotificationSender;
import com.yashmerino.ecommerce.service.NotificationSenderFactory;
import com.yashmerino.ecommerce.service.NotificationService;
import com.yashmerino.ecommerce.utils.NotificationStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Payment service implementation.
 */
@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    /**
     * Notification repository.
     */
    private final NotificationRepository notificationRepository;

    /**
     * Notification sender factory.
     */
    private final NotificationSenderFactory senderFactory;

    /**
     * Sends the notification.
     *
     * @param event is the event from Kafka topic.
     */
    @Override
    @Transactional
    public void sendNotification(NotificationRequestedEvent event) {
        Notification notification = Notification.builder()
                .paymentStatus(event.paymentStatus())
                .contact(event.contact())
                .contactType(event.contactType())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .build();

        notificationRepository.save(notification);

        try {
            NotificationSender sender = senderFactory.getSender(event.contactType().toString());
            sender.send(event);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.from(Instant.now()));
            notificationRepository.save(notification);
        } catch (Exception e) {
            // TODO: Implement retryable???
            notification.setLastError(e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }
}
