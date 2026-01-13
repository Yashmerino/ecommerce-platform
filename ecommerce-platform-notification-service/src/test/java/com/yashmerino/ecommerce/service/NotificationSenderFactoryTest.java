package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.service.impl.EmailNotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Test class for NotificationSenderFactory.
 */
class NotificationSenderFactoryTest {

    private NotificationSenderFactory factory;

    @BeforeEach
    void setUp() {
        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        NotificationSender emailSender = new EmailNotificationSender(mockMailSender);
        
        List<NotificationSender> senderList = Arrays.asList(emailSender);
        factory = new NotificationSenderFactory(senderList);
    }

    @Test
    void testGetSenderEmailTypeReturnsEmailSender() {
        NotificationSender sender = factory.getSender("email");

        assertNotNull(sender);
        assertTrue(sender instanceof EmailNotificationSender);
    }

    @Test
    void testGetSenderEmailUpperCaseReturnsEmailSender() {
        NotificationSender sender = factory.getSender("EMAIL");

        assertNotNull(sender);
        assertTrue(sender instanceof EmailNotificationSender);
    }

    @Test
    void testGetSenderEmailMixedCaseReturnsEmailSender() {
        NotificationSender sender = factory.getSender("EmAiL");

        assertNotNull(sender);
        assertTrue(sender instanceof EmailNotificationSender);
    }

    @Test
    void testGetSenderUnknownTypeThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getSender("unknown")
        );

        assertEquals("Unknown notification type", exception.getMessage());
    }

    @Test
    void testGetSenderNullTypeThrowsException() {
        assertThrows(
                NullPointerException.class,
                () -> factory.getSender(null)
        );
    }

    @Test
    void testGetSenderEmptyTypeThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> factory.getSender("")
        );
    }
}
