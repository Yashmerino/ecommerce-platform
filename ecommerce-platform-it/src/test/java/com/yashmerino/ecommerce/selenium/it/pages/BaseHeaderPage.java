package com.yashmerino.ecommerce.selenium.it.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class for pages that have the header component.
 */
public class BaseHeaderPage extends BasePage {
    /**
     * My Profile header button.
     */
    protected final By profileButton = By.id("profile-button");

    /**
     * My Cart header button.
     */
    protected final By myCartButton = By.id("my-cart-button");

    /**
     * My Products header button.
     */
    protected final By myProductsButton = By.id("my-products-button");

    /**
     * Add Product header button.
     */
    protected final By addProductButton = By.id("add-product-button");

    /**
     * Home header button.
     */
    protected final By homeButton = By.id("home-title");

    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public BaseHeaderPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    /**
     * Clicks Add Product header button.
     */
    public void clickAddProductButton() {
        driver.findElement(addProductButton).click();
        wait.until(ExpectedConditions.urlContains("product/add"));
    }

    /**
     * Clicks My Products button.
     */
    public void clickMyProductsButton() {
        driver.findElement(myProductsButton).click();
        wait.until(ExpectedConditions.urlContains("profile/products"));
    }

    /**
     * Clicks Home button.
     */
    public void clickHomeButton() {
        driver.findElement(homeButton).click();
        wait.until(ExpectedConditions.urlContains("products"));
    }

    /**
     * Clicks My Cart button.
     */
    public void clickMyCartButton() {
        driver.findElement(myCartButton).click();
        wait.until(ExpectedConditions.urlContains("cart"));
    }
}
