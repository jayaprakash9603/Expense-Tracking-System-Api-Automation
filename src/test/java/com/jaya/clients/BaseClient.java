package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.utils.RequestResponseLogger;
import com.jaya.utils.TestContext;
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
 * Base client providing common HTTP operations with retry logic and detailed
 * logging.
 * All API clients should extend this class to inherit consistent behavior.
 */
public abstract class BaseClient {

    private static final Logger log = LoggerFactory.getLogger(BaseClient.class);
    private static final long RETRY_DELAY_MS = 1000L;

    protected RequestSpecification requestSpec;
    private final int maxRetries;

    protected BaseClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
        this.maxRetries = ConfigManager.getRetryCount();
    }

    // ==================== GET METHODS ====================

    @Step("GET {endpoint}")
    protected Response get(String endpoint) {
        return executeWithLogging("GET", endpoint, null, () -> request().get(endpoint));
    }

    @Step("GET {endpoint} with path param {paramName}={paramValue}")
    protected Response getWithPathParam(String endpoint, String paramName, Object paramValue) {
        return executeWithLogging("GET", endpoint, null,
                () -> request().pathParam(paramName, paramValue).get(endpoint));
    }

    @Step("GET {endpoint} with path params")
    protected Response getWithPathParams(String endpoint, Map<String, Object> pathParams) {
        return executeWithLogging("GET", endpoint, null,
                () -> request().pathParams(pathParams).get(endpoint));
    }

    @Step("GET {endpoint} with query param {paramName}={paramValue}")
    protected Response getWithQueryParam(String endpoint, String paramName, Object paramValue) {
        return executeWithLogging("GET", endpoint, null,
                () -> request().queryParam(paramName, paramValue).get(endpoint));
    }

    @Step("GET {endpoint} with query params")
    protected Response getWithQueryParams(String endpoint, Map<String, Object> queryParams) {
        return executeWithLogging("GET", endpoint, null,
                () -> request().queryParams(queryParams).get(endpoint));
    }

    // ==================== POST METHODS ====================

    @Step("POST {endpoint}")
    protected Response post(String endpoint, Object body) {
        return executeWithLogging("POST", endpoint, body,
                () -> request().body(body).post(endpoint));
    }

    @Step("POST {endpoint}")
    protected Response postWithoutBody(String endpoint) {
        return executeWithLogging("POST", endpoint, null,
                () -> request().post(endpoint));
    }

    @Step("POST {endpoint} with path param {paramName}={paramValue}")
    protected Response postWithPathParam(String endpoint, String paramName, Object paramValue, Object body) {
        return executeWithLogging("POST", endpoint, body,
                () -> request().pathParam(paramName, paramValue).body(body).post(endpoint));
    }

    // ==================== PUT METHODS ====================

    @Step("PUT {endpoint}")
    protected Response put(String endpoint, Object body) {
        return executeWithLogging("PUT", endpoint, body,
                () -> request().body(body).put(endpoint));
    }

    @Step("PUT {endpoint} with path param {paramName}={paramValue}")
    protected Response putWithPathParam(String endpoint, String paramName, Object paramValue, Object body) {
        return executeWithLogging("PUT", endpoint, body,
                () -> request().pathParam(paramName, paramValue).body(body).put(endpoint));
    }

    @Step("PUT {endpoint} with query param {paramName}={paramValue}")
    protected Response putWithQueryParam(String endpoint, String paramName, Object paramValue, Object body) {
        return executeWithLogging("PUT", endpoint, body,
                () -> request().queryParam(paramName, paramValue).body(body).put(endpoint));
    }

    // ==================== DELETE METHODS ====================

    @Step("DELETE {endpoint}")
    protected Response delete(String endpoint) {
        return executeWithLogging("DELETE", endpoint, null,
                () -> request().delete(endpoint));
    }

    @Step("DELETE {endpoint} with path param {paramName}={paramValue}")
    protected Response deleteWithPathParam(String endpoint, String paramName, Object paramValue) {
        return executeWithLogging("DELETE", endpoint, null,
                () -> request().pathParam(paramName, paramValue).delete(endpoint));
    }

    // ==================== PATCH METHODS ====================

    @Step("PATCH {endpoint}")
    protected Response patch(String endpoint, Object body) {
        return executeWithLogging("PATCH", endpoint, body,
                () -> request().body(body).patch(endpoint));
    }

    @Step("PATCH {endpoint} with path param {paramName}={paramValue}")
    protected Response patchWithPathParam(String endpoint, String paramName, Object paramValue, Object body) {
        return executeWithLogging("PATCH", endpoint, body,
                () -> request().pathParam(paramName, paramValue).body(body).patch(endpoint));
    }

    // ==================== NO-RETRY METHODS (for negative tests expecting errors)
    // ====================

    /**
     * GET without retry - use for tests expecting error responses (4xx/5xx)
     */
    @Step("GET {endpoint} (no retry)")
    protected Response getNoRetry(String endpoint) {
        return executeWithoutRetry("GET", endpoint, null, () -> request().get(endpoint));
    }

    /**
     * GET with path param without retry - use for tests expecting error responses
     */
    @Step("GET {endpoint} with path param {paramName}={paramValue} (no retry)")
    protected Response getWithPathParamNoRetry(String endpoint, String paramName, Object paramValue) {
        return executeWithoutRetry("GET", endpoint, null,
                () -> request().pathParam(paramName, paramValue).get(endpoint));
    }

    /**
     * PUT with query param without retry - use for tests expecting error responses
     */
    @Step("PUT {endpoint} with query param {paramName}={paramValue} (no retry)")
    protected Response putWithQueryParamNoRetry(String endpoint, String paramName, Object paramValue, Object body) {
        return executeWithoutRetry("PUT", endpoint, body,
                () -> request().queryParam(paramName, paramValue).body(body).put(endpoint));
    }

    // ==================== UNAUTHENTICATED REQUESTS (DRY) ====================

    protected Response unauthenticatedGet(String endpoint) {
        String requestId = TestContext.registerRequest();
        log.debug("[{}] Unauthenticated GET {}", requestId, endpoint);
        return unauthenticatedRequest().get(endpoint).then().log().ifValidationFails().extract().response();
    }

    protected Response unauthenticatedPost(String endpoint, Object body) {
        String requestId = TestContext.registerRequest();
        log.debug("[{}] Unauthenticated POST {} with body", requestId, endpoint);
        return unauthenticatedRequest().body(body).post(endpoint).then().log().ifValidationFails().extract().response();
    }

    protected Response unauthenticatedPut(String endpoint, Object body) {
        String requestId = TestContext.registerRequest();
        log.debug("[{}] Unauthenticated PUT {} with body", requestId, endpoint);
        return unauthenticatedRequest().body(body).put(endpoint).then().log().ifValidationFails().extract().response();
    }

    protected Response unauthenticatedDelete(String endpoint) {
        String requestId = TestContext.registerRequest();
        log.debug("[{}] Unauthenticated DELETE {}", requestId, endpoint);
        return unauthenticatedRequest().delete(endpoint).then().log().ifValidationFails().extract().response();
    }

    protected Response unauthenticatedPutWithQueryParam(String endpoint, String paramName, Object paramValue) {
        String requestId = TestContext.registerRequest();
        log.debug("[{}] Unauthenticated PUT {} with query param {}={}", requestId, endpoint, paramName, paramValue);
        return unauthenticatedRequest().queryParam(paramName, paramValue).put(endpoint)
                .then().log().ifValidationFails().extract().response();
    }

    // ==================== UTILITIES ====================

    protected String replacePath(String endpoint, String paramName, Object paramValue) {
        return endpoint.replace("{" + paramName + "}", String.valueOf(paramValue));
    }

    public void updateRequestSpec(RequestSpecification newSpec) {
        this.requestSpec = newSpec;
    }

    // ==================== PRIVATE HELPERS ====================

    private RequestSpecification request() {
        return given().spec(requestSpec).when();
    }

    private RequestSpecification unauthenticatedRequest() {
        return given().contentType("application/json").baseUri(ConfigManager.getBaseUrl()).when();
    }

    /**
     * Executes an HTTP request with detailed logging, retry logic, and correlation
     * tracking.
     */
    private Response executeWithLogging(String method, String endpoint, Object body,
            Supplier<Response> requestSupplier) {
        String requestId = TestContext.registerRequest();
        String operation = method + " " + endpoint;
        long startTime = System.currentTimeMillis();

        // Log request details
        RequestResponseLogger.logRequest(requestId, method, endpoint, requestSpec, body);

        int attempt = 0;
        Response response = null;
        Exception lastException = null;

        while (attempt < maxRetries) {
            attempt++;
            try {
                ValidatableResponse validatable = requestSupplier.get().then();
                applyLogging(validatable);
                response = validatable.extract().response();

                long duration = System.currentTimeMillis() - startTime;

                if (response.getStatusCode() < 500) {
                    // Log successful response
                    RequestResponseLogger.logResponse(requestId, response, duration);

                    // Log summary for quick debugging
                    if (response.getStatusCode() >= 400) {
                        log.warn("[{}] {} completed with client error {} in {}ms",
                                requestId, operation, response.getStatusCode(), duration);
                    } else {
                        log.info("[{}] {} completed successfully {} in {}ms",
                                requestId, operation, response.getStatusCode(), duration);
                    }
                    return response;
                }

                // Server error - log and retry
                if (attempt < maxRetries) {
                    String errorBody = extractErrorMessage(response);
                    RequestResponseLogger.logRetry(requestId, operation, attempt, maxRetries,
                            response.getStatusCode(), errorBody);
                    sleep(RETRY_DELAY_MS * attempt);
                }
            } catch (Exception e) {
                lastException = e;
                RequestResponseLogger.logRequestFailure(requestId, operation, e);

                if (attempt < maxRetries) {
                    log.warn("[{}] Retrying after exception ({}/{})", requestId, attempt, maxRetries);
                    sleep(RETRY_DELAY_MS * attempt);
                }
            }
        }

        // Log final response if we have one
        if (response != null) {
            long duration = System.currentTimeMillis() - startTime;
            RequestResponseLogger.logResponse(requestId, response, duration);
            log.error("[{}] {} failed after {} retries with status {}",
                    requestId, operation, maxRetries, response.getStatusCode());
            return response;
        }

        throw new RuntimeException("Request failed after " + maxRetries + " attempts: " + operation, lastException);
    }

    /**
     * Executes an HTTP request WITHOUT retry logic.
     * Use this for negative tests that expect error responses (4xx/5xx).
     * Avoids unnecessary retries and delays when testing error scenarios.
     */
    private Response executeWithoutRetry(String method, String endpoint, Object body,
            Supplier<Response> requestSupplier) {
        String requestId = TestContext.registerRequest();
        String operation = method + " " + endpoint;
        long startTime = System.currentTimeMillis();

        // Log request details
        RequestResponseLogger.logRequest(requestId, method, endpoint, requestSpec, body);

        try {
            ValidatableResponse validatable = requestSupplier.get().then();
            applyLogging(validatable);
            Response response = validatable.extract().response();

            long duration = System.currentTimeMillis() - startTime;

            // Log response
            RequestResponseLogger.logResponse(requestId, response, duration);

            // Log summary based on status code
            int statusCode = response.getStatusCode();
            if (statusCode >= 500) {
                log.warn("[{}] {} completed with server error {} in {}ms (no retry)",
                        requestId, operation, statusCode, duration);
            } else if (statusCode >= 400) {
                log.warn("[{}] {} completed with client error {} in {}ms",
                        requestId, operation, statusCode, duration);
            } else {
                log.info("[{}] {} completed successfully {} in {}ms",
                        requestId, operation, statusCode, duration);
            }

            return response;
        } catch (Exception e) {
            RequestResponseLogger.logRequestFailure(requestId, operation, e);
            throw new RuntimeException("Request failed: " + operation, e);
        }
    }

    /**
     * Extracts a meaningful error message from the response body.
     */
    private String extractErrorMessage(Response response) {
        try {
            String body = response.getBody().asString();
            if (body != null && !body.isEmpty()) {
                // Try to extract message field from JSON
                if (body.contains("\"message\"")) {
                    String message = response.jsonPath().getString("message");
                    if (message != null)
                        return message;
                }
                if (body.contains("\"error\"")) {
                    String error = response.jsonPath().getString("error");
                    if (error != null)
                        return error;
                }
                // Return truncated body if no message field
                return body.length() > 100 ? body.substring(0, 100) + "..." : body;
            }
        } catch (Exception e) {
            log.trace("Could not extract error message: {}", e.getMessage());
        }
        return "Status " + response.getStatusCode();
    }

    private void applyLogging(ValidatableResponse validatable) {
        if (ConfigManager.isResponseLoggingEnabled()) {
            validatable.log().all();
        } else {
            validatable.log().ifValidationFails();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
