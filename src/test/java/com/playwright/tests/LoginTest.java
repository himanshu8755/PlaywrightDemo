package com.playwright.tests;

import com.microsoft.playwright.Page;
import com.playwright.config.TestConfig;
import com.playwright.pages.LoginPage;
import com.playwright.support.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

public class LoginTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(LoginTest.class.getName());

    @Test
    @Story("Valid login flow")
    @Description("A valid user can sign in and view the product inventory.")
    public void validUserCanLogIn() {
        LOGGER.info("Starting end-to-end login test");
        LoginPage loginPage = new LoginPage(page);
        LOGGER.info("Opening login page: " + TestConfig.baseUrl());
        loginPage.open(TestConfig.baseUrl());
        LOGGER.info("Submitting login form");
        loginPage.login(TestConfig.username(), TestConfig.password());

        // Assert the outcome of the action, not just that the button was clicked.
        Assert.assertTrue(page.url().contains("inventory.html"),
            "A successful login should navigate to the inventory page");
        Assert.assertTrue(page.getByText("Products", new Page.GetByTextOptions().setExact(true)).isVisible(),
            "The product inventory should be visible after login");

        LOGGER.info("Login succeeded; attaching screenshot to the Allure report");
        attachScreenshot("Inventory page after successful login");
        LOGGER.info("End-to-end login test completed successfully");
    }

    @Test
    @Story("Invalid login flow")
    @Description("An unknown username is rejected with a helpful error message.")
    public void invalidUsernameShowsError() {
        LOGGER.info("Starting invalid-username login test");
        LoginPage loginPage = new LoginPage(page);
        loginPage.open(TestConfig.baseUrl());
        loginPage.login("unknown_user", TestConfig.password());

        Assert.assertTrue(loginPage.errorMessage().contains("Username and password do not match"),
            "An invalid username should show the authentication error");
        attachScreenshot("Login page after invalid username");
        LOGGER.info("Invalid username was rejected as expected");
    }

    @Test
    @Story("Blank login flow")
    @Description("Submitting the login form without credentials shows a required-username error.")
    public void blankSubmitShowsRequiredUsernameError() {
        LOGGER.info("Starting blank-submit login test");
        LoginPage loginPage = new LoginPage(page);
        loginPage.open(TestConfig.baseUrl());
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.errorMessage().contains("Username is required"),
            "Submitting an empty form should require a username");
        attachScreenshot("Login page after blank submit");
        LOGGER.info("Blank submit was rejected as expected");
    }

    private void attachScreenshot(String name) {
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.attachment(name, "image/png", new ByteArrayInputStream(screenshot),
            AttachmentOptions.withFileExtension(".png"));
    }
}
