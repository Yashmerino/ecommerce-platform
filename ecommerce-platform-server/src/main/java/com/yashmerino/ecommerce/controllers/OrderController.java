package com.yashmerino.ecommerce.controllers;

import com.yashmerino.ecommerce.model.dto.OrderDTO;
import com.yashmerino.ecommerce.model.dto.OrderWithPaymentDTO;
import com.yashmerino.ecommerce.model.dto.PaginatedDTO;
import com.yashmerino.ecommerce.model.dto.SuccessWithIdDTO;
import com.yashmerino.ecommerce.services.interfaces.OrderService;
import com.yashmerino.ecommerce.swagger.SwaggerConfig;
import com.yashmerino.ecommerce.swagger.SwaggerHttpStatus;
import com.yashmerino.ecommerce.swagger.SwaggerMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Order controller.
 */
@Tag(name = "6. Orders Controller", description = "These endpoints are used to perform actions on orders.")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/order")
@Validated
@AllArgsConstructor
public class OrderController {

    /**
     * Order service.
     */
    private final OrderService orderService;

    /**
     * Places a new order.
     *
     * @param orderDTO is the order DTO.
     *
     * @return SuccessDTO.
     */
    @Operation(summary = "Places a new order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.PRODUCT_SUCCESSFULLY_ADDED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.BAD_REQUEST, description = SwaggerMessages.BAD_REQUEST,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @PostMapping
    public ResponseEntity<SuccessWithIdDTO> placeOrder(@Validated @RequestBody OrderDTO orderDTO) {
        Long id = this.orderService.placeOrder(orderDTO);

        SuccessWithIdDTO successWithIdDTO = new SuccessWithIdDTO();
        successWithIdDTO.setStatus(200);
        successWithIdDTO.setMessage("order_placed_successfully");
        successWithIdDTO.setId(id);

        return new ResponseEntity<>(successWithIdDTO, HttpStatus.OK);
    }

    /**
     * Gets all orders for the current user with their payment information.
     *
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @return Paginated DTO of orders with payments.
     */
    @Operation(summary = "Gets all orders for the current user with their payment information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = SwaggerHttpStatus.OK, description = SwaggerMessages.ORDERS_RETURNED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.FORBIDDEN, description = SwaggerMessages.FORBIDDEN,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.UNAUTHORIZED, description = SwaggerMessages.UNAUTHORIZED,
                    content = @Content),
            @ApiResponse(responseCode = SwaggerHttpStatus.INTERNAL_SERVER_ERROR, description = SwaggerMessages.INTERNAL_SERVER_ERROR,
                    content = @Content)})
    @GetMapping("/my-orders")
    public ResponseEntity<PaginatedDTO<OrderWithPaymentDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedDTO<OrderWithPaymentDTO> orders = this.orderService.getUserOrders(page, size);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
