package com.jaya.clients;

import com.jaya.config.ConfigManager;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {
    
    protected RequestSpecification requestSpec;
    
    public BaseClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }
    
    @Step("GET {endpoint}")
    protected Response get(String endpoint) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("GET {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response getWithPathParam(String endpoint, String pathParamName, Object pathParamValue) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("GET {endpoint} with path params")
    protected Response getWithPathParams(String endpoint, Map<String, Object> pathParams) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .pathParams(pathParams)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("GET {endpoint} with query params")
    protected Response getWithQueryParams(String endpoint, Map<String, Object> queryParams) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("GET {endpoint} with query param {paramName}={paramValue}")
    protected Response getWithQueryParam(String endpoint, String paramName, Object paramValue) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .queryParam(paramName, paramValue)
                .when()
                .get(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("POST {endpoint}")
    protected Response post(String endpoint, Object body) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("POST {endpoint} (no body)")
    protected Response postWithoutBody(String endpoint) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .when()
                .post(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("POST {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response postWithPathParam(String endpoint, String pathParamName, Object pathParamValue, Object body) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .body(body)
                .when()
                .post(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("PUT {endpoint}")
    protected Response put(String endpoint, Object body) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .put(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("PUT {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response putWithPathParam(String endpoint, String pathParamName, Object pathParamValue, Object body) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .body(body)
                .when()
                .put(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("DELETE {endpoint}")
    protected Response delete(String endpoint) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .when()
                .delete(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("DELETE {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response deleteWithPathParam(String endpoint, String pathParamName, Object pathParamValue) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .when()
                .delete(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("PATCH {endpoint}")
    protected Response patch(String endpoint, Object body) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .body(body)
                .when()
                .patch(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    @Step("PATCH {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response patchWithPathParam(String endpoint, String pathParamName, Object pathParamValue, Object body) {
        ValidatableResponse validatable = given()
                .spec(requestSpec)
                .pathParam(pathParamName, pathParamValue)
                .body(body)
                .when()
                .patch(endpoint)
                .then();
        applyConditionalResponseLogging(validatable);
        return validatable.extract().response();
    }
    
    private void applyConditionalResponseLogging(ValidatableResponse validatable) {
        if (ConfigManager.isResponseLoggingEnabled()) {
            validatable.log().all();
        } else {
            validatable.log().ifValidationFails();
        }
    }
    
    protected String replacePath(String endpoint, String paramName, Object paramValue) {
        return endpoint.replace("{" + paramName + "}", String.valueOf(paramValue));
    }
}
