package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.service.NotificationSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email implementation of the notification sender.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    @Value("${spring.mail.username}")
    private String APP_EMAIL;

    /**
     * The java mail sender object.
     */
    private final JavaMailSender mailSender;

    /**
     * Sends an email notification.
     *
     * @param event is the Kafka event.
     */
    @Override
    public void send(NotificationRequestedEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            // TODO: Different messages if payment failed. Plus fix the precision for amount.
            helper.setFrom(APP_EMAIL);
            helper.setTo(event.contact());
            helper.setSubject(String.format("Order with ID %d was successfully paid.", event.orderId()));
            helper.setText(String.format("Your order with ID %d was successfully paid by the payment with ID %d.\n\nThe amount of the order was %f€", event.orderId(), event.paymentId(), event.amount()), true);

            mailSender.send(message);
            log.info("Email successfully sent to: {}", event.contact());
        } catch (MessagingException e) {
            log.error("Email failed to be sent to: {}", event.contact(), e);
        }
    }
}
