package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentResultEvent;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentResultProducer.
 */
@ExtendWith(MockitoExtension.class)
class PaymentResultProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private PaymentResultProducer producer;

    @BeforeEach
    void setUp() {
        producer = new PaymentResultProducer(kafkaTemplate);
    }

    @Test
    void testSendSucceeded() {
        Long orderId = 1L;
        Long paymentId = 100L;

        producer.sendSucceeded(orderId, paymentId);

        ArgumentCaptor<PaymentResultEvent> eventCaptor = ArgumentCaptor.forClass(PaymentResultEvent.class);
        verify(kafkaTemplate, times(1)).send(eq("payment.result"), eventCaptor.capture());

        PaymentResultEvent capturedEvent = eventCaptor.getValue();
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
        verify(kafkaTemplate, times(1)).send(eq("payment.result"), eventCaptor.capture());

        PaymentResultEvent capturedEvent = eventCaptor.getValue();
        assertEquals(orderId, capturedEvent.orderId());
        assertEquals(paymentId, capturedEvent.paymentId());
        assertEquals(PaymentStatus.FAILED, capturedEvent.status());
        assertEquals(errorMessage, capturedEvent.errorMessage());
    }
}
