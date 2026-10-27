package com.playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import com.playwright.agents.healing.LocatorHealer;

/** Page object: keeps login page locators and actions in one place. */
public class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public LoginPage open(String baseUrl) {
        page.navigate(baseUrl);
        return this; // Allows the test to chain open(...).login(...).
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public void enterUsername(String username) {
        Allure.step("Enter username", () -> LocatorHealer.resolve("username field",
            LocatorHealer.candidates(
                LocatorHealer.candidate("Username placeholder", page.getByPlaceholder("Username")),
                LocatorHealer.candidate("data-test username", page.locator("[data-test='username']")),
                LocatorHealer.candidate("username id", page.locator("#user-name"))
            )).fill(username));
    }

    public void enterPassword(String password) {
        // Keep credential values out of logs and Allure step titles.
        Allure.step("Enter password", () -> LocatorHealer.resolve("password field",
            LocatorHealer.candidates(
                LocatorHealer.candidate("Password placeholder", page.getByPlaceholder("Password")),
                LocatorHealer.candidate("data-test password", page.locator("[data-test='password']")),
                LocatorHealer.candidate("password id", page.locator("#password"))
            )).fill(password));
    }

    public void clickLogin() {
        Allure.step("Click Login", () -> LocatorHealer.resolve("Login button",
            LocatorHealer.candidates(
                LocatorHealer.candidate("accessible Login button",
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"))),
                LocatorHealer.candidate("data-test login button", page.locator("[data-test='login-button']")),
                LocatorHealer.candidate("login button id", page.locator("#login-button"))
            )).click());
    }

    public String errorMessage() {
        return page.locator("[data-test='error']").innerText();
    }
}
