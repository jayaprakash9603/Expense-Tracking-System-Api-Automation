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

@Listeners(TestListener.class)
public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static RequestSpecification requestSpec;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("Initializing test suite...");
        ConfigManager.printConfiguration();
        configureRestAssured();
        log.info("Test suite initialization complete");
    }

    @BeforeClass(alwaysRun = true)
    public void setup() {
        log.debug("Setting up test class: {}", getClass().getSimpleName());
        requestSpec = buildBaseRequestSpec();
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        log.info("Starting test suite cleanup...");
        TestUserCleanupManager.cleanupAllUsers();
        TokenManager.clearToken();
        RestAssured.reset();
        log.info("Test suite cleanup complete");
    }

    // ==================== REQUEST SPEC BUILDERS ====================

    protected RequestSpecification getAuthenticatedRequest() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken())
                .build();
    }

    protected RequestSpecification getUnauthenticatedRequest() {
        return requestSpec;
    }

    protected RequestSpecification getRequestWithToken(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    protected RequestSpecification getRequestWithHeader(String headerName, String headerValue) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader(headerName, headerValue)
                .build();
    }

    protected RequestSpecBuilder cloneBaseSpec() {
        return new RequestSpecBuilder().addRequestSpecification(requestSpec);
    }

    // ==================== CONFIG ACCESSORS ====================

    protected String getBaseUrl() {
        return ConfigManager.getBaseUrl();
    }

    protected String getEnvironment() {
        return ConfigManager.getEnvironment();
    }

    protected boolean isCI() {
        return ConfigManager.isCI();
    }

    // ==================== PRIVATE HELPERS ====================

    private void configureRestAssured() {
        RestAssured.baseURI = ConfigManager.getBaseUrl();
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigManager.getConnectionTimeout())
                        .setParam("http.socket.timeout", ConfigManager.getResponseTimeout()));
    }

    private RequestSpecification buildBaseRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured());

        if (ConfigManager.isRequestLoggingEnabled()) {
            builder.log(LogDetail.ALL);
        }
        return builder.build();
    }
}
