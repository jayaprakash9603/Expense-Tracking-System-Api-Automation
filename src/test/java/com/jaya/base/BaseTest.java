package com.jaya.base;

import com.jaya.config.ConfigManager;
import com.jaya.utils.TokenManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

/**
 * BaseTest - Base setup for all API test classes
 * Initializes RequestSpecification with common configurations
 */
public class BaseTest {
    
    protected static RequestSpecification requestSpec;
    
    /**
     * Setup method executed once before all tests in the class
     * Initializes RequestSpecification with base URL, headers, and filters
     */
    @BeforeClass
    public void setup() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        
        // Set base URI from configuration
        builder.setBaseUri(ConfigManager.getBaseUrl());
        
        // Set common headers
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Accept", "application/json");
        
        // Add Allure reporting filter
        builder.addFilter(new AllureRestAssured());
        
        // Conditional logging based on configuration
        if (ConfigManager.isRequestLoggingEnabled()) {
            builder.log(LogDetail.ALL);
        } else {
            builder.log(LogDetail.URI);
        }
        
        // Build RequestSpecification
        requestSpec = builder.build();
    }
    
    /**
     * Get RequestSpecification with authentication token
     * @return RequestSpecification with Bearer token
     */
    protected RequestSpecification getAuthenticatedRequest() {
        return requestSpec.header("Authorization", "Bearer " + TokenManager.getToken());
    }
    
    /**
     * Get base RequestSpecification without authentication
     * Useful for public endpoints or login tests
     * @return RequestSpecification without auth header
     */
    protected RequestSpecification getUnauthenticatedRequest() {
        return requestSpec;
    }

    /**
     * Create a fresh copy of the base RequestSpecification so we can safely
     * add or override headers (like Authorization) without mutating the shared static instance.
     * This prevents duplicate Authorization headers accumulating across tests.
     * @return cloned RequestSpecification without auth header
     */
    protected RequestSpecification cloneBaseSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .build();
    }
}
