package com.yashmerino.ecommerce.model;

import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Payment entity.
 */
class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    @Test
    void testPaymentCreation() {
        payment.setOrderId(1L);
        payment.setStripePaymentId("pi_123456");
        payment.setAmount(new BigDecimal("99.99"));
        payment.setStatus(PaymentStatus.SUCCEEDED);

        assertEquals(1L, payment.getOrderId());
        assertEquals("pi_123456", payment.getStripePaymentId());
        assertEquals(new BigDecimal("99.99"), payment.getAmount());
        assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());
    }

    @Test
    void testPaymentStatusChange() {
        payment.setStatus(PaymentStatus.SUCCEEDED);
        assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());

        payment.setStatus(PaymentStatus.FAILED);
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void testPaymentAmountCalculation() {
        BigDecimal amount1 = new BigDecimal("50.00");
        BigDecimal amount2 = new BigDecimal("49.99");

        payment.setAmount(amount1);
        assertEquals(0, payment.getAmount().compareTo(amount1));

        payment.setAmount(amount2);
        assertEquals(0, payment.getAmount().compareTo(amount2));
    }

    @Test
    void testPaymentConstructorWithAllArguments() {
        Long orderId = 2L;
        String stripeId = "pi_789012";
        BigDecimal amount = new BigDecimal("150.50");
        PaymentStatus status = PaymentStatus.SUCCEEDED;

        Payment newPayment = new Payment(orderId, stripeId, amount, status);

        assertEquals(orderId, newPayment.getOrderId());
        assertEquals(stripeId, newPayment.getStripePaymentId());
        assertEquals(amount, newPayment.getAmount());
        assertEquals(status, newPayment.getStatus());
    }

    @Test
    void testPaymentNullValues() {
        Payment emptyPayment = new Payment();

        assertNull(emptyPayment.getOrderId());
        assertNull(emptyPayment.getStripePaymentId());
        assertNull(emptyPayment.getAmount());
        assertNull(emptyPayment.getStatus());
    }
}
