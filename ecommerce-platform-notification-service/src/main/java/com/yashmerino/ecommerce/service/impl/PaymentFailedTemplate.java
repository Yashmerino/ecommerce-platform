package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.service.NotificationTemplate;
import com.yashmerino.ecommerce.utils.NotificationType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The payment failed notification.
 */
@Service
public class PaymentFailedTemplate implements NotificationTemplate {

    /**
     * Get the Notification Type of the template.
     *
     * @return the notification type.
     */
    @Override
    public NotificationType getType() {
        return NotificationType.PAYMENT_FAILED;
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
        Double amount = (Double) data.get("amount");
        Integer orderId = (Integer) data.get("orderId");
        Integer paymentId = (Integer) data.get("paymentId");

        return new NotificationContent(
            String.format("Payment with ID %s failed", paymentId),
            String.format("Your payment of " + amount + " € for order with ID %s failed. Please retry in your orders page", orderId)
        );
    }
}
