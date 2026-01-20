package com.jaya.utils;

import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonSchemaValidatorUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidatorUtil.class);

    // Schema file constants
    public static final String USER_SCHEMA = "user-schema.json";
    public static final String USER_LIST_SCHEMA = "user-list-schema.json";
    public static final String ROLE_SCHEMA = "role-schema.json";
    public static final String ROLE_LIST_SCHEMA = "role-list-schema.json";
    public static final String AUTH_RESPONSE_SCHEMA = "auth-response-schema.json";
    public static final String EXPENSE_SCHEMA = "expense-schema.json";

    private JsonSchemaValidatorUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    @Step("Validate response against schema: {schemaFileName}")
    public static void validateSchema(Response response, String schemaFileName) {
        log.debug("Validating response against schema: {}", schemaFileName);
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaFileName));
        log.debug("Schema validation passed for: {}", schemaFileName);
    }

    @Step("Validate user response schema")
    public static void validateUserSchema(Response response) {
        validateSchema(response, USER_SCHEMA);
    }

    @Step("Validate user list response schema")
    public static void validateUserListSchema(Response response) {
        validateSchema(response, USER_LIST_SCHEMA);
    }

    @Step("Validate role response schema")
    public static void validateRoleSchema(Response response) {
        validateSchema(response, ROLE_SCHEMA);
    }

    @Step("Validate role list response schema")
    public static void validateRoleListSchema(Response response) {
        validateSchema(response, ROLE_LIST_SCHEMA);
    }

    @Step("Validate auth response schema")
    public static void validateAuthResponseSchema(Response response) {
        validateSchema(response, AUTH_RESPONSE_SCHEMA);
    }

    @Step("Validate expense response schema")
    public static void validateExpenseSchema(Response response) {
        validateSchema(response, EXPENSE_SCHEMA);
    }

    @Step("Validate response against inline schema")
    public static void validateSchemaFromString(Response response, String schemaString) {
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaString));
    }
}
