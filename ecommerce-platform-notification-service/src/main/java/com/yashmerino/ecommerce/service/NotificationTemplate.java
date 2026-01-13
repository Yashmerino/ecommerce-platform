package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.utils.NotificationType;

import java.util.Map;

/**
 * Notification template.
 */
public interface NotificationTemplate {
    /**
     * Returns the type of the notification.
     *
     * @return the type of the notification.
     */
    NotificationType getType();

    /**
     * Builds the content of the notification using the data from event.
     *
     * @param data is the data sent from event.
     *
     * @return The Notification Content.
     */
    NotificationContent build(Map<String, Object> data);
}
