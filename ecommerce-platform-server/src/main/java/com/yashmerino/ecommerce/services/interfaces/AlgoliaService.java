package com.yashmerino.ecommerce.services.interfaces;
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

import com.yashmerino.ecommerce.model.Product;
import com.yashmerino.ecommerce.model.dto.ProductDTO;

import java.util.List;

/**
 * Interface for Algolia service.
 */
public interface AlgoliaService {
    /**
     * Populates index with passed products.
     *
     * @param products is the list of products to add to the index.
     */
    void populateIndex(List<Product> products);

    /**
     * Adds a product to the index.
     *
     * @param productDTO is the product to add.
     */
    void addProductToIndex(ProductDTO productDTO);

    /**
     * Removes a product from the index.
     *
     * @param productId is the product's id to delete.
     */
    void deleteProductFromIndex(Long productId);

    /**
     * Updates a product.
     *
     * @param productDTO is the product's DTO.
     */
    void updateProduct(ProductDTO productDTO);
}
