package com.playwright.tests;

import com.playwright.agents.planner.TestPlan;
import com.playwright.agents.planner.TestPlanner;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.logging.Logger;

/** Shows the planner output before the UI scenarios are executed. */
public class AgentPlanTest {
    private static final Logger LOGGER = Logger.getLogger(AgentPlanTest.class.getName());

    @Test
    @Story("Planner")
    @Description("Creates a structured plan for the login and cart flows covered by this project.")
    public void createLoginAndCartTestPlan() {
        TestPlan plan = new TestPlanner().createPlan();
        Assert.assertEquals(plan.scenarios().size(), 5, "Planner should describe the five current user flows");
        Allure.attachment("Generated test plan", "text/markdown", plan.asMarkdown());
        LOGGER.info("Planner created a test plan with " + plan.scenarios().size() + " scenarios");
    }
}
