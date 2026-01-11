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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for product service.
 */
public interface ProductService {

    /**
     * Returns the product.
     *
     * @param id is the product's id.
     * @return <code>Product</code>
     */
    Product getProduct(final Long id);

    /**
     * Returns all the products.
     *
     * @param pageable is the page object.
     *
     * @return <code>Page of Products</code>
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Saves a product.
     *
     * @param product is the product's object.
     */
    void save(final Product product);

    /**
     * Deletes a product.
     *
     * @param id is the product's id.
     */
    void delete(final Long id);

    /**
     * Returns seller's products.
     *
     * @param username is the seller's username.
     * @param pageable is the page details.
     *
     * @return Page of Products.
     */
    Page<Product> getSellerProducts(String username, Pageable pageable);

    /**
     * Add product to the cart.
     *
     * @param id       is the product's id.
     * @param quantity is the quantity.
     */
    void addProductToCart(final Long id, final Integer quantity);

    /**
     * Adds a new product.
     *
     * @param productDTO is the product DTO.
     * @return the product's id.
     */
    Long addProduct(final ProductDTO productDTO);

    /**
     * Updates product photo.
     *
     * @param id    is the product's id.
     * @param photo is the product's photo.
     */
    void updatePhoto(Long id, MultipartFile photo);

    /**
     * Updates product.
     *
     * @param id         is the product's id.
     * @param productDTO is the product DTO.
     */
    void updateProduct(Long id, ProductDTO productDTO);
}
