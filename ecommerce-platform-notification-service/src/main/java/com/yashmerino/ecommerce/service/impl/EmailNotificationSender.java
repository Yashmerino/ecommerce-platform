package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.service.NotificationSender;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

            helper.setFrom(APP_EMAIL);
            helper.setTo(event.contact());

            if (PaymentStatus.SUCCEEDED.equals(event.paymentStatus())) {
                helper.setSubject(String.format("Order with ID %d was successfully paid.", event.orderId()));
                helper.setText(String.format("Your order with ID %d was successfully paid by the payment with ID %d.\n\nThe amount of the order was %s€", event.orderId(), event.paymentId(), event.amount().setScale(2, RoundingMode.HALF_UP).toPlainString()), true);
            } else {
                helper.setSubject(String.format("Order with ID %d failed to be paid.", event.orderId()));
                helper.setText(String.format("Your order with ID %d failed to be paid by the payment with ID %d. Please retry in your orders page", event.orderId(), event.paymentId()), true);
            }
            mailSender.send(message);
            log.info("Email successfully sent to: {}", event.contact());
        } catch (MessagingException e) {
            log.error("Email failed to be sent to: {}", event.contact(), e);
        }
    }
}
