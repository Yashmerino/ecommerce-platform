package com.yashmerino.ecommerce.model;

/**
 * Notification content.
 *
 * @param subject is the subject.
 * @param body is the body of the notification.
 */
public record NotificationContent(
    String subject,
    String body
) {}
