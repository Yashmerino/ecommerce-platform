package com.yashmerino.ecommerce.model.dto;
/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + MIT License
 +
 + Copyright (c) 2023 Artiom Bozieac
 +
 + Permission is hereby granted, free of charge, to any person obtaining a copy
 + of this software and associated documentation files (the "Software"), to deal
 + in the Software without restriction, including without limitation the rights
 + to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 + copies of the Software, and to permit persons to whom the Software is
 + furnished to do so, subject to the following conditions:
 +
 + The above copyright notice and this permission notice shall be included in all
 + copies or substantial portions of the Software.
 +
 + THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 + IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 + FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 + AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 + LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 + OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 + SOFTWARE.
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Cart item DTO.
 */
@Getter
@Setter
public class CartItemDTO {

    /**
     * Cart item's ID.
     */
    private Long id;

    /**
     * Product's id.
     */
    private Long productId;

    /**
     * Cart Item's name.
     */
    @NotNull(message = "name_is_required")
    @NotBlank(message = "name_is_required")
    // NOSONAR: The wrapper is required. Different error messages.
    @Size(min = 4, max = 255, message = "name_invalid_length")
    private String name;

    /**
     * Cart Item's price.
     */
    @NotNull(message = "price_is_required")
    @DecimalMin(value = "0.01", message = "price_value_error")
    private Double price;

    /**
     * Cart's id.
     */
    private Long cartId;

    /**
     * Quantity.
     */
    @NotNull(message = "quantity_is_required")
    @Min(value = 1L, message = "quantity_value_error")
    private Integer quantity;
}
