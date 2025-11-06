package com.jaya.clients;

import com.jaya.config.ConfigManager;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * BaseClient - Base class for all API client classes
 * Provides common HTTP methods (GET, POST, PUT, DELETE)
 */
public abstract class BaseClient {
    
    protected RequestSpecification requestSpec;
    
    /**
     * Constructor
     * @param requestSpec RequestSpecification instance
     */
    public BaseClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }
    
    /**
     * Execute GET request
     * @param endpoint API endpoint
     * @return Response object
     */
    protected Response get(String endpoint) {
        var validatable = given()
                .spec(requestSpec)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    /**
     * Execute GET request with path parameters
     * @param endpoint API endpoint with placeholders
     * @param pathParamName Path parameter name
     * @param pathParamValue Path parameter value
     * @return Response object
     */
    protected Response getWithPathParam(String endpoint, String pathParamName, Object pathParamValue) {
        var validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    /**
     * Execute POST request
     * @param endpoint API endpoint
     * @param body Request body (POJO)
     * @return Response object
     */
    protected Response post(String endpoint, Object body) {
        var validatable = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    /**
     * Execute PUT request
     * @param endpoint API endpoint
     * @param body Request body (POJO)
     * @return Response object
     */
    protected Response put(String endpoint, Object body) {
        var validatable = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .put(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    /**
     * Execute DELETE request
     * @param endpoint API endpoint
     * @return Response object
     */
    protected Response delete(String endpoint) {
        var validatable = given()
                .spec(requestSpec)
                .when()
                .delete(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    /**
     * Execute DELETE request with path parameters
     * @param endpoint API endpoint with placeholders
     * @param pathParamName Path parameter name
     * @param pathParamValue Path parameter value
     * @return Response object
     */
    protected Response deleteWithPathParam(String endpoint, String pathParamName, Object pathParamValue) {
        var validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .when()
                .delete(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    /**
     * Execute PATCH request
     * @param endpoint API endpoint
     * @param body Request body (POJO)
     * @return Response object
     */
    protected Response patch(String endpoint, Object body) {
        var validatable = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .patch(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }

    /**
     * Apply conditional response logging: if enable.response.logging=true log entire response;
     * otherwise only log when validation fails (existing behavior).
     */
    private void applyConditionalResponseLogging(io.restassured.response.ValidatableResponse validatable) {
        if (ConfigManager.isResponseLoggingEnabled()) {
            validatable.log().all();
        } else {
            validatable.log().ifValidationFails();
        }
    }
}
