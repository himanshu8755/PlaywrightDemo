package com.playwright.support;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.playwright.config.TestConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

/** Shared Playwright lifecycle for UI tests. */
public abstract class BaseTest {
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeMethod
    public void initializeBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(TestConfig.headless())
            .setSlowMo(TestConfig.slowMo()));
        context = browser.newContext();
        page = context.newPage();
        writeAllureEnvironment();
        LOGGER.info("Browser initialized: Chromium " + browser.version() + " at " + TestConfig.baseUrl());
    }

    private void writeAllureEnvironment() {
        String resultsDirectory = System.getProperty("allure.results.directory", "target/allure-results");
        Properties environment = new Properties();
        environment.setProperty("Base URL", TestConfig.baseUrl());
        environment.setProperty("Browser", "Chromium");
        environment.setProperty("Browser Version", browser.version());
        environment.setProperty("Playwright Version", System.getProperty("playwright.version", "unknown"));
        environment.setProperty("Java Version", System.getProperty("java.version", "unknown"));
        environment.setProperty("Operating System", System.getProperty("os.name", "unknown")
            + " " + System.getProperty("os.version", ""));
       

        try {
            Path resultsPath = Path.of(resultsDirectory);
            Files.createDirectories(resultsPath);
            try (var writer = Files.newBufferedWriter(resultsPath.resolve("environment.properties"),
                StandardCharsets.UTF_8)) {
                environment.store(writer, "Playwright test execution environment");
            }
        } catch (IOException error) {
            LOGGER.warning("Could not write Allure environment.properties: " + error.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
