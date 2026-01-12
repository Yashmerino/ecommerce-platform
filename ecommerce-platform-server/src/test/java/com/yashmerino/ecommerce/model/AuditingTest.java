package com.yashmerino.ecommerce.model;

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

import com.yashmerino.ecommerce.config.JpaAuditingConfig;
import com.yashmerino.ecommerce.repositories.CategoryRepository;
import com.yashmerino.ecommerce.repositories.ProductRepository;
import com.yashmerino.ecommerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JPA Auditing functionality on entity models.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
@Transactional
public class AuditingTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "testuser")
    public void testCategoryAuditFields() {
        Category category = new Category();
        category.setName("Test Category");

        Category savedCategory = categoryRepository.save(category);
        entityManager.flush();

        assertNotNull(savedCategory.getId());
        assertNotNull(savedCategory.getCreatedAt(), "createdAt should be populated");
        assertNotNull(savedCategory.getUpdatedAt(), "updatedAt should be populated");
        assertNotNull(savedCategory.getCreatedBy(), "createdBy should be populated");
        assertNotNull(savedCategory.getUpdatedBy(), "updatedBy should be populated");
        
        assertEquals("testuser", savedCategory.getCreatedBy());
        assertEquals("testuser", savedCategory.getUpdatedBy());
        assertTrue(savedCategory.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @WithMockUser(username = "seller1")
    public void testProductAuditFields() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);

        Product savedProduct = productRepository.save(product);
        entityManager.flush();

        assertNotNull(savedProduct.getId());
        assertNotNull(savedProduct.getCreatedAt(), "createdAt should be populated");
        assertNotNull(savedProduct.getUpdatedAt(), "updatedAt should be populated");
        assertNotNull(savedProduct.getCreatedBy(), "createdBy should be populated");
        assertNotNull(savedProduct.getUpdatedBy(), "updatedBy should be populated");
        
        assertEquals("seller1", savedProduct.getCreatedBy());
        assertEquals("seller1", savedProduct.getUpdatedBy());
    }

    @Test
    @WithMockUser(username = "user1")
    public void testUserAuditFields() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@test.com");
        user.setPassword("encrypted_password");

        User savedUser = userRepository.save(user);
        entityManager.flush();

        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt(), "createdAt should be populated");
        assertNotNull(savedUser.getUpdatedAt(), "updatedAt should be populated");
        assertNotNull(savedUser.getCreatedBy(), "createdBy should be populated");
        assertNotNull(savedUser.getUpdatedBy(), "updatedBy should be populated");
        
        assertEquals("user1", savedUser.getCreatedBy());
        assertEquals("user1", savedUser.getUpdatedBy());
    }

    @Test
    @WithMockUser(username = "admin")
    public void testAuditFieldsUpdateTime() throws InterruptedException {
        Category category = categoryRepository.save(new Category("Initial Category"));
        entityManager.flush();
        
        LocalDateTime initialCreatedAt = category.getCreatedAt();
        LocalDateTime initialUpdatedAt = category.getUpdatedAt();

        Thread.sleep(100);

        category.setName("Updated Category");
        Category updatedCategory = categoryRepository.save(category);
        entityManager.flush();

        assertEquals(initialCreatedAt, updatedCategory.getCreatedAt(),
                "createdAt should not change on update");
        assertTrue(updatedCategory.getUpdatedAt().isAfter(initialUpdatedAt), 
                "updatedAt should be updated");
        assertEquals("admin", updatedCategory.getUpdatedBy());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCartAuditFields() {
        User user = new User();
        user.setUsername("test");

        user = userRepository.save(user);
        Cart cart = new Cart();
        cart.setUser(user);

        Cart savedCart = entityManager.persistAndFlush(cart);

        assertNotNull(savedCart.getId());
        assertNotNull(savedCart.getCreatedAt(), "createdAt should be populated");
        assertNotNull(savedCart.getUpdatedAt(), "updatedAt should be populated");
    }
}
