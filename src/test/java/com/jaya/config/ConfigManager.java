package com.jaya.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    private static final String CLASSPATH_CONFIG = "/config.properties";

    private static Properties properties;
    private static volatile boolean initialized = false;

    private ConfigManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        loadProperties();
    }

    // ==================== PROPERTY GETTERS ====================

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (isNotBlank(value))
            return value.trim();

        String envKey = key.toUpperCase().replace(".", "_").replace("-", "_");
        value = System.getenv(envKey);
        if (isNotBlank(value))
            return value.trim();

        value = properties.getProperty(key);
        return value != null ? value.trim() : null;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return isNotBlank(value) ? value : defaultValue;
    }

    public static String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (!isNotBlank(value)) {
            throw new RuntimeException("Required configuration property not found: " + key);
        }
        return value;
    }

    // ==================== ENVIRONMENT CONFIG ====================

    public static String getEnvironment() {
        return getProperty("environment", "qa").toLowerCase();
    }

    public static String getBaseUrl() {
        String env = System.getProperty("env", getEnvironment());
        String baseUrlKey = "base.url." + env.toLowerCase();
        String baseUrl = getProperty(baseUrlKey);

        if (!isNotBlank(baseUrl)) {
            throw new RuntimeException("Base URL not found for environment: " + env);
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public static boolean isCI() {
        return getBooleanProperty("ci.environment", false)
                || System.getenv("CI") != null
                || System.getenv("JENKINS_HOME") != null
                || System.getenv("GITHUB_ACTIONS") != null
                || System.getenv("GITLAB_CI") != null;
    }

    // ==================== AUTH CONFIG ====================

    public static String getUsername() {
        return getProperty("auth.username");
    }

    public static String getPassword() {
        return getProperty("auth.password");
    }

    public static String getAdminUsername() {
        return getProperty("auth.admin.username", getUsername());
    }

    public static String getAdminPassword() {
        return getProperty("auth.admin.password", getPassword());
    }

    // ==================== TIMEOUT CONFIG ====================

    public static int getConnectionTimeout() {
        return getIntProperty("connection.timeout", 5000);
    }

    public static int getResponseTimeout() {
        return getIntProperty("response.timeout", 10000);
    }

    public static int getRetryCount() {
        return getIntProperty("retry.count", 3);
    }

    // ==================== LOGGING CONFIG ====================

    public static boolean isRequestLoggingEnabled() {
        return getBooleanProperty("enable.request.logging", true);
    }

    public static boolean isResponseLoggingEnabled() {
        return getBooleanProperty("enable.response.logging", true);
    }

    public static boolean isCleanupLoggingEnabled() {
        return getBooleanProperty("enable.cleanup.logging", true);
    }

    public static String getAllureResultsDirectory() {
        return getProperty("allure.results.directory", "target/allure-results");
    }

    public static String getDatabaseUrl() {
        return getProperty("database.url");
    }

    // ==================== UTILITY METHODS ====================

    public static synchronized void reloadConfig() {
        initialized = false;
        loadProperties();
        log.info("Configuration reloaded");
    }

    public static void printConfiguration() {
        log.info("=".repeat(60));
        log.info("API AUTOMATION CONFIGURATION");
        log.info("=".repeat(60));
        log.info("Environment     : {}", getEnvironment());
        log.info("Base URL        : {}", getBaseUrl());
        log.info("Username        : {}", getUsername());
        log.info("Conn Timeout    : {}ms", getConnectionTimeout());
        log.info("Resp Timeout    : {}ms", getResponseTimeout());
        log.info("Retry Count     : {}", getRetryCount());
        log.info("CI Environment  : {}", isCI());
        log.info("=".repeat(60));
    }

    // ==================== PRIVATE HELPERS ====================

    private static synchronized void loadProperties() {
        if (initialized && properties != null)
            return;

        properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            log.info("Configuration loaded from: {}", CONFIG_FILE_PATH);
            initialized = true;
            return;
        } catch (IOException e) {
            log.debug("Config not found at {}, trying classpath", CONFIG_FILE_PATH);
        }

        try (InputStream is = ConfigManager.class.getResourceAsStream(CLASSPATH_CONFIG)) {
            if (is != null) {
                properties.load(is);
                log.info("Configuration loaded from classpath: {}", CLASSPATH_CONFIG);
                initialized = true;
            } else {
                throw new RuntimeException("Configuration file not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    private static int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return isNotBlank(value) ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid integer for '{}', using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return isNotBlank(value) ? Boolean.parseBoolean(value.trim()) : defaultValue;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
