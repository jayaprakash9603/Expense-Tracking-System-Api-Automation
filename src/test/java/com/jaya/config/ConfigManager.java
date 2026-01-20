package com.jaya.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    private static final String CLASSPATH_CONFIG = "/config.properties";
    private static final String DEFAULT_ENV = "qa";
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    private static final int DEFAULT_RESPONSE_TIMEOUT = 10000;
    private static final int DEFAULT_RETRY_COUNT = 3;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            return;
        } catch (IOException e) {
        }
        try (InputStream is = ConfigManager.class.getResourceAsStream(CLASSPATH_CONFIG)) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }
    
    public static void reloadConfig() {
        loadProperties();
    }
    
    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        String envKey = key.toUpperCase().replace(".", "_");
        value = System.getenv(envKey);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    public static String getEnvironment() {
        return getProperty("environment", DEFAULT_ENV).toLowerCase();
    }
    
    public static String getBaseUrl() {
        String env = System.getProperty("env", getEnvironment());
        String baseUrlKey = "base.url." + env.toLowerCase();
        String baseUrl = getProperty(baseUrlKey);
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("Base URL not found for environment: " + env + 
                    ". Please configure '" + baseUrlKey + "' in config.properties");
        }
        return baseUrl;
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
        try {
            return Integer.parseInt(getProperty("connection.timeout", 
                    String.valueOf(DEFAULT_CONNECTION_TIMEOUT)));
        } catch (NumberFormatException e) {
            return DEFAULT_CONNECTION_TIMEOUT;
        }
    }
    
    public static int getResponseTimeout() {
        try {
            return Integer.parseInt(getProperty("response.timeout", 
                    String.valueOf(DEFAULT_RESPONSE_TIMEOUT)));
        } catch (NumberFormatException e) {
            return DEFAULT_RESPONSE_TIMEOUT;
        }
    }
    
    public static int getRetryCount() {
        try {
            return Integer.parseInt(getProperty("retry.count", 
                    String.valueOf(DEFAULT_RETRY_COUNT)));
        } catch (NumberFormatException e) {
            return DEFAULT_RETRY_COUNT;
        }
    }
    
    public static boolean isRequestLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("enable.request.logging", "true"));
    }
    
    public static boolean isResponseLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("enable.response.logging", "true"));
    }
    
    public static String getAllureResultsDirectory() {
        return getProperty("allure.results.directory", "target/allure-results");
    }
    
    public static boolean isCI() {
        return Boolean.parseBoolean(getProperty("ci.environment", "false")) ||
               System.getenv("CI") != null ||
               System.getenv("JENKINS_HOME") != null ||
               System.getenv("GITHUB_ACTIONS") != null;
    }
    
    public static String getDatabaseUrl() {
        return getProperty("database.url");
    }
    
    public static void printConfiguration() {
        System.out.println("=".repeat(60));
        System.out.println("EXPENSE TRACKING SYSTEM - API AUTOMATION CONFIGURATION");
        System.out.println("=".repeat(60));
        System.out.println("Environment     : " + getEnvironment());
        System.out.println("Base URL        : " + getBaseUrl());
        System.out.println("Username        : " + getUsername());
        System.out.println("Conn Timeout    : " + getConnectionTimeout() + "ms");
        System.out.println("Resp Timeout    : " + getResponseTimeout() + "ms");
        System.out.println("Request Logging : " + isRequestLoggingEnabled());
        System.out.println("Response Logging: " + isResponseLoggingEnabled());
        System.out.println("CI Environment  : " + isCI());
        System.out.println("=".repeat(60));
    }
}
