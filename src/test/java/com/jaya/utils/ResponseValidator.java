package com.jaya.utils;

import com.jaya.constants.HttpStatus;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.function.Consumer;

/**
 * Comprehensive response validation utility with detailed logging for
 * debugging.
 * All validation methods log the validation being performed and its result.
 */
public final class ResponseValidator {

    private static final Logger log = LoggerFactory.getLogger(ResponseValidator.class);
    private static final String[] ERROR_MESSAGE_FIELDS = { "message", "error", "errorMessage", "detail",
            "errors[0].message" };
    private static final int MAX_BODY_LENGTH = 500;

    private ResponseValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== STATUS CODE VALIDATIONS ====================

    @Step("Validate status code is {expectedStatusCode}")
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actual = response.getStatusCode();
        logValidation("Status Code", String.valueOf(expectedStatusCode), String.valueOf(actual),
                actual == expectedStatusCode);
        Assert.assertEquals(actual, expectedStatusCode,
                String.format("Status code mismatch. Expected: %d, Actual: %d. Response: %s",
                        expectedStatusCode, actual, truncateBody(response)));
    }

    @Step("Validate response is successful (2xx)")
    public static void validateSuccessResponse(Response response) {
        int statusCode = response.getStatusCode();
        boolean passed = HttpStatus.isSuccess(statusCode);
        logValidation("Success Response (2xx)", "2xx", String.valueOf(statusCode), passed);
        Assert.assertTrue(passed,
                String.format("Response is not successful. Status code: %d. Response: %s",
                        statusCode, truncateBody(response)));
    }

    @Step("Validate response is client error (4xx)")
    public static void validateClientError(Response response) {
        int statusCode = response.getStatusCode();
        boolean passed = HttpStatus.isClientError(statusCode);
        logValidation("Client Error (4xx)", "4xx", String.valueOf(statusCode), passed);
        Assert.assertTrue(passed,
                String.format("Response is not a client error. Status code: %d", statusCode));
    }

    @Step("Validate response is server error (5xx)")
    public static void validateServerError(Response response) {
        int statusCode = response.getStatusCode();
        boolean passed = HttpStatus.isServerError(statusCode);
        logValidation("Server Error (5xx)", "5xx", String.valueOf(statusCode), passed);
        Assert.assertTrue(passed,
                String.format("Response is not a server error. Status code: %d", statusCode));
    }

    @Step("Validate status code is one of expected values")
    public static void validateStatusCodeIn(Response response, int... expectedStatusCodes) {
        int actual = response.getStatusCode();
        boolean found = false;
        StringBuilder expectedStr = new StringBuilder();

        for (int code : expectedStatusCodes) {
            if (actual == code) {
                found = true;
                break;
            }
            expectedStr.append(code).append(", ");
        }

        String expectedValues = expectedStr.toString().replaceAll(", $", "");
        logValidation("Status Code In", "[" + expectedValues + "]", String.valueOf(actual), found);

        Assert.assertTrue(found,
                String.format("Status code %d not in expected values: [%s]", actual, expectedValues));
    }

    // ==================== FIELD VALIDATIONS ====================

    @Step("Validate field exists: {fieldPath}")
    public static void validateFieldExists(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        boolean passed = value != null;
        logValidation("Field Exists [" + fieldPath + "]", "not null", value == null ? "null" : "exists", passed);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' not found in response");
    }

    @Step("Validate field does not exist: {fieldPath}")
    public static void validateFieldNotExists(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        boolean passed = value == null;
        logValidation("Field Not Exists [" + fieldPath + "]", "null", value == null ? "null" : "exists", passed);
        Assert.assertNull(value, "Field '" + fieldPath + "' should not exist in response");
    }

    @Step("Validate field {fieldPath} equals {expectedValue}")
    public static void validateFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actual = response.jsonPath().get(fieldPath);
        boolean passed = (expectedValue == null && actual == null) ||
                (expectedValue != null && expectedValue.equals(actual));
        logValidation("Field Value [" + fieldPath + "]",
                String.valueOf(expectedValue), String.valueOf(actual), passed);
        Assert.assertEquals(actual, expectedValue,
                "Field value mismatch for '" + fieldPath + "'. Expected: " + expectedValue + ", Actual: " + actual);
    }

    @Step("Validate field {fieldPath} is not null")
    public static void validateFieldNotNull(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        boolean passed = value != null;
        logValidation("Field Not Null [" + fieldPath + "]", "not null", value == null ? "null" : "value present",
                passed);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
    }

    @Step("Validate field {fieldPath} contains '{expectedSubstring}'")
    public static void validateFieldContains(Response response, String fieldPath, String expectedSubstring) {
        String actual = response.jsonPath().getString(fieldPath);
        boolean passed = actual != null && actual.contains(expectedSubstring);
        logValidation("Field Contains [" + fieldPath + "]",
                "contains '" + expectedSubstring + "'", truncateString(actual, 50), passed);
        Assert.assertNotNull(actual, "Field '" + fieldPath + "' not found in response");
        Assert.assertTrue(actual.contains(expectedSubstring),
                "Field '" + fieldPath + "' does not contain '" + expectedSubstring + "'. Actual: " + actual);
    }

    @Step("Validate field {fieldPath} matches pattern '{regexPattern}'")
    public static void validateFieldMatchesPattern(Response response, String fieldPath, String regexPattern) {
        String actual = response.jsonPath().getString(fieldPath);
        boolean passed = actual != null && actual.matches(regexPattern);
        logValidation("Field Pattern [" + fieldPath + "]",
                "matches /" + regexPattern + "/", truncateString(actual, 50), passed);
        Assert.assertNotNull(actual, "Field '" + fieldPath + "' not found in response");
        Assert.assertTrue(actual.matches(regexPattern),
                "Field '" + fieldPath + "' does not match pattern '" + regexPattern + "'. Actual: " + actual);
    }

    // ==================== LIST VALIDATIONS ====================

    @Step("Validate list {listPath} is not empty")
    public static void validateListNotEmpty(Response response, String listPath) {
        List<?> list = response.jsonPath().getList(listPath);
        boolean passed = list != null && !list.isEmpty();
        logValidation("List Not Empty [" + listPath + "]", "non-empty list",
                list == null ? "null" : "size=" + list.size(), passed);
        Assert.assertNotNull(list, "List '" + listPath + "' not found in response");
        Assert.assertFalse(list.isEmpty(), "List '" + listPath + "' should not be empty");
    }

    @Step("Validate list {listPath} has size {expectedSize}")
    public static void validateListSize(Response response, String listPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(listPath);
        int actualSize = list != null ? list.size() : 0;
        boolean passed = actualSize == expectedSize;
        logValidation("List Size [" + listPath + "]", String.valueOf(expectedSize), String.valueOf(actualSize), passed);
        Assert.assertNotNull(list, "List '" + listPath + "' not found in response");
        Assert.assertEquals(actualSize, expectedSize,
                "List size mismatch for '" + listPath + "'. Expected: " + expectedSize + ", Actual: " + actualSize);
    }

    @Step("Validate list {listPath} has at least {minSize} items")
    public static void validateListMinSize(Response response, String listPath, int minSize) {
        List<?> list = response.jsonPath().getList(listPath);
        int actualSize = list != null ? list.size() : 0;
        boolean passed = actualSize >= minSize;
        logValidation("List Min Size [" + listPath + "]", ">=" + minSize, String.valueOf(actualSize), passed);
        Assert.assertNotNull(list, "List '" + listPath + "' not found in response");
        Assert.assertTrue(actualSize >= minSize,
                "List '" + listPath + "' has " + actualSize + " items, expected at least " + minSize);
    }

    // ==================== RESPONSE BODY VALIDATIONS ====================

    @Step("Validate response body is not empty")
    public static void validateResponseNotEmpty(Response response) {
        String body = response.getBody().asString();
        boolean passed = body != null && !body.isEmpty();
        logValidation("Response Not Empty", "non-empty",
                body == null ? "null" : "length=" + body.length(), passed);
        Assert.assertFalse(body == null || body.isEmpty(), "Response body is empty");
    }

    @Step("Validate response contains text: '{expectedText}'")
    public static void validateResponseContains(Response response, String expectedText) {
        String body = response.getBody().asString();
        boolean passed = body != null && body.contains(expectedText);
        logValidation("Response Contains", "contains '" + truncateString(expectedText, 30) + "'",
                passed ? "found" : "not found", passed);
        Assert.assertTrue(passed, "Response body does not contain '" + expectedText + "'");
    }

    // ==================== HEADER VALIDATIONS ====================

    @Step("Validate header exists: {headerName}")
    public static void validateHeaderExists(Response response, String headerName) {
        String headerValue = response.getHeader(headerName);
        boolean passed = headerValue != null;
        logValidation("Header Exists [" + headerName + "]", "not null",
                headerValue == null ? "null" : "exists", passed);
        Assert.assertNotNull(headerValue, "Header '" + headerName + "' not found in response");
    }

    @Step("Validate header {headerName} equals {expectedValue}")
    public static void validateHeaderValue(Response response, String headerName, String expectedValue) {
        String actual = response.getHeader(headerName);
        boolean passed = (expectedValue == null && actual == null) ||
                (expectedValue != null && expectedValue.equals(actual));
        logValidation("Header Value [" + headerName + "]", expectedValue, actual, passed);
        Assert.assertEquals(actual, expectedValue,
                "Header value mismatch for '" + headerName + "'. Expected: " + expectedValue + ", Actual: " + actual);
    }

    @Step("Validate content type contains: {expectedContentType}")
    public static void validateContentType(Response response, String expectedContentType) {
        String actual = response.getContentType();
        boolean passed = actual != null && actual.contains(expectedContentType);
        logValidation("Content Type", "contains '" + expectedContentType + "'", actual, passed);
        Assert.assertTrue(passed,
                String.format("Content type mismatch. Expected to contain: %s, Actual: %s",
                        expectedContentType, actual));
    }

    // ==================== ERROR & PERFORMANCE VALIDATIONS ====================

    @Step("Validate error message contains: '{expectedMessage}'")
    public static void validateErrorMessage(Response response, String expectedMessage) {
        String actual = extractErrorMessage(response);
        boolean passed = actual != null && actual.toLowerCase().contains(expectedMessage.toLowerCase());
        logValidation("Error Message", "contains '" + expectedMessage + "'",
                truncateString(actual, 50), passed);
        Assert.assertNotNull(actual, "Error message not found in response");
        Assert.assertTrue(passed,
                String.format("Error message mismatch. Expected to contain: '%s', Actual: '%s'",
                        expectedMessage, actual));
    }

    @Step("Validate response time is less than {maxTimeInMs}ms")
    public static void validateResponseTime(Response response, long maxTimeInMs) {
        long actual = response.getTime();
        boolean passed = actual <= maxTimeInMs;
        String indicator = actual < 500 ? " (fast)" : actual < 2000 ? " (normal)" : " (slow)";
        logValidation("Response Time", "<=" + maxTimeInMs + "ms", actual + "ms" + indicator, passed);
        Assert.assertTrue(passed,
                String.format("Response time exceeded limit. Expected: <=%dms, Actual: %dms",
                        maxTimeInMs, actual));
    }

    public static long getResponseTime(Response response) {
        return response.getTime();
    }

    // ==================== SOFT ASSERTION SUPPORT ====================

    public static void softAssert(Response response, Consumer<SoftResponseValidator> validations) {
        SoftResponseValidator softValidator = new SoftResponseValidator(response);
        validations.accept(softValidator);
        softValidator.assertAll();
    }

    public static class SoftResponseValidator {
        private final Response response;
        private final SoftAssert softAssert;

        public SoftResponseValidator(Response response) {
            this.response = response;
            this.softAssert = new SoftAssert();
        }

        public void validateStatusCode(int expected) {
            softAssert.assertEquals(response.getStatusCode(), expected, "Status code mismatch");
        }

        public void validateFieldExists(String fieldPath) {
            Object value = response.jsonPath().get(fieldPath);
            softAssert.assertNotNull(value, "Field '" + fieldPath + "' not found");
        }

        public void validateFieldValue(String fieldPath, Object expected) {
            Object actual = response.jsonPath().get(fieldPath);
            softAssert.assertEquals(actual, expected, "Field value mismatch for '" + fieldPath + "'");
        }

        public void validateFieldContains(String fieldPath, String expected) {
            String actual = response.jsonPath().getString(fieldPath);
            softAssert.assertTrue(actual != null && actual.contains(expected),
                    "Field '" + fieldPath + "' should contain '" + expected + "'");
        }

        public void assertAll() {
            softAssert.assertAll();
        }
    }

    // ==================== PRIVATE HELPERS ====================

    private static String extractErrorMessage(Response response) {
        for (String field : ERROR_MESSAGE_FIELDS) {
            try {
                String value = response.jsonPath().getString(field);
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private static String truncateBody(Response response) {
        String body = response.getBody().asString();
        if (body == null)
            return "null";
        return body.length() > MAX_BODY_LENGTH ? body.substring(0, MAX_BODY_LENGTH) + "..." : body;
    }

    private static String truncateString(String str, int maxLength) {
        if (str == null)
            return "null";
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }

    /**
     * Logs validation result with clear pass/fail indicator.
     */
    private static void logValidation(String validation, String expected, String actual, boolean passed) {
        if (passed) {
            log.debug("  ✓ PASS: {} | Expected: {} | Actual: {}", validation, expected, actual);
        } else {
            log.error("  ✗ FAIL: {} | Expected: {} | Actual: {}", validation, expected, actual);
        }
    }
}
