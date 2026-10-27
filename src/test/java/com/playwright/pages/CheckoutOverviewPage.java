package com.playwright.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;

/** Review step that displays the order total before confirmation. */
public class CheckoutOverviewPage {
    private final Page page;

    public CheckoutOverviewPage(Page page) {
        this.page = page;
    }

    public boolean isLoaded() {
        return page.getByText("Checkout: Overview", new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public boolean containsProduct(String productName) {
        return page.getByText(productName, new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public String orderTotal() {
        return page.locator(".summary_total_label").innerText();
    }

    public void finishOrder() {
        Allure.step("Finish order", () ->
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Finish")).click());
    }
}
