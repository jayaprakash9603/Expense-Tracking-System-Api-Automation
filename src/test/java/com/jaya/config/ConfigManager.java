package com.jaya.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigManager - Centralized configuration management
 * Reads properties from config.properties and supports environment-specific overrides
 */
public class ConfigManager {
    
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    
    static {
        loadProperties();
    }
    
    /**
     * Load properties from config file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE_PATH, e);
        }
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        return value;
    }
    
    /**
     * Get base URL based on environment
     * Environment can be set via -Denv=qa/dev/prod
     * @return Base URL for the specified environment
     */
    public static String getBaseUrl() {
        String env = System.getProperty("env", getProperty("environment"));
        String baseUrlKey = "base.url." + env.toLowerCase();
        String baseUrl = getProperty(baseUrlKey);
        
        if (baseUrl == null) {
            throw new RuntimeException("Base URL not found for environment: " + env);
        }
        
        return baseUrl;
    }
    
    /**
     * Get authentication username
     * @return Username
     */
    public static String getUsername() {
        return getProperty("auth.username");
    }
    
    /**
     * Get authentication password
     * @return Password
     */
    public static String getPassword() {
        return getProperty("auth.password");
    }
    
    /**
     * Get connection timeout
     * @return Connection timeout in milliseconds
     */
    public static int getConnectionTimeout() {
        return Integer.parseInt(getProperty("connection.timeout"));
    }
    
    /**
     * Get response timeout
     * @return Response timeout in milliseconds
     */
    public static int getResponseTimeout() {
        return Integer.parseInt(getProperty("response.timeout"));
    }
    
    /**
     * Check if request logging is enabled
     * @return true if enabled, false otherwise
     */
    public static boolean isRequestLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("enable.request.logging"));
    }
    
    /**
     * Check if response logging is enabled
     * @return true if enabled, false otherwise
     */
    public static boolean isResponseLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("enable.response.logging"));
    }
}
