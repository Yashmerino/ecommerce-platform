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
import com.yashmerino.ecommerce.kafka.NotificationEventProducer;
import com.yashmerino.ecommerce.model.dto.PaymentDTO;
import com.yashmerino.ecommerce.model.dto.auth.LoginDTO;
import com.yashmerino.ecommerce.model.dto.auth.RegisterDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.yashmerino.ecommerce.utils.Role.USER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.hamcrest.Matchers;

/**
 * Tests {@link AuthController} endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Transactional
@ActiveProfiles("test")
class AuthControllerTest {

    /**
     * MVC Mock.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Object mapper.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Register DTO.
     */
    private RegisterDTO registerDTO;

    /**
     * Login DTO.
     */
    private LoginDTO loginDTO;

    /**
     * Notification event producer mock.
     */
    @MockBean
    private NotificationEventProducer notificationEventProducer;

    @BeforeEach
    void setup() {
        registerDTO = new RegisterDTO();
        registerDTO.setRole(USER);
        registerDTO.setUsername("test");
        registerDTO.setPassword("test");
        registerDTO.setEmail("test@test.test");

        loginDTO = new LoginDTO();
        loginDTO.setUsername("test");
        loginDTO.setPassword("test");
    }

    /**
     * Tests that /register endpoint works as expected.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void registerSuccessfulTest() throws Exception {
        doNothing().when(notificationEventProducer).sendWelcomeNotificationRequested(anyString());

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("user_registered_successfully"));
    }

    /**
     * Tests /register with existing user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void registerExistingUserTest() throws Exception {
        doNothing().when(notificationEventProducer).sendWelcomeNotificationRequested(anyString());

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO))).andExpect(status().isOk());

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("username_taken"));
    }

    /**
     * Test if an email has no sign, is invalid and is not provided.
     */
    @ParameterizedTest
    @ValueSource(strings = {"nosign_email", "@invalid.mail", ""})
    void registerNoSignEmailTest() throws Exception {
        registerDTO.setEmail("nosign_email");

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("email_is_invalid"));
    }

    /**
     * Tests /register without username.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void registerNoUsername() throws Exception {
        registerDTO.setUsername("");

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'username' && @.message == 'username_is_required')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'username' && @.message == 'username_invalid_length')]").exists());
    }

    /**
     * Tests /register without body.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void registerNoRequestBodyTest() throws Exception {
        mvc.perform(post("/api/auth/register")).andExpect(status().isBadRequest()).andReturn();
    }

    /**
     * Tests /register without password.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void registerNoPasswordTest() throws Exception {
        registerDTO.setPassword("");

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password' && @.message == 'password_invalid_length')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password' && @.message == 'password_is_required')]").exists());
    }

    /**
     * Tests that /login endpoint works as expected.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void loginSuccessfulTest() throws Exception {
        doNothing().when(notificationEventProducer).sendWelcomeNotificationRequested(anyString());

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO))).andExpect(status().isOk());

        mvc.perform(post("/api/auth/login").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value(org.hamcrest.Matchers.containsString("Bearer ")));
    }

    /**
     * Tests /login with a non-existing username.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void usernameDoesntExistTest() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("username_not_found"));
    }

    /**
     * Tests /login without username.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void loginNoUsernameTest() throws Exception {
        doNothing().when(notificationEventProducer).sendWelcomeNotificationRequested(anyString());

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO))).andExpect(status().isOk());

        loginDTO.setUsername("");

        mvc.perform(post("/api/auth/login").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("username"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("username_is_required"));
    }

    /**
     * Tests /login without password.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void loginNoPasswordTest() throws Exception {
        doNothing().when(notificationEventProducer).sendWelcomeNotificationRequested(anyString());

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO))).andExpect(status().isOk());

        loginDTO.setPassword("");

        mvc.perform(post("/api/auth/login").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("password"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("password_is_required"));
    }

    /**
     * Tests /register with a username that has invalid length
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void registerUsernameInvalidLengthTest() throws Exception {
        registerDTO.setUsername("gaerghetrhatetaewrvtuaewtgarhgyjaregdwafdawfawfawdawfarhgyjaregdwafdawfawfawdawfarhgyjaregdwafdawfawfawdawfarhgyjaregdwafdawfawfawdawfarhgyjaregdwafdawfawfawdawfwefawefaewfwaefawefawfawfawdawfwefawefaewfwaefawefawfawfawdawfwefawefaewfwaefawefawfawfawdawfwefawefaewfwaefawefawehtyjargeytjgaeRJGYAERKGFHAGERKUGHERUKGHARUKGhgukraehgkuerahkughkurgaehukg");

        mvc.perform(post("/api/auth/register").contentType(
                APPLICATION_JSON).content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("username"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("username_invalid_length"));
    }
}
