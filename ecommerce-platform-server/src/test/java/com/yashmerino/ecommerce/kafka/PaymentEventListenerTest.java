package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.inbox.InboxEventRepository;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentEventListener.
 */
@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private NotificationEventProducer notificationEventProducer;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InboxEventRepository inboxEventRepository;

    private PaymentEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new PaymentEventListener(notificationEventProducer, paymentRepository, inboxEventRepository);
    }

    @Test
    void testOnPaymentRequestedSuccessfulUpdate() {
        Long orderId = 1L;
        Long paymentId = 100L;
        PaymentResultEvent event = new PaymentResultEvent("result-1", orderId, paymentId, PaymentStatus.SUCCEEDED, null);

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        listener.onPaymentRequested(event);

        verify(inboxEventRepository).saveAndFlush(any());
        verify(paymentRepository).save(payment);
        verify(notificationEventProducer).sendPaymentNotificationRequested(event);
    }

    @Test
    void testOnPaymentRequestedPaymentNotFound() {
        Long orderId = 1L;
        Long paymentId = 100L;
        PaymentResultEvent event = new PaymentResultEvent("result-2", orderId, paymentId, PaymentStatus.SUCCEEDED, null);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalStateException.class, () -> listener.onPaymentRequested(event)
        );

        verify(paymentRepository, never()).save(any());
        verify(notificationEventProducer, never()).sendPaymentNotificationRequested(any());
    }

    @Test
    void testOnPaymentRequestedWithException() {
        Long orderId = 1L;
        Long paymentId = 100L;
        PaymentResultEvent event = new PaymentResultEvent("result-3", orderId, paymentId, PaymentStatus.FAILED, "Error");

        when(paymentRepository.findById(paymentId)).thenThrow(new RuntimeException("Database error"));

        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, () -> listener.onPaymentRequested(event)
        );

        verify(paymentRepository, never()).save(any());
        verify(notificationEventProducer, never()).sendPaymentNotificationRequested(any());
    }

    @Test
    void testDuplicatePaymentResultIsIgnored() {
        PaymentResultEvent event = new PaymentResultEvent(
                "result-duplicate", 1L, 100L, PaymentStatus.SUCCEEDED, null
        );
        when(inboxEventRepository.existsById(event.eventId())).thenReturn(true);

        listener.onPaymentRequested(event);

        verify(inboxEventRepository, never()).saveAndFlush(any());
        verifyNoInteractions(paymentRepository, notificationEventProducer);
    }
}
