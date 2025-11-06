package com.jaya.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class SimpleApiTest {
    
    @Test
    public void testSimpleSignup() {
        RestAssured.baseURI = "http://localhost:6001";
        
        Map<String, String> signupPayload = new HashMap<>();
        signupPayload.put("firstName", "Test");
        signupPayload.put("lastName", "User");
        signupPayload.put("email", "test" + System.currentTimeMillis() + "@example.com");
        signupPayload.put("password", "Test@123");
        signupPayload.put("gender", "male");
        
        Response response = given()
                .contentType(ContentType.JSON)
                .body(signupPayload)
                .when()
                .post("/auth/signup");
        
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }
}
