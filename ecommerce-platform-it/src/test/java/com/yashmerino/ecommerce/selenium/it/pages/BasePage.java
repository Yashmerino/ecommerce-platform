package com.yashmerino.ecommerce.selenium.it.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base Page for Page Object Model.
 */
public class BasePage {

    /**
     * Web Driver to make browser actions.
     */
    protected final WebDriver driver;

    /**
     * Wait web driver to wait until certain conditions met.
     */
    protected final WebDriverWait wait;

    /**
     * Success alert element.
     */
    protected final By successAlert = By.id("alert-success");

    /**
     * Submit button.
     */
    protected final By submitButton = By.id("submit-button");

    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public BasePage(final WebDriver driver, final WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
}
