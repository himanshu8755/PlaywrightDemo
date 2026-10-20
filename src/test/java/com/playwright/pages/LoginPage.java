package com.playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;

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
        Allure.step("Enter username", () -> page.getByPlaceholder("Username").fill(username));
    }

    public void enterPassword(String password) {
        // Keep credential values out of logs and Allure step titles.
        Allure.step("Enter password", () -> page.getByPlaceholder("Password").fill(password));
    }

    public void clickLogin() {
        Allure.step("Click Login", () ->
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click());
    }

    public String errorMessage() {
        return page.locator("[data-test='error']").innerText();
    }
}
