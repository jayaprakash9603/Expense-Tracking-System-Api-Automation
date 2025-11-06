package com.jaya.utils;

import com.jaya.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * TokenManager - Centralized token management with auto-refresh capability
 * Handles authentication token caching and automatic renewal
 */
public class TokenManager {
    
    private static String cachedToken;
    private static LocalDateTime tokenExpiryTime;
    private static final int TOKEN_VALIDITY_MINUTES = 30; // Adjust based on your API
    
    /**
     * Get valid authentication token
     * Automatically refreshes if token is expired or about to expire
     * @return Valid authentication token
     */
    public static String getToken() {
        if (isTokenExpired()) {
            refreshToken();
        }
        return cachedToken;
    }
    
    /**
     * Check if token is expired or about to expire (within 2 minutes)
     * @return true if token needs refresh, false otherwise
     */
    private static boolean isTokenExpired() {
        if (cachedToken == null || tokenExpiryTime == null) {
            return true;
        }
        
        LocalDateTime bufferTime = LocalDateTime.now().plus(2, ChronoUnit.MINUTES);
        return bufferTime.isAfter(tokenExpiryTime);
    }
    
    /**
     * Refresh authentication token
     * Makes login API call and caches new token
     */
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
    
    /**
     * Build login request payload
     * @return JSON login payload
     */
    private static String buildLoginPayload() {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", 
                ConfigManager.getUsername(), 
                ConfigManager.getPassword());
    }
    
    /**
     * Clear cached token (useful for testing logout scenarios)
     */
    public static void clearToken() {
        cachedToken = null;
        tokenExpiryTime = null;
    }
    
    /**
     * Manually set token (useful for testing with specific tokens)
     * @param token Token to set
     */
    public static void setToken(String token) {
        cachedToken = token;
        tokenExpiryTime = LocalDateTime.now().plus(TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES);
    }
}
