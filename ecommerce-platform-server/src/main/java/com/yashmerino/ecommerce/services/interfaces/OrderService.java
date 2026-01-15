package com.yashmerino.ecommerce.services.interfaces;

import com.yashmerino.ecommerce.model.dto.OrderDTO;
import com.yashmerino.ecommerce.model.dto.OrderWithPaymentDTO;
import com.yashmerino.ecommerce.model.dto.PaginatedDTO;

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

    /**
     * Gets all orders for the current user with their payment information.
     *
     * @param page page number
     * @param size page size
     * @return paginated orders with payments.
     */
    PaginatedDTO<OrderWithPaymentDTO> getUserOrders(int page, int size);
}
