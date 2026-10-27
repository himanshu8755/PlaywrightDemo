package com.playwright.agents.healing;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Allure;

import java.util.List;
import java.util.logging.Logger;

/** Tries preferred and fallback locators in order, reporting when a fallback is needed. */
public final class LocatorHealer {
    private static final Logger LOGGER = Logger.getLogger(LocatorHealer.class.getName());

    private LocatorHealer() {}

    public record Candidate(String description, Locator locator) {}

    public static Locator resolve(String elementName, List<Candidate> candidates) {
        Locator[] resolved = new Locator[1];
        Allure.step("Resolve " + elementName + " locator", () -> {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate candidate = candidates.get(i);
                Locator locator = candidate.locator().first();
                if (locator.count() > 0 && locator.isVisible()) {
                    resolved[0] = locator;
                    if (i == 0) {
                        LOGGER.info("Using preferred locator for " + elementName + ": " + candidate.description());
                    } else {
                        LOGGER.warning("Self-healing fallback for " + elementName + ": " + candidate.description());
                        Allure.attachment("Healed locator", "text/plain",
                            elementName + " used " + candidate.description());
                    }
                    return;
                }
            }
            throw new AssertionError("No visible locator found for " + elementName);
        });
        return resolved[0];
    }

    public static Candidate candidate(String description, Locator locator) {
        return new Candidate(description, locator);
    }

    public static List<Candidate> candidates(Candidate... candidates) {
        return List.of(candidates);
    }
}
