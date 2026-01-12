package com.yashmerino.ecommerce.controllers;

import com.yashmerino.ecommerce.model.dto.PaymentDTO;
import com.yashmerino.ecommerce.model.dto.SuccessDTO;
import com.yashmerino.ecommerce.services.interfaces.PaymentService;
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
 * Payments controller.
 */
@Tag(name = "7. Payments Controller", description = "These endpoints are used to perform actions on payments.")
@SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/payment")
@Validated
@AllArgsConstructor
public class PaymentController {

    /**
     * Payment service.
     */
    private final PaymentService paymentService;

    /**
     * Sends an event to Kafka topic to process the payment for an order.
     *
     * @param orderId is the payment's order ID.
     *
     * @return SuccessDTO.
     */
    @Operation(summary = "Sends an event to Kafka topic to process the payment for an order.")
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
    @PostMapping("/{orderId}")
    public ResponseEntity<SuccessDTO> pay(@PathVariable Long orderId, @Validated @RequestBody PaymentDTO paymentDTO) {
        this.paymentService.pay(orderId, paymentDTO);

        SuccessDTO successDTO = new SuccessDTO();
        successDTO.setStatus(200);
        successDTO.setMessage("payment_sent_successfully");

        return new ResponseEntity<>(successDTO, HttpStatus.OK);
    }
}
