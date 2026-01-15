package com.yashmerino.ecommerce.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe configuration.
 */
@Configuration
public class StripeConfig {

    /**
     * Stripe API key.
     */
    @Value("${stripe.api.key}")
    private static String apiKey;

    @PostConstruct
    public static void init() {
        Stripe.apiKey = apiKey;
    }
}
