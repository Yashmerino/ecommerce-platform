package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.outbox.OutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentEventProducer.
 */
@ExtendWith(MockitoExtension.class)
class PaymentEventProducerTest {

    @Mock
    private OutboxService outboxService;

    private PaymentEventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new PaymentEventProducer(outboxService);
    }

    @Test
    void testSendPaymentRequested() {
        PaymentRequestedEvent event = new PaymentRequestedEvent(
            "event-1",
                100L,
                1L,
                BigDecimal.valueOf(99.99),
                "tok_visa",
                "test-payment-key"
        );

        producer.sendPaymentRequested(event);

        verify(outboxService).enqueue("event-1", "payment.requested", "1", event);
        assertEquals("event-1", event.eventId());
    }
}
