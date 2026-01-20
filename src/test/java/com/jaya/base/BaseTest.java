package com.jaya.base;

import com.jaya.config.ConfigManager;
import com.jaya.utils.TokenManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

public class BaseTest {
    
    protected static RequestSpecification requestSpec;
    protected static RequestSpecification authenticatedSpec;
    
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        ConfigManager.printConfiguration();
        RestAssured.baseURI = ConfigManager.getBaseUrl();
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ConfigManager.getConnectionTimeout())
                        .setParam("http.socket.timeout", ConfigManager.getResponseTimeout()));
        RestAssured.config = config;
    }
    
    @BeforeClass(alwaysRun = true)
    public void setup() {
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
        TokenManager.clearToken();
        RestAssured.reset();
    }
    
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
    
    protected RequestSpecification cloneBaseSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .build();
    }
    
    protected String getBaseUrl() {
        return ConfigManager.getBaseUrl();
    }
    
    protected String getEnvironment() {
        return ConfigManager.getEnvironment();
    }
}
