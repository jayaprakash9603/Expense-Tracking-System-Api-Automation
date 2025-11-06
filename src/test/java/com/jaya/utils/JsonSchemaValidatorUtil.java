package com.jaya.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import java.io.File;

/**
 * JsonSchemaValidatorUtil - Utility for JSON schema validation
 * Validates API responses against predefined JSON schemas
 */
public class JsonSchemaValidatorUtil {
    
    private static final String SCHEMA_BASE_PATH = "src/test/resources/schemas/";
    
    /**
     * Validate response against JSON schema file
     * @param response API response
     * @param schemaFileName Schema file name (without path)
     */
    public static void validateSchema(Response response, String schemaFileName) {
        String schemaPath = SCHEMA_BASE_PATH + schemaFileName;
        File schemaFile = new File(schemaPath);
        
        if (!schemaFile.exists()) {
            throw new RuntimeException("Schema file not found: " + schemaPath);
        }
        
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaFileName));
    }
    
    /**
     * Validate response against JSON schema string
     * @param response API response
     * @param schemaString JSON schema as string
     */
    public static void validateSchemaFromString(Response response, String schemaString) {
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaString));
    }
}
