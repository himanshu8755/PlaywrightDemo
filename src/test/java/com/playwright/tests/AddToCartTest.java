package com.playwright.tests;

import com.microsoft.playwright.Page;
import com.playwright.config.TestConfig;
import com.playwright.pages.CartPage;
import com.playwright.pages.InventoryPage;
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

public class AddToCartTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(AddToCartTest.class.getName());
    private static final String PRODUCT = "Sauce Labs Backpack";

    @Test
    @Story("Add product to cart")
    @Description("A signed-in user can add a product and see it in the shopping cart.")
    public void loggedInUserCanAddProductToCart() {
        LOGGER.info("Starting add-to-cart flow");
        new LoginPage(page).open(TestConfig.baseUrl()).login(TestConfig.username(), TestConfig.password());

        InventoryPage inventory = new InventoryPage(page);
        Assert.assertTrue(inventory.isLoaded(), "Inventory should be visible after login");

        inventory.addProductToCart(PRODUCT);
        Assert.assertEquals(inventory.cartItemCount(), "1", "Cart badge should show one item");

        inventory.openCart();
        CartPage cart = new CartPage(page);
        Assert.assertTrue(cart.isLoaded(), "Shopping cart page should be visible");
        Assert.assertTrue(cart.containsProduct(PRODUCT), "Selected product should appear in the cart");

        LOGGER.info("Product was added and verified; attaching cart screenshot");
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.attachment("Cart after adding " + PRODUCT, "image/png",
            new ByteArrayInputStream(screenshot), AttachmentOptions.withFileExtension(".png"));
        LOGGER.info("Add-to-cart flow completed successfully");
    }
}
