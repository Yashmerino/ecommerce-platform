package com.yashmerino.ecommerce.selenium.it.user;

import com.yashmerino.ecommerce.selenium.it.BaseIT;
import com.yashmerino.ecommerce.selenium.it.pages.cart.MyCartPage;
import com.yashmerino.ecommerce.selenium.it.pages.products.AddProductPage;
import com.yashmerino.ecommerce.selenium.it.pages.products.MyProductsPage;
import com.yashmerino.ecommerce.selenium.it.pages.products.ProductsPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * User integration tests.
 */
class UserIT extends BaseIT {

    /**
     * Tests that a product is added to the cart.
     */
    @Test
    void testAddProductToCart() {
        final String productName = "test";

        assertTrue(createTestSellerAndLogin());

        currentPage = new ProductsPage(driver, wait);
        ((ProductsPage) currentPage).clickAddProductButton();

        currentPage = new AddProductPage(driver, wait);
        ((AddProductPage) currentPage).addProductToCard(productName);
        ((AddProductPage) currentPage).clickHomeButton();

        currentPage = new ProductsPage(driver, wait);
        assertTrue(((ProductsPage) currentPage).productExists(productName));

        ((ProductsPage) currentPage).clickMyProductsButton();
        currentPage = new MyProductsPage(driver, wait);
        assertTrue(((MyProductsPage) currentPage).productExists(productName));

        driver.navigate().to(REGISTER_PAGE_URL);
        assertTrue(createTestUserAndLogin());

        currentPage = new ProductsPage(driver, wait);
        ((ProductsPage) currentPage).addProductToCard(productName);
        ((ProductsPage) currentPage).clickMyCartButton();

        currentPage = new MyCartPage(driver, wait);
        assertTrue(((MyCartPage) currentPage).productExists(productName));
    }
}
