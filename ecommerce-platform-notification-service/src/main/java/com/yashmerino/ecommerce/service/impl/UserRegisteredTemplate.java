package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.service.NotificationTemplate;
import com.yashmerino.ecommerce.utils.NotificationType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The user registered notification template.
 */
@Service
public class UserRegisteredTemplate implements NotificationTemplate {

    /**
     * Get the Notification Type of the template.
     *
     * @return the notification type.
     */
    @Override
    public NotificationType getType() {
        return NotificationType.USER_REGISTERED;
    }

    /**
     * Builds the notification content using data from event.
     *
     * @param data is the data sent from event.
     *
     * @return the notification content.
     */
    @Override
    public NotificationContent build(Map<String, Object> data) {
        return new NotificationContent(
                "Welcome to Ecommerce Platform!",
                "We're happy to see you using our platform."
        );
    }
}
