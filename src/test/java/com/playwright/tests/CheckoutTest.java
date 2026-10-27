package com.playwright.tests;

import com.microsoft.playwright.Page;
import com.playwright.config.TestConfig;
import com.playwright.pages.CartPage;
import com.playwright.pages.CheckoutInformationPage;
import com.playwright.pages.CheckoutOverviewPage;
import com.playwright.pages.InventoryPage;
import com.playwright.pages.LoginPage;
import com.playwright.pages.OrderConfirmationPage;
import com.playwright.support.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

public class CheckoutTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(CheckoutTest.class.getName());
    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    @Test
    @Story("Complete checkout")
    @Description("A user can add a product, provide checkout details, and reach order confirmation.")
    public void userCanCompleteCheckout() {
        LOGGER.info("Starting end-to-end checkout flow");
        new LoginPage(page).open(TestConfig.baseUrl()).login(TestConfig.username(), TestConfig.password());

        InventoryPage inventory = new InventoryPage(page);
        Assert.assertTrue(inventory.isLoaded(), "Inventory should appear after login");
        inventory.addProductToCart(PRODUCT_NAME);
        Assert.assertEquals(inventory.cartItemCount(), "1", "Cart should contain one item");
        inventory.openCart();

        CartPage cart = new CartPage(page);
        Assert.assertTrue(cart.containsProduct(PRODUCT_NAME), "Selected product should be in the cart");
        cart.checkout();

        CheckoutInformationPage information = new CheckoutInformationPage(page);
        Assert.assertTrue(information.isLoaded(), "Checkout information page should be visible");
        information.continueWith("Test", "User", "12345");

        CheckoutOverviewPage overview = new CheckoutOverviewPage(page);
        Assert.assertTrue(overview.isLoaded(), "Order overview should be visible");
        Assert.assertTrue(overview.containsProduct(PRODUCT_NAME), "Order overview should contain the selected product");
        Assert.assertEquals(overview.orderTotal(), "Total: $32.39", "Order total should include item price and tax");
        overview.finishOrder();

        OrderConfirmationPage confirmation = new OrderConfirmationPage(page);
        Assert.assertTrue(confirmation.isLoaded(), "Order confirmation page should be visible");
        Assert.assertEquals(confirmation.confirmationMessage(), "Thank you for your order!");

        LOGGER.info("Checkout completed; attaching order confirmation screenshot");
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.attachment("Order confirmation", "image/png", new ByteArrayInputStream(screenshot),
            AttachmentOptions.withFileExtension(".png"));
        LOGGER.info("End-to-end checkout flow completed successfully");
    }
}
