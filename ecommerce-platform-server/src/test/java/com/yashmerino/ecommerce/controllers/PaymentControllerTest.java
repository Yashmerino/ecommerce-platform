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
import com.yashmerino.ecommerce.model.dto.PaymentDTO;
import com.yashmerino.ecommerce.services.interfaces.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Tests for {@link PaymentController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class PaymentControllerTest {

    /**
     * Payment DTO to use in tests.
     */
    private final PaymentDTO paymentDTO = new PaymentDTO();

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

    @MockBean
    private PaymentService paymentService;

    @BeforeEach
    void setup() {
        paymentDTO.setOrderId(1L);
        paymentDTO.setStripeToken("STRIPE-TOKEN");
        paymentDTO.setAmount(BigDecimal.valueOf(2.50));
    }

    /**
     * Test send pay event.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void sendPayEventTest() throws Exception {
        doNothing().when(paymentService).pay(anyLong(), any(PaymentDTO.class));

        mvc.perform(post("/api/payment/1")
                .content(objectMapper.writeValueAsString(paymentDTO)).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("payment_sent_successfully"));
    }

    /**
     * Test send pay event as seller.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void sendPayEventAsSellerTest() throws Exception {
        mvc.perform(post("/api/payment/1")).andExpect(status().isForbidden()).andReturn();
    }
}
