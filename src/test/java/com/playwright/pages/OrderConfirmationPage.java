package com.playwright.pages;

import com.microsoft.playwright.Page;

/** Confirmation page shown after a successful demo checkout. */
public class OrderConfirmationPage {
    private final Page page;

    public OrderConfirmationPage(Page page) {
        this.page = page;
    }

    public boolean isLoaded() {
        return page.getByText("Checkout: Complete!", new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public String confirmationMessage() {
        return page.locator(".complete-header").innerText();
    }
}
