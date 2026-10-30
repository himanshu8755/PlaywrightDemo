package com.playwright.support;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ViewportSize;
import com.playwright.config.TestConfig;
import io.qameta.allure.Allure;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** Shared Playwright lifecycle for UI tests. */
public abstract class BaseTest {
    private static final Logger ROOT_LOGGER = Logger.getLogger("");
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    static {
        configureFileLogging();
    }

    private static void configureFileLogging() {
        try {
            Path logDirectory = Path.of("target", "test-logs");
            Files.createDirectories(logDirectory);
            FileHandler fileHandler = new FileHandler(logDirectory.resolve("tests.log").toString(), false);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            ROOT_LOGGER.addHandler(fileHandler);
            ROOT_LOGGER.setLevel(Level.INFO);
        } catch (IOException error) {
            throw new ExceptionInInitializerError(error);
        }
    }
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    private String browserName;
    private String viewportName;

    @BeforeMethod
    @Parameters({"browser", "viewport"})
    public void initializeBrowser(@Optional("chromium") String browserName,
                                  @Optional("desktop") String viewportName) {
        this.browserName = browserName.toLowerCase();
        this.viewportName = viewportName.toLowerCase();
        playwright = Playwright.create();
        boolean headless = TestConfig.headless();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(headless)
            .setSlowMo(TestConfig.slowMo());
        if (!headless && this.browserName.equals("chromium")) {
            launchOptions.setArgs(List.of("--start-maximized"));
        }

        browser = switch (this.browserName) {
            case "chromium", "chrome" -> playwright.chromium().launch(launchOptions);
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit", "safari" -> playwright.webkit().launch(launchOptions);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserName
                + ". Choose chromium, firefox, or webkit.");
        };

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        if (this.viewportName.equals("mobile")) {
            contextOptions.setViewportSize(new ViewportSize(390, 844))
                .setIsMobile(true)
                .setHasTouch(true)
                .setDeviceScaleFactor(3.0);
        } else if (!this.viewportName.equals("desktop")) {
            throw new IllegalArgumentException("Unsupported viewport: " + viewportName
                + ". Choose desktop or mobile.");
        } else if (!headless && this.browserName.equals("chromium")) {
            contextOptions.setViewportSize((ViewportSize) null);
        }
        context = browser.newContext(contextOptions);
        page = context.newPage();
        Allure.parameter("Browser", this.browserName);
        Allure.parameter("Viewport", this.viewportName);
        writeAllureEnvironment(this.browserName, this.viewportName, browser.version());
        LOGGER.info("Browser initialized: " + this.browserName + " " + browser.version()
            + " with " + this.viewportName + " viewport at " + TestConfig.baseUrl());
    }

    private static synchronized void writeAllureEnvironment(String browserName, String viewportName,
                                                            String browserVersion) {
        String resultsDirectory = System.getProperty("allure.results.directory", "target/allure-results");
        Properties environment = new Properties();
        environment.setProperty("Base URL", TestConfig.baseUrl());
        environment.setProperty("Browser", browserName);
        environment.setProperty("Browser Version", browserVersion);
        environment.setProperty("Viewport", viewportName);
        environment.setProperty("Slow Motion (ms)", String.valueOf(TestConfig.slowMo()));
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
