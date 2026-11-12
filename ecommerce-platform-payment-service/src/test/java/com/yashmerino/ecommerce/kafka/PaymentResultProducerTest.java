package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.outbox.OutboxService;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentResultProducer.
 */
@ExtendWith(MockitoExtension.class)
class PaymentResultProducerTest {

    @Mock
    private OutboxService outboxService;

    private PaymentResultProducer producer;

    @BeforeEach
    void setUp() {
        producer = new PaymentResultProducer(outboxService);
    }

    @Test
    void testSendSucceeded() {
        Long orderId = 1L;
        Long paymentId = 100L;

        producer.sendSucceeded(orderId, paymentId);

        ArgumentCaptor<PaymentResultEvent> eventCaptor = ArgumentCaptor.forClass(PaymentResultEvent.class);
        verify(outboxService).enqueue(anyString(), eq("payment.result"), eq(paymentId.toString()), eventCaptor.capture());

        PaymentResultEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent.eventId());
        assertEquals(orderId, capturedEvent.orderId());
        assertEquals(paymentId, capturedEvent.paymentId());
        assertEquals(PaymentStatus.SUCCEEDED, capturedEvent.status());
        assertNull(capturedEvent.errorMessage());
    }

    @Test
    void testSendFailed() {
        Long orderId = 1L;
        Long paymentId = 100L;
        String errorMessage = "Payment declined";

        producer.sendFailed(orderId, paymentId, errorMessage);

        ArgumentCaptor<PaymentResultEvent> eventCaptor = ArgumentCaptor.forClass(PaymentResultEvent.class);
        verify(outboxService).enqueue(anyString(), eq("payment.result"), eq(paymentId.toString()), eventCaptor.capture());

        PaymentResultEvent capturedEvent = eventCaptor.getValue();
        assertEquals(orderId, capturedEvent.orderId());
        assertEquals(paymentId, capturedEvent.paymentId());
        assertEquals(PaymentStatus.FAILED, capturedEvent.status());
        assertEquals(errorMessage, capturedEvent.errorMessage());
    }
}
