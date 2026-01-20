package com.jaya.utils;

import com.jaya.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TokenManager - Thread-safe JWT token management for API authentication
 * Handles token caching, refresh, and expiry with support for parallel test
 * execution
 */
public final class TokenManager {

    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);

    private static volatile String cachedToken;
    private static volatile LocalDateTime tokenExpiryTime;
    private static final int TOKEN_VALIDITY_MINUTES = 30;
    private static final int TOKEN_BUFFER_MINUTES = 2;

    // Lock for thread-safe token refresh
    private static final ReentrantLock tokenLock = new ReentrantLock();

    // Private constructor to prevent instantiation
    private TokenManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get valid authentication token - thread-safe
     * Automatically refreshes if expired or about to expire
     */
    public static String getToken() {
        // Double-checked locking pattern for performance
        if (isTokenExpired()) {
            tokenLock.lock();
            try {
                // Check again after acquiring lock
                if (isTokenExpired()) {
                    refreshToken();
                }
            } finally {
                tokenLock.unlock();
            }
        }
        return cachedToken;
    }

    /**
     * Check if token is expired or will expire within buffer time
     */
    private static boolean isTokenExpired() {
        if (cachedToken == null || tokenExpiryTime == null) {
            return true;
        }
        LocalDateTime bufferTime = LocalDateTime.now().plus(TOKEN_BUFFER_MINUTES, ChronoUnit.MINUTES);
        return bufferTime.isAfter(tokenExpiryTime);
    }

    /**
     * Refresh token by authenticating with configured credentials
     */
    private static void refreshToken() {
        log.info("Refreshing authentication token...");

        String username = ConfigManager.getUsername();
        String password = ConfigManager.getPassword();

        if (username == null || password == null) {
            throw new RuntimeException("Authentication credentials not configured. " +
                    "Please set auth.username and auth.password in config.properties");
        }

        try {
            Response response = RestAssured.given()
                    .baseUri(ConfigManager.getBaseUrl())
                    .contentType("application/json")
                    .body(buildLoginPayload(username, password))
                    .when()
                    .post("/auth/signin")
                    .then()
                    .extract()
                    .response();

            if (response.getStatusCode() != 200) {
                log.error("Token refresh failed with status: {} - {}",
                        response.getStatusCode(), response.getBody().asString());
                throw new RuntimeException("Failed to authenticate. Status: " + response.getStatusCode());
            }

            cachedToken = response.jsonPath().getString("jwt");
            tokenExpiryTime = LocalDateTime.now().plus(TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES);

            if (cachedToken == null || cachedToken.isEmpty()) {
                throw new RuntimeException("JWT token not found in login response");
            }

            log.info("Token refreshed successfully. Valid until: {}", tokenExpiryTime);

        } catch (Exception e) {
            log.error("Failed to refresh authentication token", e);
            throw new RuntimeException("Failed to refresh authentication token: " + e.getMessage(), e);
        }
    }

    /**
     * Build JSON login payload
     */
    private static String buildLoginPayload(String email, String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }

    /**
     * Clear cached token - useful for testing token expiry scenarios
     */
    public static void clearToken() {
        tokenLock.lock();
        try {
            cachedToken = null;
            tokenExpiryTime = null;
            log.debug("Token cache cleared");
        } finally {
            tokenLock.unlock();
        }
    }

    /**
     * Manually set token - useful for test user tokens
     */
    public static void setToken(String token) {
        tokenLock.lock();
        try {
            cachedToken = token;
            tokenExpiryTime = LocalDateTime.now().plus(TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES);
            log.debug("Token manually set. Valid until: {}", tokenExpiryTime);
        } finally {
            tokenLock.unlock();
        }
    }

    /**
     * Set token with custom validity duration
     */
    public static void setToken(String token, int validityMinutes) {
        tokenLock.lock();
        try {
            cachedToken = token;
            tokenExpiryTime = LocalDateTime.now().plus(validityMinutes, ChronoUnit.MINUTES);
            log.debug("Token manually set with {}min validity. Valid until: {}", validityMinutes, tokenExpiryTime);
        } finally {
            tokenLock.unlock();
        }
    }

    /**
     * Check if a valid token exists
     */
    public static boolean hasValidToken() {
        return cachedToken != null && !isTokenExpired();
    }

    /**
     * Get token expiry time
     */
    public static LocalDateTime getTokenExpiryTime() {
        return tokenExpiryTime;
    }
}
