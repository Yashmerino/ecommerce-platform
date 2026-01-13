package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PaymentSuccessTemplate.
 */
class PaymentSuccessTemplateTest {

    private PaymentSuccessTemplate template;

    @BeforeEach
    void setUp() {
        template = new PaymentSuccessTemplate();
    }

    @Test
    void testGetTypeReturnsCorrectNotificationType() {
        NotificationType type = template.getType();

        assertEquals(NotificationType.PAYMENT_SUCCESS, type);
    }

    @Test
    void testBuildCreatesCorrectNotificationContent() {
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 150.50);
        data.put("orderId", 42);
        data.put("paymentId", 789);

        NotificationContent content = template.build(data);

        assertNotNull(content);
        assertEquals("Payment with ID 789 successful", content.subject());
        assertTrue(content.body().contains("150.5"));
        assertTrue(content.body().contains("42"));
        assertTrue(content.body().contains("€"));
    }

    @Test
    void testBuildWithDifferentValues() {
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 99.99);
        data.put("orderId", 1);
        data.put("paymentId", 123);

        NotificationContent content = template.build(data);

        assertNotNull(content);
        assertEquals("Payment with ID 123 successful", content.subject());
        assertTrue(content.body().contains("99.99"));
        assertTrue(content.body().contains("order with ID 1"));
    }

    @Test
    void testBuildWithZeroAmount() {
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 0.0);
        data.put("orderId", 999);
        data.put("paymentId", 555);

        NotificationContent content = template.build(data);

        assertNotNull(content);
        assertTrue(content.body().contains("0.0"));
    }

    @Test
    void testBuildSubjectContainsPaymentId() {
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 100.0);
        data.put("orderId", 10);
        data.put("paymentId", 12345);

        NotificationContent content = template.build(data);

        assertTrue(content.subject().contains("12345"));
        assertTrue(content.subject().contains("Payment"));
        assertTrue(content.subject().contains("successful"));
    }
}
