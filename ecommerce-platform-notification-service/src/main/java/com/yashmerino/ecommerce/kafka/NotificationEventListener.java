package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka notification events listener.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "notification.requested",
        groupId = "notification-service"
    )
    public void onNotificationRequested(NotificationRequestedEvent event) {
        notificationService.sendNotification(event);
    }
}
