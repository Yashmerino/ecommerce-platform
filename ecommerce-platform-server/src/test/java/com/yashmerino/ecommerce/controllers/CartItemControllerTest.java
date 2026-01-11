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
        MvcResult result = mvc.perform(get("/api/cartItem/1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":1,\"productId\":1,\"name\":\"Phone\",\"price\":5.0,\"cartId\":1,\"quantity\":1}"));
    }

    /**
     * Test get cart item with wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void getCartItemWrongUserTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/cartItem/1")).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }

    /**
     * Test delete cart item.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void deleteCartItemTest() throws Exception {
        MvcResult result = mvc.perform(delete("/api/cartItem/1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"cartitem_deleted_successfully\"}"));
    }

    /**
     * Test delete cart item as wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void deleteCartItemWrongUserTest() throws Exception {
        MvcResult result = mvc.perform(delete("/api/cartItem/1")).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }

    /**
     * Test change item's quantity.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void changeQuantityTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/cartItem/1/quantity?quantity=5")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"quantity_changed_successfully\"}"));

        result = mvc.perform(get("/api/cartItem/1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":1,\"productId\":1,\"name\":\"Phone\",\"price\":5.0,\"cartId\":1,\"quantity\":5}"));
    }

    /**
     * Test change item's quantity as wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void changeQuantityWrongUserTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/cartItem/1/quantity?quantity=5")).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }

    /**
     * Test get cart items.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCartItemsTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/cartItem?username=user")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("[{\"id\":1,\"productId\":1,\"name\":\"Phone\",\"price\":5.0,\"cartId\":1,\"quantity\":1}]"));
    }

    /**
     * Test get cart items with wrong user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherUser", authorities = {"USER"})
    void getCartItemsWrongUserTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/cartItem?username=user")).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }

    /**
     * Test change item's quantity to negative value.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void changeQuantityToNegativeTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/cartItem/1/quantity?quantity=-4")).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"fieldErrors\":[{\"field\":\"changeQuantity.quantity\",\"message\":\"Quantity should be greater or equal to 1.\"}]}"));
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
