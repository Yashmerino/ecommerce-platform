package com.yashmerino.ecommerce.selenium.it.pages.auth;

import com.yashmerino.ecommerce.selenium.it.pages.BasePage;
import com.yashmerino.ecommerce.selenium.it.utils.Role;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model that represents Register page.
 */
public class RegisterPage extends BasePage {

    /**
     * Role selection field element.
     */
    private final By roleSelectField = By.id("role");

    /**
     * User role selection field item.
     */
    private final By userRoleItem = By.id("user-role");

    /**
     * Seller role selection field item.
     */
    private final By sellerRoleItem = By.id("seller-role");

    /**
     * Email selection field element.
     */
    private final By emailTextField = By.id("email");

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
     * Have already an account button.
     */
    private final By haveAlreadyAccountButton = By.id("have-already-account");

    /**
     * Constructor.
     *
     * @param driver web driver to perform browser actions.
     * @param wait   wait web driver to wait until certain conditions met.
     */
    public RegisterPage(final WebDriver driver, final WebDriverWait wait) {
        super(driver, wait);
    }

    /**
     * Registers a new user.
     *
     * @param role     is the user's role.
     * @param email    is the user's email.
     * @param username is the user's username.
     * @param password is the user's password.
     */
    public void registerUser(final Role role, final String email, final String username, final String password) {
        selectRole(role);

        driver.findElement(emailTextField).sendKeys(email);
        driver.findElement(usernameTextField).sendKeys(username);
        driver.findElement(passwordTextField).sendKeys(password);

        driver.findElement(submitButton).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert));
    }

    /**
     * Go to login page by pressing "Have already an account" button.
     */
    public void goToLoginPage() {
        driver.findElement(haveAlreadyAccountButton).click();
    }

    /**
     * Selects a role.
     *
     * @param role is the role to select.
     */
    private void selectRole(final Role role) {
        driver.findElement(roleSelectField).click();

        if (role.equals(Role.USER)) {
            driver.findElement(userRoleItem).click();
        } else {
            driver.findElement(sellerRoleItem).click();
        }
    }
}
