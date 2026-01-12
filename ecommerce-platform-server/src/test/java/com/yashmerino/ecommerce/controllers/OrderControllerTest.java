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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashmerino.ecommerce.model.dto.OrderDTO;
import com.yashmerino.ecommerce.model.dto.ProductDTO;
import com.yashmerino.ecommerce.utils.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link OrderController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class OrderControllerTest {

    /**
     * Order DTO to use in tests.
     */
    private final OrderDTO orderDTO = new OrderDTO();

    /**
     * Mock mvc to perform requests.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Object mapper.
     */
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        orderDTO.setStatus(OrderStatus.CREATED);
        orderDTO.setTotalAmount(BigDecimal.valueOf(2.50));
        orderDTO.setUserId(1L);
    }

    /**
     * Test place order.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void placeOrderTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/order")
                .content(objectMapper.writeValueAsString(orderDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"order_placed_successfully\",\"id\":2}"));
    }

    /**
     * Test place order as seller.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void placeOrderAsSellerTest() throws Exception {
        mvc.perform(post("/api/order")
                .content(objectMapper.writeValueAsString(orderDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();
    }
}
