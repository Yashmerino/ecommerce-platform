package com.yashmerino.ecommerce.service;

import com.yashmerino.ecommerce.service.impl.EmailNotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest(name = "getSender(\"{0}\") should return EmailNotificationSender")
    @ValueSource(strings = {"email", "EMAIL", "EmAiL"})
    void testGetSenderEmailReturnsEmailSenderRegardlessOfCase(String type) {
        NotificationSender sender = factory.getSender(type);

        assertNotNull(sender);
        assertInstanceOf(EmailNotificationSender.class, sender);
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
