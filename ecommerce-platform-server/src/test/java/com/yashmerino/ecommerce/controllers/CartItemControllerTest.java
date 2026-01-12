package com.yashmerino.ecommerce.controllers;
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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Tests for {@link CartItemController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class CartItemControllerTest {

    /**
     * Mock mvc to perform requests.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Test get cart item.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCartItemTest() throws Exception {
        mvc.perform(get("/api/cartItem/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Phone"))
                .andExpect(jsonPath("$.price").value(5.0))
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.quantity").value(1));
    }

    /**
     * Test get cart item with wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void getCartItemWrongUserTest() throws Exception {
        mvc.perform(get("/api/cartItem/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("access_denied"));
    }

    /**
     * Test delete cart item.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void deleteCartItemTest() throws Exception {
        mvc.perform(delete("/api/cartItem/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("cartitem_deleted_successfully"));
    }

    /**
     * Test delete cart item as wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void deleteCartItemWrongUserTest() throws Exception {
        mvc.perform(delete("/api/cartItem/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("access_denied"));
    }

    /**
     * Test change item's quantity.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void changeQuantityTest() throws Exception {
        mvc.perform(post("/api/cartItem/1/quantity?quantity=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("quantity_changed_successfully"));

        mvc.perform(get("/api/cartItem/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }

    /**
     * Test change item's quantity as wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void changeQuantityWrongUserTest() throws Exception {
        mvc.perform(post("/api/cartItem/1/quantity?quantity=5"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("access_denied"));
    }

    /**
     * Test get cart items.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCartItemsTest() throws Exception {
        mvc.perform(get("/api/cartItem?username=user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].productId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Phone"));
    }

    /**
     * Test get cart items with wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void getCartItemsWrongUserTest() throws Exception {
        mvc.perform(get("/api/cartItem?username=user"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("access_denied"));
    }

    /**
     * Test change item's quantity to negative value.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void changeQuantityToNegativeTest() throws Exception {
        mvc.perform(post("/api/cartItem/1/quantity?quantity=-4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("changeQuantity.quantity"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("Quantity should be greater or equal to 1."));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void cartItemsPaginationTest() throws Exception {
        // Page 0, size 1
        MvcResult page0 = mvc.perform(get("/api/cartItem?username=user&page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn();
        String content0 = page0.getResponse().getContentAsString();
        assertTrue(content0.contains("Phone"));

        // Page 1, size 1 → should be empty since there's only 1 item
        MvcResult page1 = mvc.perform(get("/api/cartItem?username=user&page=1&size=1"))
                .andExpect(status().isOk())
                .andReturn();
        String content1 = page1.getResponse().getContentAsString();
        assertTrue(content1.equals("{\"data\":[],\"currentPage\":1,\"totalPages\":1,\"totalItems\":1,\"pageSize\":1,\"totalPrice\":5.0,\"hasNext\":false,\"hasPrevious\":true}"));
    }
}
