package com.yashmerino.ecommerce.kafka;

import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentEventListener.
 */
@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

    @Mock
    private PaymentService paymentService;

    private PaymentEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new PaymentEventListener(paymentService);
    }

    @Test
    void testOnPaymentRequestedCallsPaymentService() {
        PaymentRequestedEvent event = new PaymentRequestedEvent(
                100L,
                1L,
                BigDecimal.valueOf(99.99),
                "tok_visa"
        );

        listener.onPaymentRequested(event);

        verify(paymentService, times(1)).processPayment(event);
    }
}
