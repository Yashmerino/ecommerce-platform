package com.yashmerino.ecommerce.utils;
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

import com.yashmerino.ecommerce.model.CartItem;
import com.yashmerino.ecommerce.model.Category;
import com.yashmerino.ecommerce.model.Product;
import com.yashmerino.ecommerce.model.dto.CartItemDTO;
import com.yashmerino.ecommerce.model.dto.CategoryDTO;
import com.yashmerino.ecommerce.model.dto.ProductDTO;

/**
 * Utils class that converts request body to an entity;
 */
public class RequestBodyToEntityConverter {

    /**
     * Private constructor to now allow instantiation.
     */
    private RequestBodyToEntityConverter() {

    }

    /**
     * Converts productDTO to product entity.
     *
     * @param productDTO is the product DTO.
     * @return <code>Product</code>
     */
    public static Product convertToProduct(final ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategories(productDTO.getCategories());

        return product;
    }

    /**
     * Converts a product to ProductDTO.
     *
     * @param product is the product entity.
     * @return <code>ProductDTO</code>
     */
    public static ProductDTO convertToProductDTO(final Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId().toString());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategories(product.getCategories());

        return productDTO;
    }

    /**
     * Converts a cart item entity to cart item DTO.
     *
     * @param cartItem is the cart item entity.
     * @return <code>CartItemDTO</code>
     */
    public static CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setCartId(cartItem.getCart().getId());
        cartItemDTO.setName(cartItem.getName());
        cartItemDTO.setPrice(cartItem.getPrice());
        cartItemDTO.setProductId(cartItem.getProduct().getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());

        return cartItemDTO;
    }

    /**
     * Converts a category entity to category DTO.
     *
     * @param category is the category entity.
     * @return <code>CategoryDTO</code>
     */
    public static CategoryDTO convertToCategoryDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());

        return categoryDTO;
    }
}
