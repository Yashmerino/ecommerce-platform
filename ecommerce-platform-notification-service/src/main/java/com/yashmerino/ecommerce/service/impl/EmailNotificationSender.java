package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.service.NotificationSender;
import com.yashmerino.ecommerce.utils.ContactType;
import jakarta.mail.internet.MimeMessage;
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
     * Returns the contact type for notification.
     *
     * @return the contact type.
     */
    @Override
    public ContactType getContactType() {
        return ContactType.EMAIL;
    }

    /**
     * Sends the notification.
     *
     * @param contact is the contact to send notification to.
     * @param content is the notification content.
     */
    @Override
    public void send(String contact, NotificationContent content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(contact);
            helper.setSubject(content.subject());
            helper.setText(content.body(), true);
            mailSender.send(message);

            log.info("Email to {} was successfully sent.", contact);
        } catch (Exception e) {
            log.error("Email to {} couldn't be sent.", contact, e);
        }
    }
}
