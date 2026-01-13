package com.yashmerino.ecommerce.model.stripe;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Stripe payment result.
 */
@Getter
@AllArgsConstructor
public class StripePaymentResult {

    /**
     * Stripe payment intent ID.
     */
    private final String paymentIntentId;

    /**
     * Stripe payment status.
     */
    private final String status; // succeeded, requires_action, failed
}
