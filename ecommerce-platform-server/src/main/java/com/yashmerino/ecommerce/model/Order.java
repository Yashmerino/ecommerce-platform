package com.yashmerino.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yashmerino.ecommerce.model.base.BaseEntity;
import com.yashmerino.ecommerce.utils.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA Entity for order.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "orders")
@Table(name = "orders")
public class Order extends BaseEntity {
    /**
     * Order's user.
     */
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Total amount of the order to be paid.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Order's status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
}
