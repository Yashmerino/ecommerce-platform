package com.yashmerino.ecommerce.selenium.it.pages.auth;

import com.yashmerino.ecommerce.selenium.it.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model that represents Login page.
 */
public class LoginPage extends BasePage {
    /**
     * Username selection field element.
     */
    private final By usernameTextField = By.id("username");

    /**
     * Password selection field element.
     */
    private final By passwordTextField = By.id("password");

    /**
     * Submit button
     */
    private final By submitButton = By.id("submit");

    /**
     * Don't have an account button.
     */
    private final By dontHaveAccountButton = By.id("dont-have-account");

    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public LoginPage(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    /**
     * Logins the user
     *
     * @param username is the user's username.
     * @param password is the user's password.
     */
    public void loginUser(final String username, final String password) {
        driver.findElement(usernameTextField).sendKeys(username);
        driver.findElement(passwordTextField).sendKeys(password);

        driver.findElement(submitButton).click();

        wait.until(ExpectedConditions.urlContains("products"));
    }

    /**
     * Go to register page by pressing "Don't have an account" button.
     */
    public void goToRegisterPage() {
        driver.findElement(dontHaveAccountButton).click();
    }
}
