package com.jaya.utils;

import com.jaya.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TokenManager {
    
    private static String cachedToken;
    private static LocalDateTime tokenExpiryTime;
    private static final int TOKEN_VALIDITY_MINUTES = 30;
    
    public static String getToken() {
        if (isTokenExpired()) {
            refreshToken();
        }
        return cachedToken;
    }
    
    private static boolean isTokenExpired() {
        if (cachedToken == null || tokenExpiryTime == null) {
            return true;
        }
        LocalDateTime bufferTime = LocalDateTime.now().plus(2, ChronoUnit.MINUTES);
        return bufferTime.isAfter(tokenExpiryTime);
    }
    
    private static void refreshToken() {
        try {
            Response response = RestAssured.given()
                    .baseUri(ConfigManager.getBaseUrl())
                    .contentType("application/json")
                    .body(buildLoginPayload())
                    .when()
                    .post("/auth/signin")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            cachedToken = response.jsonPath().getString("jwt");
            tokenExpiryTime = LocalDateTime.now().plus(TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES);
            
            if (cachedToken == null) {
                throw new RuntimeException("JWT token not found in login response");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh authentication token: " + e.getMessage(), e);
        }
    }
    
    private static String buildLoginPayload() {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", 
                ConfigManager.getUsername(), 
                ConfigManager.getPassword());
    }
    
    public static void clearToken() {
        cachedToken = null;
        tokenExpiryTime = null;
    }
    
    public static void setToken(String token) {
        cachedToken = token;
        tokenExpiryTime = LocalDateTime.now().plus(TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES);
    }
}
