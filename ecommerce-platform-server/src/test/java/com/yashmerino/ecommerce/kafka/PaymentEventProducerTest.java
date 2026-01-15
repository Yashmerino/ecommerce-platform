package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentEventProducer.
 */
@ExtendWith(MockitoExtension.class)
class PaymentEventProducerTest {

    @Mock
    private KafkaTemplate<String, PaymentRequestedEvent> kafkaTemplate;

    private PaymentEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new PaymentEventProducer(kafkaTemplate);
    }

    @Test
    void testSendPaymentRequested() {
        PaymentRequestedEvent event = new PaymentRequestedEvent(
                100L,
                1L,
                BigDecimal.valueOf(99.99),
                "tok_visa"
        );

        producer.sendPaymentRequested(event);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PaymentRequestedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentRequestedEvent.class);
        
        verify(kafkaTemplate).send(eq("payment.requested"), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("1", keyCaptor.getValue());
        assertEquals(event, eventCaptor.getValue());
    }
}
