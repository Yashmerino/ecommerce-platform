package com.yashmerino.ecommerce.selenium.it.pages.cart;

import com.yashmerino.ecommerce.selenium.it.pages.BaseHeaderPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model that represents my cart page.
 */
public class MyCartPage extends BaseHeaderPage {
    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public MyCartPage(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    /**
     * Checks if a product exists.
     *
     * @param productName is the product's name.
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean productExists(final String productName) {
        try {
            driver.findElement(By.xpath(String.format("//h6[contains(string(), %s)]", productName)));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
