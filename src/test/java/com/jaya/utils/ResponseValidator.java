package com.jaya.utils;

import io.restassured.response.Response;
import org.testng.Assert;

/**
 * ResponseValidator - Reusable validation methods for API responses
 * Provides common assertion methods to validate response data
 */
public class ResponseValidator {
    
    /**
     * Validate response status code
     * @param response API response
     * @param expectedStatusCode Expected status code
     */
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
                "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
    }
    
    /**
     * Validate response contains a specific field
     * @param response API response
     * @param fieldPath JSON path to the field
     */
    public static void validateFieldExists(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' not found in response");
    }
    
    /**
     * Validate response field value
     * @param response API response
     * @param fieldPath JSON path to the field
     * @param expectedValue Expected field value
     */
    public static void validateFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        Assert.assertEquals(actualValue, expectedValue, 
                "Field value mismatch for '" + fieldPath + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
    }
    
    /**
     * Validate response time is within acceptable limit
     * @param response API response
     * @param maxTimeInMs Maximum acceptable response time in milliseconds
     */
    public static void validateResponseTime(Response response, long maxTimeInMs) {
        long actualTime = response.getTime();
        Assert.assertTrue(actualTime <= maxTimeInMs, 
                "Response time exceeded limit. Expected: <=" + maxTimeInMs + "ms, Actual: " + actualTime + "ms");
    }
    
    /**
     * Validate response body is not empty
     * @param response API response
     */
    public static void validateResponseNotEmpty(Response response) {
        String body = response.getBody().asString();
        Assert.assertFalse(body == null || body.isEmpty(), "Response body is empty");
    }
    
    /**
     * Validate content type header
     * @param response API response
     * @param expectedContentType Expected content type
     */
    public static void validateContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        Assert.assertTrue(actualContentType.contains(expectedContentType), 
                "Content type mismatch. Expected to contain: " + expectedContentType + ", Actual: " + actualContentType);
    }
    
    /**
     * Validate response contains error message
     * @param response API response
     * @param expectedMessage Expected error message or substring
     */
    public static void validateErrorMessage(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        if (actualMessage == null) {
            actualMessage = response.jsonPath().getString("error");
        }
        Assert.assertNotNull(actualMessage, "Error message not found in response");
        Assert.assertTrue(actualMessage.contains(expectedMessage), 
                "Error message mismatch. Expected to contain: '" + expectedMessage + "', Actual: '" + actualMessage + "'");
    }
    
    /**
     * Validate response is successful (2xx status code)
     * @param response API response
     */
    public static void validateSuccessResponse(Response response) {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode >= 200 && statusCode < 300, 
                "Response is not successful. Status code: " + statusCode);
    }
    
    /**
     * Validate response header exists
     * @param response API response
     * @param headerName Header name
     */
    public static void validateHeaderExists(Response response, String headerName) {
        String headerValue = response.getHeader(headerName);
        Assert.assertNotNull(headerValue, "Header '" + headerName + "' not found in response");
    }
}
