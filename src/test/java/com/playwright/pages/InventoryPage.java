package com.playwright.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import com.playwright.agents.healing.LocatorHealer;

/** Page object for the product inventory shown after login. */
public class InventoryPage {
    private final Page page;

    public InventoryPage(Page page) {
        this.page = page;
    }

    public boolean isLoaded() {
        return page.getByText("Products", new Page.GetByTextOptions().setExact(true)).isVisible();
    }

    public void addProductToCart(String productName) {
        Allure.step("Add " + productName + " to cart", () -> {
            Locator productCard = page.locator(".inventory_item").filter(new Locator.FilterOptions()
                .setHas(page.getByText(productName, new Page.GetByTextOptions().setExact(true))));
            LocatorHealer.resolve("Add to cart button",
                LocatorHealer.candidates(
                    LocatorHealer.candidate("accessible Add to cart button",
                        productCard.getByRole(AriaRole.BUTTON,
                            new Locator.GetByRoleOptions().setName("Add to cart"))),
                    LocatorHealer.candidate("data-test add button", productCard.locator("[data-test^='add-to-cart']"))
                )).click();
        });
    }

    public String cartItemCount() {
        return page.locator(".shopping_cart_badge").innerText();
    }

    public void openCart() {
        Allure.step("Open shopping cart", () -> page.locator(".shopping_cart_link").click());
    }
}
