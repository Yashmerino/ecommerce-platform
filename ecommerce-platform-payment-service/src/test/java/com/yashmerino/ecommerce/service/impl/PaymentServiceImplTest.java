package com.yashmerino.ecommerce.service.impl;

import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.StripeException;
import com.yashmerino.ecommerce.kafka.PaymentResultProducer;
import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.stripe.StripePaymentResult;
import com.yashmerino.ecommerce.repository.PaymentRepository;
import com.yashmerino.ecommerce.service.StripePaymentService;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private StripePaymentService stripePaymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentResultProducer resultProducer;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(stripePaymentService, paymentRepository, resultProducer);
    }

    @Test
    void testProcessPaymentSuccess() throws StripeException {
        Long paymentId = 100L;
        Long orderId = 1L;
        BigDecimal amount = new BigDecimal("99.99");
        String stripeToken = "pm_1234567890";

        PaymentRequestedEvent event = new PaymentRequestedEvent(paymentId, orderId, amount, stripeToken);
        StripePaymentResult stripeResult = new StripePaymentResult("pi_success_123", "succeeded");

        when(stripePaymentService.charge(amount, "EUR", stripeToken))
                .thenReturn(stripeResult);

        paymentService.processPayment(event);

        verify(stripePaymentService).charge(amount, "EUR", stripeToken);
        verify(paymentRepository).save(any(Payment.class));
        verify(resultProducer).sendSucceeded(orderId, paymentId);
        verify(resultProducer, never()).sendFailed(anyLong(), anyLong(), anyString());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(orderId, savedPayment.getOrderId());
        assertEquals("pi_success_123", savedPayment.getStripePaymentId());
        assertEquals(amount, savedPayment.getAmount());
        assertEquals(PaymentStatus.SUCCEEDED, savedPayment.getStatus());
    }

    @Test
    void testProcessPaymentFailure() throws StripeException {
        Long paymentId = 101L;
        Long orderId = 2L;
        BigDecimal amount = new BigDecimal("50.00");
        String stripeToken = "pm_invalid";
        String errorMessage = "Card declined";

        PaymentRequestedEvent event = new PaymentRequestedEvent(paymentId, orderId, amount, stripeToken);

        StripeException stripeException = new ApiConnectionException(errorMessage);
        when(stripePaymentService.charge(amount, "EUR", stripeToken))
                .thenThrow(stripeException);

        paymentService.processPayment(event);

        verify(stripePaymentService).charge(amount, "EUR", stripeToken);
        verify(paymentRepository).save(any(Payment.class));
        verify(resultProducer).sendFailed(orderId, paymentId, errorMessage);
        verify(resultProducer, never()).sendSucceeded(anyLong(), anyLong());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(orderId, savedPayment.getOrderId());
        assertNull(savedPayment.getStripePaymentId());
        assertEquals(amount, savedPayment.getAmount());
        assertEquals(PaymentStatus.FAILED, savedPayment.getStatus());
    }

    @Test
    void testProcessPaymentWithDifferentAmounts() throws StripeException {
        // Test with small amount
        BigDecimal smallAmount = new BigDecimal("10.00");
        PaymentRequestedEvent smallEvent = new PaymentRequestedEvent(102L, 3L, smallAmount, "pm_small");
        StripePaymentResult smallResult = new StripePaymentResult("pi_small", "succeeded");

        when(stripePaymentService.charge(smallAmount, "EUR", "pm_small"))
                .thenReturn(smallResult);

        paymentService.processPayment(smallEvent);

        verify(paymentRepository).save(any(Payment.class));
        verify(resultProducer).sendSucceeded(3L, 102L);

        // Test with large amount
        BigDecimal largeAmount = new BigDecimal("9999.99");
        PaymentRequestedEvent largeEvent = new PaymentRequestedEvent(103L, 4L, largeAmount, "pm_large");
        StripePaymentResult largeResult = new StripePaymentResult("pi_large", "succeeded");

        when(stripePaymentService.charge(largeAmount, "EUR", "pm_large"))
                .thenReturn(largeResult);

        paymentService.processPayment(largeEvent);

        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    void testProcessPaymentCallsRepositoryWithCorrectData() throws StripeException {
        Long paymentId = 104L;
        Long orderId = 5L;
        BigDecimal amount = new BigDecimal("149.99");
        String stripeToken = "pm_test_token";

        PaymentRequestedEvent event = new PaymentRequestedEvent(paymentId, orderId, amount, stripeToken);
        StripePaymentResult result = new StripePaymentResult("pi_test_intent", "succeeded");

        when(stripePaymentService.charge(amount, "EUR", stripeToken))
                .thenReturn(result);

        paymentService.processPayment(event);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment captured = paymentCaptor.getValue();
        assertNotNull(captured);
        assertEquals(5L, captured.getOrderId());
        assertEquals("pi_test_intent", captured.getStripePaymentId());
        assertEquals(0, captured.getAmount().compareTo(amount));
    }

    @Test
    void testProcessPaymentRuntimeException() throws StripeException {
        Long paymentId = 105L;
        Long orderId = 6L;
        BigDecimal amount = new BigDecimal("75.00");
        String stripeToken = "pm_runtime_error";

        PaymentRequestedEvent event = new PaymentRequestedEvent(paymentId, orderId, amount, stripeToken);

        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        when(stripePaymentService.charge(amount, "EUR", stripeToken))
                .thenThrow(runtimeException);

        paymentService.processPayment(event);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment failedPayment = paymentCaptor.getValue();
        assertEquals(PaymentStatus.FAILED, failedPayment.getStatus());
        assertEquals(orderId, failedPayment.getOrderId());
    }
}
