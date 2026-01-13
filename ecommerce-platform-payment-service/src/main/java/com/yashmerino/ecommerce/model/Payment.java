package com.yashmerino.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yashmerino.ecommerce.model.base.BaseEntity;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA Entity for payment.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "payments")
@Table(name = "payments")
public class Payment extends BaseEntity {

    /**
     * Payment's order ID.
     */
    private Long orderId;

    /**
     * Payment's stripe ID.
     */
    private String stripePaymentId;

    /**
     * Payment's amount.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Payment's status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
}
