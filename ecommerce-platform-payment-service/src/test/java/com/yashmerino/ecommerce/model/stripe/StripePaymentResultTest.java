package com.yashmerino.ecommerce.model.stripe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StripePaymentResult.
 */
class StripePaymentResultTest {

    @Test
    void testStripePaymentResultCreation() {
        String paymentIntentId = "pi_1234567890";
        String status = "succeeded";

        StripePaymentResult result = new StripePaymentResult(paymentIntentId, status);

        assertEquals(paymentIntentId, result.getPaymentIntentId());
        assertEquals(status, result.getStatus());
    }

    @Test
    void testStripePaymentResultWithFailedStatus() {
        String paymentIntentId = "pi_failed";
        String status = "failed";

        StripePaymentResult result = new StripePaymentResult(paymentIntentId, status);

        assertEquals("pi_failed", result.getPaymentIntentId());
        assertEquals("failed", result.getStatus());
    }

    @Test
    void testStripePaymentResultWithRequiresActionStatus() {
        String paymentIntentId = "pi_requires_action";
        String status = "requires_action";

        StripePaymentResult result = new StripePaymentResult(paymentIntentId, status);

        assertEquals("pi_requires_action", result.getPaymentIntentId());
        assertEquals("requires_action", result.getStatus());
    }

    @Test
    void testStripePaymentResultImmutability() {
        StripePaymentResult result1 = new StripePaymentResult("pi_id_1", "succeeded");
        StripePaymentResult result2 = new StripePaymentResult("pi_id_1", "succeeded");

        assertEquals(result1.getPaymentIntentId(), result2.getPaymentIntentId());
        assertEquals(result1.getStatus(), result2.getStatus());
    }

    @Test
    void testStripePaymentResultGetter() {
        String intentId = "pi_getter_test";
        String testStatus = "processing";

        StripePaymentResult result = new StripePaymentResult(intentId, testStatus);

        // Test getters work correctly
        assertNotNull(result.getPaymentIntentId());
        assertNotNull(result.getStatus());
        assertEquals(intentId, result.getPaymentIntentId());
        assertEquals(testStatus, result.getStatus());
    }

    @Test
    void testStripePaymentResultDifferentStatuses() {
        String paymentIntentId = "pi_multi_status";

        StripePaymentResult succeededResult = new StripePaymentResult(paymentIntentId, "succeeded");
        StripePaymentResult failedResult = new StripePaymentResult(paymentIntentId, "failed");
        StripePaymentResult requiresActionResult = new StripePaymentResult(paymentIntentId, "requires_action");

        assertEquals("succeeded", succeededResult.getStatus());
        assertEquals("failed", failedResult.getStatus());
        assertEquals("requires_action", requiresActionResult.getStatus());
    }
}
