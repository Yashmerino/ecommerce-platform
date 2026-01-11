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
import com.yashmerino.ecommerce.model.dto.auth.UserDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Transactional
@ActiveProfiles("test")
class UserControllerTest {
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

    @BeforeEach
    void setup() {

    }

    /**
     * Tests get user info.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void getUserInfoTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/user/seller")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"roles\":[{\"id\":3,\"name\":\"SELLER\"}],\"email\":null}"));
    }

    /**
     * Tests get user info with non-existing username.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void getUserInfoNonExistingUsernameTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/user/error")).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":404,\"error\":\"username_not_found\"}"));
    }

    /**
     * Tests set user's photo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void setUserPhotoTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        MvcResult result = mvc.perform(multipart("/api/user/user/photo").file(photo)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"user_photo_updated_successfully\"}"));
    }

    /**
     * Tests set user's photo with wrong role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"ERROR"})
    void setUserPhotoWrongRoleTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        mvc.perform(multipart("/api/user/user/photo").file(photo)).andExpect(status().isForbidden()).andReturn();
    }

    /**
     * Tests set user's photo with seller role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void setUserPhotoAsSellerTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        MvcResult result = mvc.perform(multipart("/api/user/seller/photo").file(photo)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"user_photo_updated_successfully\"}"));
    }

    /**
     * Tests set user's photo with wrong username
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"USER"})
    void setUserPhotoWithWrongUsernameTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        MvcResult result = mvc.perform(multipart("/api/user/user/photo").file(photo)).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }

    /**
     * Tests get user's photo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getUserPhotoTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/user/user/photo")).andExpect(status().isOk()).andReturn();

        assertEquals(31163, result.getResponse().getContentAsString().length());
    }

    /**
     * Tests get seller's photo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"SELLER"})
    void getUserPhotoWithSellerRoleTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/user/user/photo")).andExpect(status().isOk()).andReturn();

        assertEquals(31163, result.getResponse().getContentAsString().length());
    }

    /**
     * Tests get photo for non-existing user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void getUserPhotoNonexistentUsernameTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/user/ERROR/photo")).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":404,\"error\":\"username_not_found\"}"));
    }

    /**
     * Tests update user information.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void updateUserTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@mail.com");

        MvcResult result = mvc.perform(put("/api/user/user").content(objectMapper.writeValueAsString(userDTO)).contentType(
                APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"user_information_updated_successfully\"}"));
    }

    /**
     * Tests update user information with wrong role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"ERROR"})
    void updateUserWrongRoleTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@mail.com");

        mvc.perform(put("/api/user/user").content(objectMapper.writeValueAsString(userDTO)).contentType(
                APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();
    }

    /**
     * Tests update user information as seller.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void updateUserWithSellerRoleTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@mail.com");

        MvcResult result = mvc.perform(put("/api/user/seller").content(objectMapper.writeValueAsString(userDTO)).contentType(
                APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"user_information_updated_successfully\"}"));
    }

    /**
     * Tests update user information for nonexistent user.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void updateNonexistentUserTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@mail.com");

        MvcResult result = mvc.perform(put("/api/user/ERROR").content(objectMapper.writeValueAsString(userDTO)).contentType(
                APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":404,\"error\":\"username_not_found\"}"));
    }

    /**
     * Tests update user information with wrong username.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void updateUserWithWrongUsernameTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@mail.com");

        MvcResult result = mvc.perform(put("/api/user/seller").content(objectMapper.writeValueAsString(userDTO)).contentType(
                APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }
}
