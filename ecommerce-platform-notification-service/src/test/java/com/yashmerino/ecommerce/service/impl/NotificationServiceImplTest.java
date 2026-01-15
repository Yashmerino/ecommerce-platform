package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.model.Notification;
import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.repository.NotificationRepository;
import com.yashmerino.ecommerce.service.NotificationSender;
import com.yashmerino.ecommerce.service.NotificationSenderFactory;
import com.yashmerino.ecommerce.service.NotificationTemplate;
import com.yashmerino.ecommerce.utils.ContactType;
import com.yashmerino.ecommerce.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for NotificationServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationSenderFactory senderFactory;

    @Mock
    private Map<NotificationType, NotificationTemplate> templates;

    @Mock
    private NotificationSender notificationSender;

    @Mock
    private NotificationTemplate notificationTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationRequestedEvent testEvent;
    private Map<String, Object> payload;

    @BeforeEach
    void setUp() {
        payload = new HashMap<>();
        payload.put("amount", 100.0);
        payload.put("orderId", 1);
        payload.put("paymentId", 123);

        testEvent = new NotificationRequestedEvent(
                NotificationType.PAYMENT_SUCCESS,
                ContactType.EMAIL,
                "test@example.com",
                payload
        );
    }

    @Test
    void testSendNotificationSuccess() {
        NotificationContent content = new NotificationContent(
                "Payment Successful",
                "Your payment was successful"
        );

        when(templates.get(NotificationType.PAYMENT_SUCCESS)).thenReturn(notificationTemplate);
        when(notificationTemplate.build(payload)).thenReturn(content);
        when(senderFactory.getSender("EMAIL")).thenReturn(notificationSender);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        notificationService.sendNotification(testEvent);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, atLeast(1)).save(notificationCaptor.capture());
        verify(notificationSender).send("test@example.com", content);

        Notification savedNotification = notificationCaptor.getValue();
        assertNotNull(savedNotification);
        assertEquals(NotificationType.PAYMENT_SUCCESS, savedNotification.getNotificationType());
        assertEquals("test@example.com", savedNotification.getContact());
        assertEquals(ContactType.EMAIL, savedNotification.getContactType());
    }

    @Test
    void testSendNotificationTemplateNotFound() {
        when(templates.get(NotificationType.PAYMENT_SUCCESS)).thenReturn(null);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> notificationService.sendNotification(testEvent));
        
        verify(notificationRepository, atLeast(1)).save(any(Notification.class));
    }

    @Test
    void testSendNotificationSenderThrowsException() {
        NotificationContent content = new NotificationContent(
                "Payment Successful",
                "Your payment was successful"
        );

        when(templates.get(NotificationType.PAYMENT_SUCCESS)).thenReturn(notificationTemplate);
        when(notificationTemplate.build(payload)).thenReturn(content);
        when(senderFactory.getSender("EMAIL")).thenReturn(notificationSender);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);
        doThrow(new RuntimeException("Email service unavailable")).when(notificationSender)
                .send(anyString(), any(NotificationContent.class));

        assertDoesNotThrow(() -> notificationService.sendNotification(testEvent));
        
        verify(notificationRepository, atLeast(1)).save(any(Notification.class));
    }

    @Test
    void testSendNotificationWithDifferentContactType() {
        NotificationRequestedEvent smsEvent = new NotificationRequestedEvent(
                NotificationType.USER_REGISTERED,
                ContactType.SMS,
                "+1234567890",
                payload
        );

        NotificationContent content = new NotificationContent(
                "Welcome",
                "Welcome to our platform"
        );

        when(templates.get(NotificationType.USER_REGISTERED)).thenReturn(notificationTemplate);
        when(notificationTemplate.build(payload)).thenReturn(content);
        when(senderFactory.getSender("SMS")).thenReturn(notificationSender);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArguments()[0]);

        notificationService.sendNotification(smsEvent);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, atLeast(1)).save(notificationCaptor.capture());
        verify(notificationSender).send("+1234567890", content);

        Notification savedNotification = notificationCaptor.getValue();
        assertEquals(ContactType.SMS, savedNotification.getContactType());
        assertEquals("+1234567890", savedNotification.getContact());
    }
}
