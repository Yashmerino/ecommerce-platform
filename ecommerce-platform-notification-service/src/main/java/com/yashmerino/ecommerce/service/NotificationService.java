package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;

/**
 * Interface for notification service.
 */
public interface NotificationService {
    /**
     * Sends the notification.
     *
     * @param event is the event from Kafka topic.
     */
    void sendNotification(NotificationRequestedEvent event);
}
