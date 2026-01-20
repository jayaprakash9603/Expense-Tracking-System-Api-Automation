package com.jaya.base;

import com.jaya.config.ConfigManager;
import com.jaya.utils.TestListener;
import com.jaya.utils.TestUserCleanupManager;
import com.jaya.utils.TokenManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

/**
 * BaseTest - Foundation class for all API tests
 * Provides request specifications, authentication helpers, and test lifecycle
 * management
 */
@Listeners(TestListener.class)
public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected static RequestSpecification requestSpec;
    protected static RequestSpecification authenticatedSpec;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("Initializing test suite...");
        ConfigManager.printConfiguration();

        // Configure RestAssured global settings
        RestAssured.baseURI = ConfigManager.getBaseUrl();
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigManager.getConnectionTimeout())
                        .setParam("http.socket.timeout", ConfigManager.getResponseTimeout()));
        RestAssured.config = config;

        log.info("Test suite initialization complete");
    }

    @BeforeClass(alwaysRun = true)
    public void setup() {
        log.debug("Setting up test class: {}", getClass().getSimpleName());

        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri(ConfigManager.getBaseUrl());
        builder.setContentType(ContentType.JSON);
        builder.setAccept(ContentType.JSON);
        builder.addFilter(new AllureRestAssured());

        if (ConfigManager.isRequestLoggingEnabled()) {
            builder.log(LogDetail.ALL);
        }

        requestSpec = builder.build();
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        log.info("Starting test suite cleanup...");

        // Cleanup all test users created during test execution
        TestUserCleanupManager.cleanupAllUsers();

        // Clear token cache
        TokenManager.clearToken();

        // Reset RestAssured to defaults
        RestAssured.reset();

        log.info("Test suite cleanup complete");
    }

    /**
     * Get authenticated request specification with Bearer token
     */
    protected RequestSpecification getAuthenticatedRequest() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken())
                .build();
    }

    /**
     * Get unauthenticated request specification
     */
    protected RequestSpecification getUnauthenticatedRequest() {
        return requestSpec;
    }

    /**
     * Get request specification with custom token
     */
    protected RequestSpecification getRequestWithToken(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * Get request specification with custom header
     */
    protected RequestSpecification getRequestWithHeader(String headerName, String headerValue) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader(headerName, headerValue)
                .build();
    }

    /**
     * Clone base spec - creates independent copy to prevent mutation of shared spec
     */
    protected RequestSpecBuilder cloneBaseSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec);
    }

    /**
     * Get configured base URL
     */
    protected String getBaseUrl() {
        return ConfigManager.getBaseUrl();
    }

    /**
     * Get current environment name
     */
    protected String getEnvironment() {
        return ConfigManager.getEnvironment();
    }

    /**
     * Check if running in CI environment
     */
    protected boolean isCI() {
        return ConfigManager.isCI();
    }
}
