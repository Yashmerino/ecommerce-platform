package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.model.Notification;
import com.yashmerino.ecommerce.repository.NotificationRepository;
import com.yashmerino.ecommerce.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
     * Sends the notification.
     *
     * @param event is the event from Kafka topic.
     */
    @Override
    @Transactional
    public void sendNotification(NotificationRequestedEvent event) {
        try {
            // TODO: Send notification.
        } catch (Exception e) {
            // TODO: Save the notification or log the error???
        }
    }
}
