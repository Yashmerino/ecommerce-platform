package com.yashmerino.ecommerce.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.net.RequestOptions;
import com.yashmerino.ecommerce.model.stripe.StripePaymentResult;
import com.yashmerino.ecommerce.service.StripePaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Stripe payment service implementation.
 */
@Service
public class StripePaymentServiceImpl implements StripePaymentService {

    /**
     * Processes stripe payment.
     *
     * @param amount is the payment's amount.
     * @param currency is the payment's currency.
     * @param paymentMethodId is the stripe payment method ID.
     *
     * @return the Stripe payment result.
     *
     * @throws StripeException if payment fails.
     */
    public StripePaymentResult charge(
            BigDecimal amount,
            String currency,
            String paymentMethodId,
            String idempotencyKey) throws StripeException {

        PaymentIntentCreateParams params =
            PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency(currency)
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                        )
                        .build()
                )
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
            .setIdempotencyKey(idempotencyKey)
            .build();
        PaymentIntent intent = PaymentIntent.create(params, requestOptions);

        return new StripePaymentResult(intent.getId(), intent.getStatus());
    }
}
