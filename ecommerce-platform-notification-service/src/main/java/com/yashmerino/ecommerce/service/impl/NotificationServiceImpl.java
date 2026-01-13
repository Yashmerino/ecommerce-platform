package com.yashmerino.ecommerce.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.model.Notification;
import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.repository.NotificationRepository;
import com.yashmerino.ecommerce.service.NotificationSender;
import com.yashmerino.ecommerce.service.NotificationSenderFactory;
import com.yashmerino.ecommerce.service.NotificationService;
import com.yashmerino.ecommerce.service.NotificationTemplate;
import com.yashmerino.ecommerce.utils.NotificationStatus;
import com.yashmerino.ecommerce.utils.NotificationType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Payment service implementation.
 */
@Service
@AllArgsConstructor
@Slf4j
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
     * Notifications' templates.
     */
    private final Map<NotificationType, NotificationTemplate> templates;

    /**
     * Sends the notification.
     *
     * @param event is the event from Kafka topic.
     */
    @Override
    @Transactional
    public void sendNotification(NotificationRequestedEvent event) {
        Notification notification = null;
        try {
            notification = Notification.builder()
                    .notificationType(event.notificationType())
                    .contact(event.contact())
                    .contactType(event.contactType())
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
                    .payload(new ObjectMapper().writeValueAsString(event.payload()))
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Notification payload couldn't be converted to JSON.", e);
            return;
        }

        notificationRepository.save(notification);

        try {
            NotificationTemplate template = templates.get(event.notificationType());
            NotificationContent content = template.build(event.payload());

            NotificationSender sender = senderFactory.getSender(event.contactType().toString());
            sender.send(event.contact(), content);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            notificationRepository.save(notification);
        } catch (Exception e) {
            // TODO: Implement retryable???
            log.error("Notification couldn't be sent", e);

            notification.setLastError(e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);
        }
    }
}
