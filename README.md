# Playwright Java: first login test

This beginner project has a `LoginPage` page object and a `LoginTest` that covers successful login, invalid username, and blank-submit behavior on the public SauceDemo training site.

## Run it

You need Java 17+, Maven, and network access. Install Playwright's Chromium browser once:

```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

Then run the login test:

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
| Headless mode | `-Dheadless=true` | `HEADLESS=true` | `false` (visible browser) |
| Slow motion | `-DslowMo=250` | `SLOW_MO=250` | `500` milliseconds |

For example, use environment variables for a private test environment:

```bash
export BASE_URL="https://your-test-site.example"
export SAUCEDEMO_USERNAME="your-test-user"
export SAUCEDEMO_PASSWORD="your-test-password"
mvn test
```

Only the public training-site credentials are defaults. Keep private credentials in environment variables or a secret manager; do not commit them. `slowMo` is a Playwright launch option that pauses after browser operations so you can watch them; set it to `0` for normal-speed execution.

## Learn the code

Open `src/test/java/com/playwright/support/BaseTest.java` first to learn browser setup:

1. `@BeforeMethod` runs before each test. It creates Playwright, launches Chromium, creates a fresh browser context, and opens a page.
2. `@AfterMethod(alwaysRun = true)` closes the context, browser, and Playwright after each test, even if an assertion fails.

Then open `src/test/java/com/playwright/tests/LoginTest.java`. It extends `BaseTest`, so it inherits the `page` prepared by the setup method. The test constructs `LoginPage`; `open(...)` navigates to the app and `login(...)` fills the username and password and clicks Login. Its assertions check that the URL contains `inventory.html` and the Products heading is visible.

`LoginPage` is a **Page Object**. It stores the page and provides actions that make sense to a user. The test stays focused on what should happen, while the page object knows how to interact with the login form. `BaseTest` keeps browser lifecycle code out of the test scenarios and can be reused as the project grows.

The login actions are wrapped in named Allure steps, so the report shows username entry, password entry, and the Login click in order. Each scenario attaches a screenshot of its resulting page. The run also writes `environment.properties` into `target/allure-results`, which adds the base URL, browser and version, headless setting, Java version, and operating system to the Allure report. `java.util.logging.Logger` writes end-to-end progress messages to the test output; credential values are never logged or added to the report.

After running the suite, view the report with the Allure CLI:

```bash
allure serve target/allure-results
```

The locators use placeholders for the inputs and an accessible button role for Login. Playwright waits for elements to be actionable when you call `fill()` or `click()`, so fixed sleeps are not needed here.

## Try changing it

- Change the configured username or password and observe how the positive case responds.
- Change `BASE_URL` to point the same test at another compatible test environment.
- Add a second test for an invalid password. Reuse `LoginPage`, then assert that `errorMessage()` contains the expected message.
- Try running with a visible browser by changing `setHeadless(true)` to `setHeadless(false)`.

The SauceDemo credentials in this example are public demo credentials. Never put personal or production credentials in a learning repository.
