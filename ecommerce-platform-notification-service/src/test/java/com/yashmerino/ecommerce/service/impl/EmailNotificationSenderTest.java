package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.utils.ContactType;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for EmailNotificationSender.
 */
@ExtendWith(MockitoExtension.class)
class EmailNotificationSenderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailNotificationSender emailNotificationSender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailNotificationSender, "APP_EMAIL", "app@example.com");
    }

    @Test
    void testGetContactTypeReturnsEmail() {
        ContactType contactType = emailNotificationSender.getContactType();

        assertEquals(ContactType.EMAIL, contactType);
    }

    @Test
    void testSendSuccess() throws Exception {
        String contact = "user@example.com";
        NotificationContent content = new NotificationContent(
                "Test Subject",
                "Test Body"
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailNotificationSender.send(contact, content);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendWithHtmlContent() throws Exception {
        String contact = "user@example.com";
        NotificationContent content = new NotificationContent(
                "HTML Subject",
                "<html><body><h1>Test</h1></body></html>"
        );

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailNotificationSender.send(contact, content);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendHandlesExceptionGracefully() {
        String contact = "invalid-email";
        NotificationContent content = new NotificationContent(
                "Test Subject",
                "Test Body"
        );

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server error"));

        assertDoesNotThrow(() -> emailNotificationSender.send(contact, content));
        
        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testSendWithEmptyContent() throws Exception {
        String contact = "user@example.com";
        NotificationContent content = new NotificationContent("", "");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailNotificationSender.send(contact, content);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}
