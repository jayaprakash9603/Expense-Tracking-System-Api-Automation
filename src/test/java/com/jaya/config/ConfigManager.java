package com.jaya.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager - Centralized configuration management
 * Supports property files, system properties, and environment variables
 * Priority: System Property > Environment Variable > Config File
 */
public final class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    private static final String CLASSPATH_CONFIG = "/config.properties";
    private static final String DEFAULT_ENV = "qa";
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    private static final int DEFAULT_RESPONSE_TIMEOUT = 10000;
    private static final int DEFAULT_RETRY_COUNT = 3;

    private static volatile boolean initialized = false;

    // Private constructor to prevent instantiation
    private ConfigManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static {
        loadProperties();
    }

    private static synchronized void loadProperties() {
        if (initialized && properties != null) {
            return;
        }

        properties = new Properties();

        // Try loading from file system first
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            log.info("Configuration loaded from file: {}", CONFIG_FILE_PATH);
            initialized = true;
            return;
        } catch (IOException e) {
            log.debug("Config file not found at {}, trying classpath", CONFIG_FILE_PATH);
        }

        // Try loading from classpath
        try (InputStream is = ConfigManager.class.getResourceAsStream(CLASSPATH_CONFIG)) {
            if (is != null) {
                properties.load(is);
                log.info("Configuration loaded from classpath: {}", CLASSPATH_CONFIG);
                initialized = true;
            } else {
                throw new RuntimeException(
                        "Configuration file not found: " + CONFIG_FILE_PATH + " or " + CLASSPATH_CONFIG);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    /**
     * Reload configuration from file (useful for runtime config changes)
     */
    public static synchronized void reloadConfig() {
        initialized = false;
        loadProperties();
        log.info("Configuration reloaded");
    }

    /**
     * Get property with priority: System Property > Environment Variable > Config
     * File
     */
    public static String getProperty(String key) {
        // 1. Check system property first
        String value = System.getProperty(key);
        if (isNotBlank(value)) {
            return value.trim();
        }

        // 2. Check environment variable (convert key.name to KEY_NAME)
        String envKey = key.toUpperCase().replace(".", "_").replace("-", "_");
        value = System.getenv(envKey);
        if (isNotBlank(value)) {
            return value.trim();
        }

        // 3. Fall back to properties file
        value = properties.getProperty(key);
        return value != null ? value.trim() : null;
    }

    /**
     * Get property with default value
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return isNotBlank(value) ? value : defaultValue;
    }

    /**
     * Get required property - throws exception if not found
     */
    public static String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (!isNotBlank(value)) {
            throw new RuntimeException("Required configuration property not found: " + key);
        }
        return value;
    }

    public static String getEnvironment() {
        return getProperty("environment", DEFAULT_ENV).toLowerCase();
    }

    public static String getBaseUrl() {
        String env = System.getProperty("env", getEnvironment());
        String baseUrlKey = "base.url." + env.toLowerCase();
        String baseUrl = getProperty(baseUrlKey);

        if (!isNotBlank(baseUrl)) {
            throw new RuntimeException("Base URL not found for environment: " + env +
                    ". Please configure '" + baseUrlKey + "' in config.properties or set " +
                    baseUrlKey.toUpperCase().replace(".", "_") + " environment variable");
        }

        // Remove trailing slash if present
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

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

    public static int getConnectionTimeout() {
        return getIntProperty("connection.timeout", DEFAULT_CONNECTION_TIMEOUT);
    }

    public static int getResponseTimeout() {
        return getIntProperty("response.timeout", DEFAULT_RESPONSE_TIMEOUT);
    }

    public static int getRetryCount() {
        return getIntProperty("retry.count", DEFAULT_RETRY_COUNT);
    }

    public static boolean isRequestLoggingEnabled() {
        return getBooleanProperty("enable.request.logging", true);
    }

    public static boolean isResponseLoggingEnabled() {
        return getBooleanProperty("enable.response.logging", true);
    }

    public static String getAllureResultsDirectory() {
        return getProperty("allure.results.directory", "target/allure-results");
    }

    public static boolean isCI() {
        return getBooleanProperty("ci.environment", false) ||
                System.getenv("CI") != null ||
                System.getenv("JENKINS_HOME") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                System.getenv("GITLAB_CI") != null ||
                System.getenv("AZURE_PIPELINES") != null;
    }

    public static String getDatabaseUrl() {
        return getProperty("database.url");
    }

    // Helper methods
    private static int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return isNotBlank(value) ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for property '{}', using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (!isNotBlank(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static void printConfiguration() {
        log.info("=".repeat(60));
        log.info("EXPENSE TRACKING SYSTEM - API AUTOMATION CONFIGURATION");
        log.info("=".repeat(60));
        log.info("Environment     : {}", getEnvironment());
        log.info("Base URL        : {}", getBaseUrl());
        log.info("Username        : {}", getUsername());
        log.info("Conn Timeout    : {}ms", getConnectionTimeout());
        log.info("Resp Timeout    : {}ms", getResponseTimeout());
        log.info("Retry Count     : {}", getRetryCount());
        log.info("Request Logging : {}", isRequestLoggingEnabled());
        log.info("Response Logging: {}", isResponseLoggingEnabled());
        log.info("CI Environment  : {}", isCI());
        log.info("=".repeat(60));
    }
}
