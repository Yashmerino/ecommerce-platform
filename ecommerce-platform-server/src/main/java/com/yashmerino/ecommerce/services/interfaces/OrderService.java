package com.yashmerino.ecommerce.services.interfaces;

import com.yashmerino.ecommerce.model.dto.OrderDTO;

/**
 * Interface for order service.
 */
public interface OrderService {
    /**
     * Places a new order.
     *
     * @param orderDTO is the order DTO.
     *
     * @return the new order's ID.
     */
    Long placeOrder(final OrderDTO orderDTO);
}
