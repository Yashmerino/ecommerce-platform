package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka notification events listener.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "notification.requested",
        groupId = "notification-service"
    )
    public void onNotificationRequested(NotificationRequestedEvent event) {
        try {
            notificationService.sendNotification(event);
        } catch (Exception e) {
            log.error("Notification couldn't be processed.", e);
        }
    }
}
