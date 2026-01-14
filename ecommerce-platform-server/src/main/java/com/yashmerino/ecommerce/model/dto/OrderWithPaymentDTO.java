package com.yashmerino.ecommerce.model.dto;

import com.yashmerino.ecommerce.utils.OrderStatus;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Order with Payment information.
 */
@Data
public class OrderWithPaymentDTO {
    /**
     * Order ID.
     */
    private Long orderId;

    /**
     * Total amount of the order.
     */
    private BigDecimal totalAmount;

    /**
     * Order status.
     */
    private OrderStatus orderStatus;

    /**
     * Order creation date.
     */
    private Instant createdAt;

    /**
     * Payment ID.
     */
    private Long paymentId;

    /**
     * Payment amount.
     */
    private BigDecimal paymentAmount;

    /**
     * Payment status.
     */
    private PaymentStatus paymentStatus;

    /**
     * Payment creation date.
     */
    private Instant paymentCreatedAt;
}
