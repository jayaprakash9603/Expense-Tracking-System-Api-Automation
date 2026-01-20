package com.jaya.clients;

import com.jaya.config.ConfigManager;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

/**
 * BaseClient - Abstract base class for all API clients
 * Provides common HTTP methods with retry mechanism and logging
 */
public abstract class BaseClient {

    private static final Logger log = LoggerFactory.getLogger(BaseClient.class);

    protected RequestSpecification requestSpec;
    private final int maxRetries;
    private final long retryDelayMs;

    public BaseClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
        this.maxRetries = ConfigManager.getRetryCount();
        this.retryDelayMs = 1000L;
    }

    /**
     * Execute request with retry mechanism for transient failures
     */
    protected Response executeWithRetry(Supplier<Response> requestSupplier, String operationName) {
        int attempt = 0;
        Response response = null;
        Exception lastException = null;

        while (attempt < maxRetries) {
            attempt++;
            try {
                response = requestSupplier.get();

                // Don't retry on client errors (4xx) - these are expected failures
                if (response.getStatusCode() < 500) {
                    return response;
                }

                // Retry on server errors (5xx)
                if (attempt < maxRetries) {
                    log.warn("{} returned status {}, retrying ({}/{})",
                            operationName, response.getStatusCode(), attempt, maxRetries);
                    Thread.sleep(retryDelayMs * attempt); // Exponential backoff
                }
            } catch (Exception e) {
                lastException = e;
                log.error("{} failed on attempt {}/{}: {}",
                        operationName, attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelayMs * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        if (response != null) {
            return response;
        }

        throw new RuntimeException("Request failed after " + maxRetries + " attempts: " + operationName, lastException);
    }

    @Step("GET {endpoint}")
    protected Response get(String endpoint) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .when()
                    .get(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "GET " + endpoint);
    }

    @Step("GET {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response getWithPathParam(String endpoint, String pathParamName, Object pathParamValue) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .pathParam(pathParamName, pathParamValue)
                    .when()
                    .get(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "GET " + endpoint);
    }

    @Step("GET {endpoint} with path params")
    protected Response getWithPathParams(String endpoint, Map<String, Object> pathParams) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .pathParams(pathParams)
                    .when()
                    .get(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "GET " + endpoint);
    }

    @Step("GET {endpoint} with query params")
    protected Response getWithQueryParams(String endpoint, Map<String, Object> queryParams) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .queryParams(queryParams)
                    .when()
                    .get(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "GET " + endpoint);
    }

    @Step("GET {endpoint} with query param {paramName}={paramValue}")
    protected Response getWithQueryParam(String endpoint, String paramName, Object paramValue) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .queryParam(paramName, paramValue)
                    .when()
                    .get(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "GET " + endpoint);
    }

    @Step("POST {endpoint}")
    protected Response post(String endpoint, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .body(body)
                    .when()
                    .post(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "POST " + endpoint);
    }

    @Step("POST {endpoint} (no body)")
    protected Response postWithoutBody(String endpoint) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .when()
                    .post(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "POST " + endpoint);
    }

    @Step("POST {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response postWithPathParam(String endpoint, String pathParamName, Object pathParamValue, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .pathParam(pathParamName, pathParamValue)
                    .body(body)
                    .when()
                    .post(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "POST " + endpoint);
    }

    @Step("PUT {endpoint}")
    protected Response put(String endpoint, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .body(body)
                    .when()
                    .put(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "PUT " + endpoint);
    }

    @Step("PUT {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response putWithPathParam(String endpoint, String pathParamName, Object pathParamValue, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .pathParam(pathParamName, pathParamValue)
                    .body(body)
                    .when()
                    .put(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "PUT " + endpoint);
    }

    @Step("PUT {endpoint} with query param {paramName}={paramValue}")
    protected Response putWithQueryParam(String endpoint, String paramName, Object paramValue, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .queryParam(paramName, paramValue)
                    .body(body)
                    .when()
                    .put(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "PUT " + endpoint);
    }

    @Step("DELETE {endpoint}")
    protected Response delete(String endpoint) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .when()
                    .delete(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "DELETE " + endpoint);
    }

    @Step("DELETE {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response deleteWithPathParam(String endpoint, String pathParamName, Object pathParamValue) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .pathParam(pathParamName, pathParamValue)
                    .when()
                    .delete(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "DELETE " + endpoint);
    }

    @Step("PATCH {endpoint}")
    protected Response patch(String endpoint, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .body(body)
                    .when()
                    .patch(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "PATCH " + endpoint);
    }

    @Step("PATCH {endpoint} with path param {pathParamName}={pathParamValue}")
    protected Response patchWithPathParam(String endpoint, String pathParamName, Object pathParamValue, Object body) {
        return executeWithRetry(() -> {
            ValidatableResponse validatable = given()
                    .spec(requestSpec)
                    .pathParam(pathParamName, pathParamValue)
                    .body(body)
                    .when()
                    .patch(endpoint)
                    .then();
            applyConditionalResponseLogging(validatable);
            return validatable.extract().response();
        }, "PATCH " + endpoint);
    }

    private void applyConditionalResponseLogging(ValidatableResponse validatable) {
        if (ConfigManager.isResponseLoggingEnabled()) {
            validatable.log().all();
        } else {
            validatable.log().ifValidationFails();
        }
    }

    /**
     * Replace path parameter placeholder in endpoint
     */
    protected String replacePath(String endpoint, String paramName, Object paramValue) {
        return endpoint.replace("{" + paramName + "}", String.valueOf(paramValue));
    }

    /**
     * Update request specification (useful for changing auth tokens)
     */
    public void updateRequestSpec(RequestSpecification newSpec) {
        this.requestSpec = newSpec;
    }
}
