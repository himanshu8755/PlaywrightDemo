package com.playwright.agents.planner;

import java.util.List;

/**
 * First learning version of the planner: turns the project's known user flows into a readable plan.
 * It is intentionally deterministic; application discovery and LLM planning can be added later.
 */
public class TestPlanner {
    public TestPlan createPlan() {
        return new TestPlan(List.of(
            new TestPlan.Scenario("Valid login", "A registered user can open the inventory.",
                List.of("Open the login page", "Enter valid credentials", "Click Login"),
                "The inventory page is displayed"),
            new TestPlan.Scenario("Invalid username", "An unknown user is rejected.",
                List.of("Open the login page", "Enter an unknown username", "Click Login"),
                "An authentication error is displayed"),
            new TestPlan.Scenario("Blank submit", "The login form validates required input.",
                List.of("Open the login page", "Leave both fields empty", "Click Login"),
                "A username-required error is displayed"),
            new TestPlan.Scenario("Add product to cart", "A signed-in user can add a product.",
                List.of("Log in", "Add Sauce Labs Backpack", "Open the cart"),
                "The cart badge shows one and the product appears in the cart"),
            new TestPlan.Scenario("Complete checkout", "A user can complete the demo purchase flow.",
                List.of("Log in", "Add a product", "Enter customer details", "Review and finish the order"),
                "The order confirmation page is displayed")
        ));
    }
}
