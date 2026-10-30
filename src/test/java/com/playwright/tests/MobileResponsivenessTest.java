package com.playwright.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.playwright.config.TestConfig;
import com.playwright.pages.CartPage;
import com.playwright.pages.InventoryPage;
import com.playwright.pages.LoginPage;
import com.playwright.support.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Checks the main shopping flow at the mobile viewport configured by TestNG. */
public class MobileResponsivenessTest extends BaseTest {
    private static final String PRODUCT = "Sauce Labs Backpack";

    @Test
    public void shoppingFlowFitsMobileViewport() {
        Number viewportWidth = (Number) page.evaluate("() => window.innerWidth");
        Assert.assertEquals(viewportWidth.intValue(), 390,
            "The mobile suite should use a 390px viewport");

        new LoginPage(page).open(TestConfig.baseUrl()).login(TestConfig.username(), TestConfig.password());
        assertNoHorizontalOverflow("inventory");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Open Menu")).click();
        Assert.assertTrue(page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("All Items")).isVisible(),
            "The navigation menu should be usable on mobile");
        assertNoHorizontalOverflow("open navigation menu");
        page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Close Menu")).click();

        InventoryPage inventory = new InventoryPage(page);
        inventory.addProductToCart(PRODUCT);
        inventory.openCart();
        assertNoHorizontalOverflow("cart");

        CartPage cart = new CartPage(page);
        Assert.assertTrue(cart.containsProduct(PRODUCT), "The selected product should remain visible on mobile");
        cart.checkout();
        Assert.assertTrue(page.getByPlaceholder("First Name").isVisible(),
            "Checkout fields should be visible on mobile");
        assertNoHorizontalOverflow("checkout information");
    }

    private void assertNoHorizontalOverflow(String pageName) {
        boolean fitsViewport = (Boolean) page.evaluate(
            "() => document.documentElement.scrollWidth <= document.documentElement.clientWidth");
        Assert.assertTrue(fitsViewport, "The " + pageName + " page should not overflow the mobile viewport");
    }
}
