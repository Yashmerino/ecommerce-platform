package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;

/**
 * Interface for a notification sender.
 */
public interface NotificationSender {

    /**
     * Sends the notification.
     *
     * @param event is the Kafka event.
     */
    void send(NotificationRequestedEvent event);
}
