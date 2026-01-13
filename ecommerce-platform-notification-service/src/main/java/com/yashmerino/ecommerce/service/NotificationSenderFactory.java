package com.yashmerino.ecommerce.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationSenderFactory {

    /**
     * Map with all the senders and their types.
     */
    private final Map<String, NotificationSender> senders;

    /**
     * Constructor to inject all senders.
     *
     * @param senderList is the list of sender beans.
     */
    public NotificationSenderFactory(List<NotificationSender> senderList) {
        senders = senderList.stream()
                .collect(Collectors.toMap(s -> s.getClass().getSimpleName(), s -> s));
    }

    /**
     * Returns the corect sender for type.
     *
     * @param type is the type of the sender. Ex: email, sms.
     *
     * @return the correct instance of sender.
     */
    public NotificationSender getSender(String type) {
        return switch (type.toLowerCase()) {
            case "email"-> senders.get("EmailNotificationSender");
            case "sms" -> senders.get("SmsNotificationSender");
            default -> throw new IllegalArgumentException("Unknown notification type");
        };
    }
}
