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
import com.yashmerino.ecommerce.model.CartItem;
import com.yashmerino.ecommerce.model.Product;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.model.dto.ProductDTO;
import com.yashmerino.ecommerce.repositories.CartItemRepository;
import com.yashmerino.ecommerce.repositories.ProductRepository;
import com.yashmerino.ecommerce.services.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private Product testProduct;
    private User testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("seller");
        testUser.setEmail("seller@example.com");

        testCart = new Cart();
        testCart.setId(1L);
        testUser.setCart(testCart);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setUser(testUser);
    }

    @Test
    void testGetProductProductExistsReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProduct(1L);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductProductDoesNotExistThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> productService.getProduct(999L));
        assertTrue(exception.getMessage().contains("couldn't be found"));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllProductsReturnsPageOfProducts() {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testSearchWithQueryReturnsMatchingProducts() {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findByNameContaining("Test", pageable)).thenReturn(productPage);

        Page<Product> result = productService.search("Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findByNameContaining("Test", pageable);
    }

    @Test
    void testSaveSavesProduct() {
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        productService.save(testProduct);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testDeleteProductExistsDeletesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        productService.delete(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void testDeleteProductDoesNotExistThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.delete(999L));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void testGetSellerProductsReturnsSellerProducts() {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getByUsername("seller")).thenReturn(testUser);
        when(productRepository.getProductsBySellerId(1L, pageable)).thenReturn(productPage);

        Page<Product> result = productService.getSellerProducts("seller", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(userService, times(1)).getByUsername("seller");
        verify(productRepository, times(1)).getProductsBySellerId(1L, pageable);
    }

    @Test
    void testAddProductToCartAddsCartItem() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(userDetails.getUsername()).thenReturn("buyer");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        User buyer = new User();
        buyer.setId(2L);
        buyer.setUsername("buyer");
        buyer.setCart(testCart);
        when(userService.getByUsername("buyer")).thenReturn(buyer);

        productService.addProductToCart(1L, 2);

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(cartItemCaptor.capture());
        
        CartItem savedCartItem = cartItemCaptor.getValue();
        assertEquals(2, savedCartItem.getQuantity());
        assertEquals("Test Product", savedCartItem.getName());
        assertEquals(99.99, savedCartItem.getPrice());
        assertEquals(testProduct, savedCartItem.getProduct());
        assertEquals(testCart, savedCartItem.getCart());
    }

    @Test
    void testAddProductCreatesNewProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Product");
        productDTO.setPrice(49.99);
        
        when(userDetails.getUsername()).thenReturn("seller");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userService.getByUsername("seller")).thenReturn(testUser);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(2L);
            return product;
        });

        Long productId = productService.addProduct(productDTO);

        assertNotNull(productId);
        assertEquals(2L, productId);
        
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        
        Product capturedProduct = productCaptor.getValue();
        assertEquals(testUser, capturedProduct.getUser());
    }
}
