package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.NotificationRequestedEvent;
import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.model.Order;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.outbox.OutboxService;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.utils.ContactType;
import com.yashmerino.ecommerce.utils.NotificationType;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationEventProducer.
 */
@ExtendWith(MockitoExtension.class)
class NotificationEventProducerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OutboxService outboxService;

    private NotificationEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new NotificationEventProducer(paymentRepository, outboxService);
    }

    @Test
    void testSendPaymentNotificationRequestedSuccess() {
        Long orderId = 1L;
        Long paymentId = 100L;
        PaymentResultEvent event = new PaymentResultEvent("result-1", orderId, paymentId, PaymentStatus.SUCCEEDED, null);

        User user = new User();
        user.setEmail("test@example.com");

        Order order = new Order();
        order.setUser(user);

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setAmount(BigDecimal.valueOf(99.99));
        payment.setOrder(order);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        producer.sendPaymentNotificationRequested(event);

        ArgumentCaptor<NotificationRequestedEvent> eventCaptor = ArgumentCaptor.forClass(NotificationRequestedEvent.class);
        verify(outboxService).enqueue(eq("notification.requested"), eq(paymentId.toString()), eventCaptor.capture());

        NotificationRequestedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(NotificationType.PAYMENT_SUCCESS, capturedEvent.notificationType());
        assertEquals(ContactType.EMAIL, capturedEvent.contactType());
        assertEquals("test@example.com", capturedEvent.contact());
        assertNotNull(capturedEvent.payload());
    }

    @Test
    void testSendPaymentNotificationRequestedFailed() {
        Long orderId = 1L;
        Long paymentId = 100L;
        PaymentResultEvent event = new PaymentResultEvent("result-2", orderId, paymentId, PaymentStatus.FAILED, "Error");

        User user = new User();
        user.setEmail("test@example.com");

        Order order = new Order();
        order.setUser(user);

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setAmount(BigDecimal.valueOf(99.99));
        payment.setOrder(order);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        producer.sendPaymentNotificationRequested(event);

        ArgumentCaptor<NotificationRequestedEvent> eventCaptor = ArgumentCaptor.forClass(NotificationRequestedEvent.class);
        verify(outboxService).enqueue(eq("notification.requested"), eq(paymentId.toString()), eventCaptor.capture());

        NotificationRequestedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(NotificationType.PAYMENT_FAILED, capturedEvent.notificationType());
    }

    @Test
    void testSendPaymentNotificationRequestedPaymentNotFound() {
        Long orderId = 1L;
        Long paymentId = 100L;
        PaymentResultEvent event = new PaymentResultEvent("result-3", orderId, paymentId, PaymentStatus.SUCCEEDED, null);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        producer.sendPaymentNotificationRequested(event);

        verifyNoInteractions(outboxService);
    }

    @Test
    void testSendWelcomeNotificationRequested() {
        String email = "newuser@example.com";

        producer.sendWelcomeNotificationRequested(email);

        ArgumentCaptor<NotificationRequestedEvent> eventCaptor = ArgumentCaptor.forClass(NotificationRequestedEvent.class);
        verify(outboxService).enqueue(eq("notification.requested"), eq(email), eventCaptor.capture());

        NotificationRequestedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(NotificationType.USER_REGISTERED, capturedEvent.notificationType());
        assertEquals(ContactType.EMAIL, capturedEvent.contactType());
        assertEquals(email, capturedEvent.contact());
    }
}
