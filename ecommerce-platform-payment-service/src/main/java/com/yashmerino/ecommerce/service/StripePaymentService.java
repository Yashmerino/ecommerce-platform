package com.yashmerino.ecommerce.service;

import com.stripe.exception.StripeException;
import com.yashmerino.ecommerce.model.stripe.StripePaymentResult;

import java.math.BigDecimal;

/**
 * Interface for stripe payment service.
 */
public interface StripePaymentService {

    /**
     * Processes stripe payment.
     *
     * @param amount is the payment's amount.
     * @param currency is the payment's currency.
     * @param paymentMethodId is the stripe payment method ID.
     *
     * @return The Stripe payment result.
     *
     * @throws StripeException if payment failed.
     */
    StripePaymentResult charge(BigDecimal amount, String currency, String paymentMethodId) throws StripeException;
}
