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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Payment service implementation.
 */
@Service
@RequiredArgsConstructor
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
            sendNotificationWithRetry(notification, event);
        } catch (Exception e) {
            log.error("Notification failed after all retry attempts", e);
        }
    }

    /**
     * Sends notification with automatic retry on failure.
     * Uses Spring Retry for exponential backoff (1s, 2s, 4s).
     *
     * @param notification the notification entity
     * @param event the notification event
     */
    @Retryable(
        retryFor = Exception.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2),
        listeners = {"retryListener"}
    )
    private void sendNotificationWithRetry(Notification notification, NotificationRequestedEvent event) {
        notification.setRetryCount(notification.getRetryCount() + 1);
        
        log.info("Attempting to send notification (attempt {})", notification.getRetryCount());

        NotificationTemplate template = templates.get(event.notificationType());
        NotificationContent content = template.build(event.payload());

        NotificationSender sender = senderFactory.getSender(event.contactType().toString());
        sender.send(event.contact(), content);

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        notificationRepository.save(notification);
        
        log.info("Notification sent successfully after {} attempts", notification.getRetryCount());
    }

    /**
     * Recovery method called after all retry attempts fail.
     *
     * @param e the exception that caused the failure
     * @param notification the notification entity
     * @param event the notification event
     */
    @Recover
    private void handleNotificationFailure(Exception e, Notification notification, NotificationRequestedEvent event) {
        log.error("Notification failed after {} attempts: {}", notification.getRetryCount(), e.getMessage());
        
        notification.setStatus(NotificationStatus.FAILED);
        notification.setLastError(e.getMessage());
        notificationRepository.save(notification);
    }
}
