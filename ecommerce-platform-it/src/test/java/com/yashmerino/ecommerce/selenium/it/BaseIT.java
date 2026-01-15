package com.yashmerino.ecommerce.selenium.it;

import com.yashmerino.ecommerce.selenium.it.pages.BasePage;
import com.yashmerino.ecommerce.selenium.it.pages.auth.LoginPage;
import com.yashmerino.ecommerce.selenium.it.pages.auth.RegisterPage;
import com.yashmerino.ecommerce.selenium.it.utils.Role;
import com.yashmerino.ecommerce.selenium.it.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.yashmerino.ecommerce.selenium.it.utils.SeleniumProperties.CHROME_DRIVER_PROPERTY;

/**
 * Base selenium integration test.
 */
public class BaseIT { // NOSONAR: This class should be public for inheritance.

    /**
     * Register page URL.
     */
    protected final String REGISTER_PAGE_URL = "http://localhost:8081/#/register";

    /**
     * Timeout in seconds.
     */
    private final Duration TIMEOUT_IN_SECONDS = Duration.ofSeconds(30);

    /**
     * Path to the chrome driver.
     */
    protected String CHROME_DRIVER_PATH = "src/test/resources/chromedriver.exe";

    /**
     * Web Driver to execute browser actions.
     */
    protected WebDriver driver;

    /**
     * Wait web driver to wait until certain conditions met.
     */
    protected WebDriverWait wait;

    /**
     * Current page.
     */
    protected BasePage currentPage;

    /**
     * Constructor.
     */
    public BaseIT() {
        System.setProperty(CHROME_DRIVER_PROPERTY, CHROME_DRIVER_PATH);

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, TIMEOUT_IN_SECONDS);
    }

    /**
     * Method that is called before each test.
     */
    @BeforeEach
    void beforeEach() {
        TestUtils.executeSQLScript(TestUtils.SQL_CLEAN_SCRIPT_FILE);
    }

    /**
     * Method that is executed after each test.
     */
    @AfterEach
    void tearDown() {
        driver.quit();
    }

    /**
     * Creates a test seller and logins.
     *
     * @return <code>true</code> if everything went ok and <code>false</code> otherwise.
     */
    protected boolean createTestSellerAndLogin() {
        try {
            driver.get(REGISTER_PAGE_URL);
            currentPage = new RegisterPage(driver, wait);
            ((RegisterPage) currentPage).registerUser(Role.SELLER, "test@mail.ru", "seller", "seller");

            ((RegisterPage) currentPage).goToLoginPage();

            currentPage = new LoginPage(driver, wait);
            ((LoginPage) currentPage).loginUser("seller", "seller");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a test user and logins.
     *
     * @return <code>true</code> if everything went ok and <code>false</code> otherwise.
     */
    protected boolean createTestUserAndLogin() {
        try {
            driver.get(REGISTER_PAGE_URL);
            currentPage = new RegisterPage(driver, wait);
            ((RegisterPage) currentPage).registerUser(Role.USER, "test@mail.ru", "user", "user");

            ((RegisterPage) currentPage).goToLoginPage();

            currentPage = new LoginPage(driver, wait);
            ((LoginPage) currentPage).loginUser("user", "user");

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
