package com.yashmerino.ecommerce.selenium.it.pages.products;

import com.yashmerino.ecommerce.selenium.it.pages.BaseHeaderPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model that represents add product page.
 */
public class AddProductPage extends BaseHeaderPage {
    /**
     * Name field.
     */
    private final By nameField = By.id("name-field");

    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public AddProductPage(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    /**
     * Adds a product.
     *
     * @param productName is the product's name.
     */
    public void addProductToCard(final String productName) {
        driver.findElement(nameField).sendKeys(productName);
        driver.findElement(submitButton).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert));
    }
}
