package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.model.NotificationContent;
import com.yashmerino.ecommerce.utils.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaymentFailedTemplate.
 */
class PaymentFailedTemplateTest {

    private PaymentFailedTemplate template;

    @BeforeEach
    void setUp() {
        template = new PaymentFailedTemplate();
    }

    @Test
    void testGetTypeReturnsPaymentFailed() {
        assertEquals(NotificationType.PAYMENT_FAILED, template.getType());
    }

    @Test
    void testBuildCreatesCorrectContent() {
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 99.99);
        data.put("orderId", 123);
        data.put("paymentId", 456);

        NotificationContent content = template.build(data);

        assertNotNull(content);
        assertEquals("Payment with ID 456 failed", content.subject());
        assertTrue(content.body().contains("99.99"));
        assertTrue(content.body().contains("123"));
    }

    @Test
    void testBuildWithDifferentValues() {
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 250.50);
        data.put("orderId", 789);
        data.put("paymentId", 101);

        NotificationContent content = template.build(data);

        assertNotNull(content);
        assertTrue(content.subject().contains("101"));
        assertTrue(content.body().contains("250.5"));
        assertTrue(content.body().contains("789"));
    }
}
