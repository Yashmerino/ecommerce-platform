package com.yashmerino.ecommerce.selenium.it.pages.products;

import com.yashmerino.ecommerce.selenium.it.pages.BaseHeaderPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model that represents products page.
 */
public class ProductsPage extends BaseHeaderPage {

    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public ProductsPage(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    /**
     * Adds a product to the card.
     *
     * @param productName is the product's name.
     */
    public void addProductToCard(final String productName) {
        driver.findElement(By.cssSelector("[id^=" + productName + "] button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert));
    }

    /**
     * Checks if a product exists.
     *
     * @param productName is the product's name.
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean productExists(final String productName) {
        try {
            driver.findElement(By.xpath(String.format("//p[contains(string(), %s)]", productName)));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
