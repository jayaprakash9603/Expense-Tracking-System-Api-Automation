package com.jaya.utils;

import com.jaya.constants.HttpStatus;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;

public class ResponseValidator {
    
    private ResponseValidator() {}
    
    @Step("Validate status code is {expectedStatusCode}")
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
                "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
    }
    
    @Step("Validate response is successful (2xx)")
    public static void validateSuccessResponse(Response response) {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(HttpStatus.isSuccess(statusCode), 
                "Response is not successful. Status code: " + statusCode);
    }
    
    @Step("Validate response is client error (4xx)")
    public static void validateClientError(Response response) {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(HttpStatus.isClientError(statusCode), 
                "Response is not a client error. Status code: " + statusCode);
    }
    
    @Step("Validate response is server error (5xx)")
    public static void validateServerError(Response response) {
        int statusCode = response.getStatusCode();
        Assert.assertTrue(HttpStatus.isServerError(statusCode), 
                "Response is not a server error. Status code: " + statusCode);
    }
    
    @Step("Validate status code is one of expected values")
    public static void validateStatusCodeIn(Response response, int... expectedStatusCodes) {
        int actualStatusCode = response.getStatusCode();
        boolean found = false;
        StringBuilder expectedStr = new StringBuilder();
        for (int code : expectedStatusCodes) {
            if (actualStatusCode == code) {
                found = true;
                break;
            }
            expectedStr.append(code).append(", ");
        }
        Assert.assertTrue(found, 
                "Status code " + actualStatusCode + " not in expected values: [" + 
                expectedStr.toString().replaceAll(", $", "") + "]");
    }
    
    @Step("Validate field exists: {fieldPath}")
    public static void validateFieldExists(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' not found in response");
    }
    
    @Step("Validate field does not exist: {fieldPath}")
    public static void validateFieldNotExists(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        Assert.assertNull(value, "Field '" + fieldPath + "' should not exist in response");
    }
    
    @Step("Validate field {fieldPath} equals {expectedValue}")
    public static void validateFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        Assert.assertEquals(actualValue, expectedValue, 
                "Field value mismatch for '" + fieldPath + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
    }
    
    @Step("Validate field {fieldPath} is not null")
    public static void validateFieldNotNull(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
    }
    
    @Step("Validate field {fieldPath} contains '{expectedSubstring}'")
    public static void validateFieldContains(Response response, String fieldPath, String expectedSubstring) {
        String actualValue = response.jsonPath().getString(fieldPath);
        Assert.assertNotNull(actualValue, "Field '" + fieldPath + "' not found in response");
        Assert.assertTrue(actualValue.contains(expectedSubstring), 
                "Field '" + fieldPath + "' does not contain '" + expectedSubstring + "'. Actual: " + actualValue);
    }
    
    @Step("Validate field {fieldPath} matches pattern '{regexPattern}'")
    public static void validateFieldMatchesPattern(Response response, String fieldPath, String regexPattern) {
        String actualValue = response.jsonPath().getString(fieldPath);
        Assert.assertNotNull(actualValue, "Field '" + fieldPath + "' not found in response");
        Assert.assertTrue(actualValue.matches(regexPattern), 
                "Field '" + fieldPath + "' does not match pattern '" + regexPattern + "'. Actual: " + actualValue);
    }
    
    @Step("Validate list {listPath} is not empty")
    public static void validateListNotEmpty(Response response, String listPath) {
        List<?> list = response.jsonPath().getList(listPath);
        Assert.assertNotNull(list, "List '" + listPath + "' not found in response");
        Assert.assertFalse(list.isEmpty(), "List '" + listPath + "' should not be empty");
    }
    
    @Step("Validate list {listPath} has size {expectedSize}")
    public static void validateListSize(Response response, String listPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(listPath);
        Assert.assertNotNull(list, "List '" + listPath + "' not found in response");
        Assert.assertEquals(list.size(), expectedSize, 
                "List size mismatch for '" + listPath + "'. Expected: " + expectedSize + ", Actual: " + list.size());
    }
    
    @Step("Validate list {listPath} has at least {minSize} items")
    public static void validateListMinSize(Response response, String listPath, int minSize) {
        List<?> list = response.jsonPath().getList(listPath);
        Assert.assertNotNull(list, "List '" + listPath + "' not found in response");
        Assert.assertTrue(list.size() >= minSize, 
                "List '" + listPath + "' has " + list.size() + " items, expected at least " + minSize);
    }
    
    @Step("Validate response body is not empty")
    public static void validateResponseNotEmpty(Response response) {
        String body = response.getBody().asString();
        Assert.assertFalse(body == null || body.isEmpty(), "Response body is empty");
    }
    
    @Step("Validate response contains text: '{expectedText}'")
    public static void validateResponseContains(Response response, String expectedText) {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains(expectedText), 
                "Response body does not contain '" + expectedText + "'");
    }
    
    @Step("Validate header exists: {headerName}")
    public static void validateHeaderExists(Response response, String headerName) {
        String headerValue = response.getHeader(headerName);
        Assert.assertNotNull(headerValue, "Header '" + headerName + "' not found in response");
    }
    
    @Step("Validate header {headerName} equals {expectedValue}")
    public static void validateHeaderValue(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        Assert.assertEquals(actualValue, expectedValue, 
                "Header value mismatch for '" + headerName + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
    }
    
    @Step("Validate content type contains: {expectedContentType}")
    public static void validateContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        Assert.assertTrue(actualContentType != null && actualContentType.contains(expectedContentType), 
                "Content type mismatch. Expected to contain: " + expectedContentType + ", Actual: " + actualContentType);
    }
    
    @Step("Validate error message contains: '{expectedMessage}'")
    public static void validateErrorMessage(Response response, String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        if (actualMessage == null) {
            actualMessage = response.jsonPath().getString("error");
        }
        if (actualMessage == null) {
            actualMessage = response.jsonPath().getString("errorMessage");
        }
        Assert.assertNotNull(actualMessage, "Error message not found in response");
        Assert.assertTrue(actualMessage.contains(expectedMessage), 
                "Error message mismatch. Expected to contain: '" + expectedMessage + "', Actual: '" + actualMessage + "'");
    }
    
    @Step("Validate response time is less than {maxTimeInMs}ms")
    public static void validateResponseTime(Response response, long maxTimeInMs) {
        long actualTime = response.getTime();
        Assert.assertTrue(actualTime <= maxTimeInMs, 
                "Response time exceeded limit. Expected: <=" + maxTimeInMs + "ms, Actual: " + actualTime + "ms");
    }
    
    public static long getResponseTime(Response response) {
        return response.getTime();
    }
}
