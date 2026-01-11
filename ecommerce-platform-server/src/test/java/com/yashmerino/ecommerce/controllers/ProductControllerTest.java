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
import com.yashmerino.ecommerce.model.Category;
import com.yashmerino.ecommerce.model.dto.ProductDTO;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link ProductController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ProductControllerTest {

    /**
     * Product DTO to use in tests.
     */
    private final ProductDTO productDTO = new ProductDTO();

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
        productDTO.setName("Product");
        productDTO.setPrice(2.50);
        productDTO.setCategories(new HashSet<>());
    }

    /**
     * Test add valid product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void addValidProductTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":3}"));
    }

    /**
     * Test add invalid product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void addInvalidProductTest() throws Exception {
        mvc.perform(post("/api/product")
                .content("error").contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    /**
     * Test get product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getProductTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":3}"));

        result = mvc.perform(get("/api/product/3")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":\"3\",\"name\":\"Product\",\"price\":2.5,\"categories\":[],\"description\":null}"));
    }

    /**
     * Test get all products.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getAllProductsTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":3}"));

        productDTO.setName("Banana");
        productDTO.setPrice(1.25);

        result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":4}"));

        result = mvc.perform(get("/api/product")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("[{\"id\":\"1\",\"name\":\"Phone\",\"price\":5.0,\"categories\":[],\"description\":null},{\"id\":\"2\",\"name\":\"Laptop\",\"price\":3.0,\"categories\":[],\"description\":null},{\"id\":\"3\",\"name\":\"Product\",\"price\":2.5,\"categories\":[],\"description\":null},{\"id\":\"4\",\"name\":\"Banana\",\"price\":1.25,\"categories\":[],\"description\":null}]"));
    }

    /**
     * Test add product to cart.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void addProductToCartTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/product/1/add?cartId=1&quantity=1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_to_cart_successfully\"}"));
    }

    /**
     * Test request with wrong role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void requestWithWrongRoleTest() throws Exception {
        mvc.perform(post("/api/product")
                .content("error").contentType(
                        APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    /**
     * Test delete product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void deleteProductTest() throws Exception {
        MvcResult result = mvc.perform(delete("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_deleted_successfully\"}"));
    }

    /**
     * Test delete non-existent product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void deleteNonexistentProductTest() throws Exception {
        MvcResult result = mvc.perform(delete("/api/product/99999")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":404,\"error\":\"Product couldn't be found!\"}"));
    }

    /**
     * Test add product without name.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void addProductWithoutNameTest() throws Exception {
        productDTO.setName("");

        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"name\",\"message\":\"name_invalid_length\"}"));
        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"name\",\"message\":\"name_is_required\"}"));
    }

    /**
     * Test add product with zero price.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void addProductWithZeroPriceTest() throws Exception {
        productDTO.setPrice(0.0);

        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"fieldErrors\":[{\"field\":\"price\",\"message\":\"price_value_error\"}]}"));
    }

    /**
     * Test add product without name and with zero price.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void addProductWithoutNameAndWithZeroPriceTest() throws Exception {
        productDTO.setPrice(0.0);
        productDTO.setName("");

        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"price\",\"message\":\"price_value_error\"}"));
        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"name\",\"message\":\"name_is_required\"}"));
    }

    /**
     * Test get product with categories.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getProductWithCategoriesTest() throws Exception {
        Category digitalServices = new Category();
        digitalServices.setId(1L);
        digitalServices.setName("Digital Services");

        Category cosmeticsAndBodyCare = new Category();
        cosmeticsAndBodyCare.setId(2L);
        cosmeticsAndBodyCare.setName("Cosmetics and Body Care");

        productDTO.setCategories(new LinkedHashSet<>(Arrays.asList(digitalServices, cosmeticsAndBodyCare)));

        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":3}"));

        result = mvc.perform(get("/api/product/3")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":\"3\",\"name\":\"Product\",\"price\":2.5,\"categories\":[{"));
        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":1,\"name\":\"Digital Services\"}"));
        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":2,\"name\":\"Cosmetics and Body Care\"}"));
    }

    /**
     * Test add product with name that has invalid length.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void addProductNameInvalidLengthTest() throws Exception {
        productDTO.setName("geragaergaerghawrighaerwuighaerghaerghaerghaerjighaerwuighaerghaerghghaerwuighaerghaerghaerghaerjighaerwughaerwuighaerghaerghaerghaerjighaerwughaerwuighaerghaerghaerghaerjighaerwughaerwuighaerghaerghaerghaerjighaerwughaerwuighaerghaerghaerghaerjighaerwughaerwuighaerghaerghaerghaerjighaerwuaerghaerjighaerwuighaerghaerghaerghaerjighaerwuighaerghaerghaerghaerjighaerwuighaerghaerghaerghaerjygfaerjygfawjfgawefgaewyfagrjyaerjyaergfjyargfjyarwgyjfa");

        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"fieldErrors\":[{\"field\":\"name\",\"message\":\"name_invalid_length\"}]}"));
    }

    /**
     * Test get all seller's products.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getAllSellerProductsTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":3}"));

        productDTO.setName("Banana");
        productDTO.setPrice(1.25);

        result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":4}"));

        result = mvc.perform(get("/api/product/seller/seller")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("[{\"id\":\"1\",\"name\":\"Phone\",\"price\":5.0,\"categories\":[],\"description\":null},{\"id\":\"3\",\"name\":\"Product\",\"price\":2.5,\"categories\":[],\"description\":null},{\"id\":\"4\",\"name\":\"Banana\",\"price\":1.25,\"categories\":[],\"description\":null}]"));

        result = mvc.perform(get("/api/product/seller/anotherSeller")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("[{\"id\":\"2\",\"name\":\"Laptop\",\"price\":3.0,\"categories\":[],\"description\":null}]"));
    }

    /**
     * Test get all seller's products with non-existing username.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getAllSellerProductsNonexistentUsernameTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/product/seller/ERROR")).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":404,\"error\":\"username_not_found\"}"));
    }

    /**
     * Tests set product's photo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void setProductPhotoTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        MvcResult result = mvc.perform(multipart("/api/product/1/photo").file(photo)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_photo_updated_successfully\"}"));
    }

    /**
     * Tests set product's photo with wrong role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"ERROR"})
    void setProductPhotoWrongRoleTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        mvc.perform(multipart("/api/product/1/photo").file(photo)).andExpect(status().isForbidden()).andReturn();
    }

    /**
     * Tests set product's photo with user role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void setProductPhotoAsUserTest() throws Exception {
        Path photoPath = Path.of("src/test/resources/photos/photo.jpg");

        MockMultipartFile photo
                = new MockMultipartFile(
                "photo",
                "photo.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                Files.readAllBytes(photoPath)
        );

        MvcResult result = mvc.perform(multipart("/api/product/1/photo").file(photo)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_photo_updated_successfully\"}"));
    }

    /**
     * Tests get product's photo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getProductPhotoTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/product/1/photo")).andExpect(status().isOk()).andReturn();

        assertEquals(31163, result.getResponse().getContentAsString().length());
    }

    /**
     * Tests get seller's photo.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getUserPhotoWithUserRoleTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/product/1/photo")).andExpect(status().isOk()).andReturn();

        assertEquals(31163, result.getResponse().getContentAsString().length());
    }

    /**
     * Tests get photo for non-existing product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void getProductPhotoNonexistentProductTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/product/9999/photo")).andExpect(status().isNotFound()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":404,\"error\":\"Product couldn't be found!\"}"));
    }

    /**
     * Test update product.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void updateProductTest() throws Exception {
        MvcResult result = mvc.perform(get("/api/product/1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":\"1\",\"name\":\"Phone\",\"price\":5.0,\"categories\":[],\"description\":null}"));

        productDTO.setName("Android");
        productDTO.setPrice(2.5);

        Category digitalServicesCategory = new Category();
        digitalServicesCategory.setId(1L);
        digitalServicesCategory.setName("Digital Services");
        productDTO.setCategories(new HashSet<>(List.of(digitalServicesCategory)));

        result = mvc.perform(put("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_updated_successfully\"}"));

        result = mvc.perform(get("/api/product/1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":\"1\",\"name\":\"Android\",\"price\":2.5,\"categories\":[{\"id\":1,\"name\":\"Digital Services\"}],\"description\":null}"));
    }

    /**
     * Update product with invalid DTO.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void updateProductInvalidDTOTest() throws Exception {
        productDTO.setName("");
        productDTO.setPrice(-5.2);

        MvcResult result = mvc.perform(put("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"name\",\"message\":\"name_is_required\"}"));
        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"price\",\"message\":\"price_value_error\"}"));
        assertTrue(result.getResponse().getContentAsString().contains("{\"field\":\"name\",\"message\":\"name_invalid_length\"}"));

        result = mvc.perform(get("/api/product/1")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":\"1\",\"name\":\"Phone\",\"price\":5.0,\"categories\":[],\"description\":null}"));
    }

    /**
     * Test update product with user role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"USER"})
    void updateProductWithUserRoleTest() throws Exception {
        productDTO.setName("Android");
        productDTO.setPrice(2.5);

        mvc.perform(put("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();
    }

    /**
     * Test update product with wrong role.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"ERROR"})
    void updateProductWithWrongRoleTest() throws Exception {
        productDTO.setName("Android");
        productDTO.setPrice(2.5);

        mvc.perform(put("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();
    }

    /**
     * Test update product with another seller.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "anotherSeller", authorities = {"SELLER"})
    void updateProductWithWrongSellerTest() throws Exception {
        productDTO.setName("Android");
        productDTO.setPrice(2.5);

        MvcResult result = mvc.perform(put("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isForbidden()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(",\"status\":403,\"error\":\"access_denied\"}"));
    }

    /**
     * Update product with too long description.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void updateProductTooLongDescriptionTest() throws Exception {
        productDTO.setDescription("123123123123123123123123123123123123123123123123123123123123123123123123123123123123123" +
                "1231231231231231231231231231231231231231231231231231231231231231231231231231231231231231231231231231231231" +
                "23123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123" +
                "123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123" +
                "123123123123123123123123123123123123123123");

        MvcResult result = mvc.perform(put("/api/product/1")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("\"field\":\"description\",\"message\":\"description_too_long\""));
    }

    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void productPaginationWithExistingProductsTest() throws Exception {
        // Request first page with size 1
        MvcResult page0 = mvc.perform(get("/api/product?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn();

        String content0 = page0.getResponse().getContentAsString();
        assertTrue(content0.contains("Phone"));   // first product
        assertFalse(content0.contains("Laptop")); // not on first page

        // Request second page with size 1
        MvcResult page1 = mvc.perform(get("/api/product?page=1&size=1"))
                .andExpect(status().isOk())
                .andReturn();

        String content1 = page1.getResponse().getContentAsString();
        assertTrue(content1.contains("Laptop")); // second product
        assertFalse(content1.contains("Phone")); // not on second page

        // Request a page beyond the existing products
        MvcResult page2 = mvc.perform(get("/api/product?page=2&size=1"))
                .andExpect(status().isOk())
                .andReturn();

        String content2 = page2.getResponse().getContentAsString();
        // Should be empty
        assertFalse(content2.contains("Phone"));
        assertFalse(content2.contains("Laptop"));
    }

    /**
     * Test search for products.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    @WithMockUser(username = "seller", authorities = {"SELLER"})
    void searchProductsTest() throws Exception {
        MvcResult result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":3}"));

        productDTO.setName("Banana");
        productDTO.setPrice(1.25);

        result = mvc.perform(post("/api/product")
                .content(objectMapper.writeValueAsString(productDTO)).contentType(
                        APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"status\":200,\"message\":\"product_added_successfully\",\"id\":4}"));

        result = mvc.perform(get("/api/product/search?query=Banana")).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("{\"id\":\"4\",\"name\":\"Banana\",\"price\":1.25,\"categories\":[],\"description\":null}"));
    }
}
