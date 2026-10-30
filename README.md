# Playwright Java: login and cart tests

This beginner project uses the public SauceDemo training site to demonstrate successful login, invalid username, blank-submit validation, adding a product to the cart, and completing checkout. It also includes a deterministic Planner and a fallback locator helper inspired by the Planner/Generator/Healer diagram.

## Run it

You need Java 17+, Maven, and network access. Install Playwright's Chromium browser once:

```bash
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

Then run the suite:

```bash
mvn test
```

## Configuration

`TestConfig` keeps the site URL and credentials out of the test scenario. Settings can be supplied as JVM properties (highest priority), environment variables, or fall back to the public SauceDemo training credentials:

| Setting | JVM property | Environment variable | Demo default |
| --- | --- | --- | --- |
| Site URL | `-DbaseUrl=...` | `BASE_URL` | `https://www.saucedemo.com` |
| Username | `-Dusername=...` | `SAUCEDEMO_USERNAME` | `standard_user` |
| Password | `-Dpassword=...` | `SAUCEDEMO_PASSWORD` | `secret_sauce` |
| Headless mode | `-Dheadless=true` | `HEADLESS=true` | `false` (visible, maximized browser) |
| Slow motion | `-DslowMo=250` | `SLOW_MO=250` | `500` milliseconds |

For example, use environment variables for a private test environment:

```bash
export BASE_URL="https://your-test-site.example"
export SAUCEDEMO_USERNAME="your-test-user"
export SAUCEDEMO_PASSWORD="your-test-password"
mvn test
```

Only the public training-site credentials are defaults. Keep private credentials in environment variables or a secret manager; do not commit them. `slowMo` is a Playwright launch option that pauses after browser operations so you can watch them; set it to `0` for normal-speed execution.

Local runs default to a visible, maximized Chromium window. Set `HEADLESS=true` (or `-Dheadless=true`) to run without a visible browser. The GitHub Actions workflow uses headless mode and installs Chromium before running the Maven test suite.

## Cross-browser and mobile runs

The regular `mvn test` command remains a Chromium desktop run. To run the UI flows in parallel across Chromium, Firefox, WebKit, and a Chromium mobile viewport, install all three Playwright browsers and select the expanded TestNG suite:

```bash
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium firefox webkit"
HEADLESS=true SLOW_MO=0 mvn test -Dtestng.suite=testng-cross-browser.xml
```

`testng-cross-browser.xml` runs the desktop browser groups and a Chromium run configured at **390 × 844** with mobile and touch emulation. TestNG runs those four groups concurrently. The mobile group also checks the inventory, menu, cart, and checkout for horizontal overflow and confirms the navigation menu and checkout fields are visible. This is browser emulation at a mobile viewport; it does not replace checks on physical phones.

The browser and viewport appear as per-test parameters in Allure. To add another configuration, add a TestNG `<test>` entry with `browser` (`chromium`, `firefox`, or `webkit`) and `viewport` (`desktop` or `mobile`) parameters. Mobile emulation is currently configured for Chromium.

## Learn the code

Open `src/test/java/com/playwright/support/BaseTest.java` first to learn browser setup:

1. `@BeforeMethod` runs before each test. It creates Playwright, launches Chromium, creates a fresh browser context, and opens a page.
2. `@AfterMethod(alwaysRun = true)` closes the context, browser, and Playwright after each test, even if an assertion fails.

Then open `src/test/java/com/playwright/tests/LoginTest.java`. It extends `BaseTest`, so it inherits the `page` prepared by setup. `LoginPage` navigates to the app, fills the username and password, and clicks Login. The assertions check that login reached the inventory.

`AddToCartTest` reuses the login page object, then uses `InventoryPage` to choose the Sauce Labs Backpack and `CartPage` to verify it appears in the cart. This shows how each page object models the actions and checks available on one page.

`CheckoutTest` covers the full journey: login, add the backpack, verify the cart, enter synthetic customer details, review the order total, and verify the confirmation page. The current demo site shows a `$32.39` total for the `$29.99` backpack including tax. The checkout test uses placeholder customer details and only the site's public demo account.

`TestPlanner` writes a structured plan for the four flows already covered by the project; `AgentPlanTest` attaches that plan to Allure before the UI suites run. This starter planner uses a clear, rule-based catalog rather than an AI model. `LocatorHealer` tries preferred accessible locators first, then known fallbacks, and records when it needs a fallback in the logs and Allure report. It does not rewrite source code.

`LoginPage` is a **Page Object**. It stores the page and provides actions that make sense to a user. The test stays focused on what should happen, while the page object knows how to interact with the login form. `BaseTest` keeps browser lifecycle code out of the test scenarios and can be reused as the project grows.

The login actions and cart actions are wrapped in named Allure steps. Each UI scenario attaches a screenshot of its resulting page. The run also writes `environment.properties` into `target/allure-results`, which adds the base URL, browser and version, headless setting, Java version, and operating system to the Allure report. `java.util.logging.Logger` writes end-to-end progress messages to the test output; credential values are never logged or added to the report.

After running the suite, view the report with the Allure CLI:

```bash
allure serve target/allure-results
```

The locators use placeholders for the inputs and an accessible button role for Login. Playwright waits for elements to be actionable when you call `fill()` or `click()`, so fixed sleeps are not needed here.

## Try changing it

- Change the configured username or password and observe how the positive case responds.
- Change `BASE_URL` to point the same test at another compatible test environment.
- Change the selected product in `AddToCartTest` and assert that the same product is visible in the cart.
- Set `SLOW_MO=0` to watch the suite run at normal speed.

The SauceDemo credentials in this example are public demo credentials. Never put personal or production credentials in a learning repository.
