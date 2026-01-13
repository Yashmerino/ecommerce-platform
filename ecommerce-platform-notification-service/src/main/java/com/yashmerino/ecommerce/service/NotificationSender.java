package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.utils.ContactType;
import com.yashmerino.ecommerce.utils.NotificationType;

/**
 * Interface for a notification sender.
 */
public interface NotificationSender {

    /**
     * Returns the contact type for notification.
     *
     * @return the contact type.
     */
    ContactType getContactType();

    /**
     * Sends the notification.
     *
     * @param contact is the contact to send notification to.
     * @param content is the notification content.
     */
    void send(String contact, NotificationContent content);
}
