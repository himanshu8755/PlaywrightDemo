package com.playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;

/** Page object for checking items in the shopping cart. */
public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public boolean isLoaded() {
        return page.getByText("Your Cart", new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public boolean containsProduct(String productName) {
        return page.getByText(productName, new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public void checkout() {
        Allure.step("Start checkout", () ->
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Checkout")).click());
    }
}
