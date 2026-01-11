package com.yashmerino.ecommerce.services;

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

import com.yashmerino.ecommerce.model.Cart;
import com.yashmerino.ecommerce.repositories.CartRepository;
import com.yashmerino.ecommerce.services.interfaces.CartService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation for cart service.
 */
@Service
public class CartServiceImpl implements CartService {

    /**
     * Cart repository.
     */
    private final CartRepository cartRepository;

    /**
     * Constructor to inject dependencies.
     *
     * @param cartRepository is the cart repository.
     */
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Returns the cart.
     *
     * @param id is the cart's id.
     * @return <code>Optional of Cart</code>.
     */
    @Override
    public Optional<Cart> getCart(Long id) {
        return cartRepository.findById(id);
    }

    /**
     * Saves a cart.
     *
     * @param cart is the cart to save.
     */
    @Override
    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
