package com.playwright.agents.planner;

import java.util.List;

/** Structured output from the starter rule-based test planner. */
public record TestPlan(List<Scenario> scenarios) {
    public record Scenario(String name, String goal, List<String> steps, String expectedResult) {}

    public String asMarkdown() {
        StringBuilder report = new StringBuilder("# Planned Playwright scenarios\n\n");
        for (int i = 0; i < scenarios.size(); i++) {
            Scenario scenario = scenarios.get(i);
            report.append("## ").append(i + 1).append(". ").append(scenario.name()).append("\n\n")
                .append("**Goal:** ").append(scenario.goal()).append("\n\n")
                .append("**Steps:**\n");
            for (String step : scenario.steps()) {
                report.append("- ").append(step).append("\n");
            }
            report.append("\n**Expected:** ").append(scenario.expectedResult()).append("\n\n");
        }
        return report.toString();
    }
}
