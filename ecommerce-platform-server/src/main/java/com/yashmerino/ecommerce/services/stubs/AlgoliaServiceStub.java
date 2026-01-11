package com.yashmerino.ecommerce.services.stubs;
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

import com.yashmerino.ecommerce.conditions.AlgoliaServiceStubCondition;
import com.yashmerino.ecommerce.model.Product;
import com.yashmerino.ecommerce.model.dto.ProductDTO;
import com.yashmerino.ecommerce.services.interfaces.AlgoliaService;
import com.yashmerino.ecommerce.utils.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Algolia service stub if no Algolia search is used.
 */
@Service
@Conditional(AlgoliaServiceStubCondition.class)
@Slf4j
public class AlgoliaServiceStub implements AlgoliaService {
    /**
     * Constructor.
     *
     * @param applicationProperties is the Application's properties.
     */
    public AlgoliaServiceStub(ApplicationProperties applicationProperties) {
        // Stub
    }

    /**
     * Populates index with passed products.
     *
     * @param products is the list of products to add to the index.
     */
    public void populateIndex(List<Product> products) {
        // Stub
    }

    /**
     * Adds a product to the index.
     *
     * @param productDTO is the product to add.
     */
    public void addProductToIndex(ProductDTO productDTO) {
        // Stub
    }

    /**
     * Removes a product from the index.
     *
     * @param productId is the product's id to delete.
     */
    public void deleteProductFromIndex(Long productId) {
        // Stub
    }

    /**
     * Updates a product.
     *
     * @param productDTO is the product's DTO.
     */
    public void updateProduct(ProductDTO productDTO) {
        // Stub
    }
}
