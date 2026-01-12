package com.yashmerino.ecommerce.model.events;

import java.math.BigDecimal;

public record PaymentRequestedEvent(
        Long paymentId,
        Long orderId,
        BigDecimal amount
) {}
