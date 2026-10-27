package com.playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;

/** Customer information step in checkout. */
public class CheckoutInformationPage {
    private final Page page;

    public CheckoutInformationPage(Page page) {
        this.page = page;
    }

    public boolean isLoaded() {
        return page.getByText("Checkout: Your Information", new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public void continueWith(String firstName, String lastName, String postalCode) {
        Allure.step("Enter first name", () -> page.getByPlaceholder("First Name").fill(firstName));
        Allure.step("Enter last name", () -> page.getByPlaceholder("Last Name").fill(lastName));
        Allure.step("Enter postal code", () -> page.getByPlaceholder("Zip/Postal Code").fill(postalCode));
        Allure.step("Continue to order overview", () ->
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click());
    }
}
