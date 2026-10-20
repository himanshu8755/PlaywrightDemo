package com.playwright.config;

/** Reads test settings from JVM properties, then environment variables, then demo defaults. */
public final class TestConfig {
    private TestConfig() {}

    public static String baseUrl() {
        return setting("baseUrl", "BASE_URL", "https://www.saucedemo.com");
    }

    public static String username() {
        return setting("username", "SAUCEDEMO_USERNAME", "standard_user");
    }

    public static String password() {
        return setting("password", "SAUCEDEMO_PASSWORD", "secret_sauce");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(setting("headless", "HEADLESS", "false"));
    }

    public static double slowMo() {
        return Double.parseDouble(setting("slowMo", "SLOW_MO", "500"));
    }

    private static String setting(String propertyName, String environmentName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentName);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
