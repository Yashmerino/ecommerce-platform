package com.yashmerino.ecommerce.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaymentStatus enum.
 */
class PaymentStatusTest {

    @Test
    void testPaymentStatusSucceeded() {
        PaymentStatus status = PaymentStatus.SUCCEEDED;
        assertEquals(PaymentStatus.SUCCEEDED, status);
        assertNotEquals(PaymentStatus.FAILED, status);
    }

    @Test
    void testPaymentStatusFailed() {
        PaymentStatus status = PaymentStatus.FAILED;
        assertEquals(PaymentStatus.FAILED, status);
        assertNotEquals(PaymentStatus.SUCCEEDED, status);
    }

    @Test
    void testPaymentStatusValues() {
        PaymentStatus[] values = PaymentStatus.values();
        assertEquals(2, values.length);
        assertArrayEquals(new PaymentStatus[]{PaymentStatus.SUCCEEDED, PaymentStatus.FAILED}, values);
    }

    @Test
    void testPaymentStatusValueOf() {
        PaymentStatus status = PaymentStatus.valueOf("SUCCEEDED");
        assertEquals(PaymentStatus.SUCCEEDED, status);

        PaymentStatus failedStatus = PaymentStatus.valueOf("FAILED");
        assertEquals(PaymentStatus.FAILED, failedStatus);
    }

    @Test
    void testPaymentStatusEnumComparison() {
        PaymentStatus status1 = PaymentStatus.SUCCEEDED;
        PaymentStatus status2 = PaymentStatus.SUCCEEDED;

        assertTrue(status1 == status2);
        assertEquals(status1, status2);
    }
}
