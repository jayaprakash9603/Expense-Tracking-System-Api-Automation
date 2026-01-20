package com.jaya.utils;

import com.jaya.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class TokenManager {

    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);
    private static final int TOKEN_VALIDITY_MINUTES = 30;
    private static final int TOKEN_BUFFER_MINUTES = 2;
    private static final ReentrantLock tokenLock = new ReentrantLock();

    private static volatile String cachedToken;
    private static volatile LocalDateTime tokenExpiryTime;

    private TokenManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String getToken() {
        if (isTokenExpired()) {
            tokenLock.lock();
            try {
                if (isTokenExpired()) {
                    refreshToken();
                }
            } finally {
                tokenLock.unlock();
            }
        }
        return cachedToken;
    }

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

    public static void setToken(String token) {
        setToken(token, TOKEN_VALIDITY_MINUTES);
    }

    public static void setToken(String token, int validityMinutes) {
        tokenLock.lock();
        try {
            cachedToken = token;
            tokenExpiryTime = LocalDateTime.now().plus(validityMinutes, ChronoUnit.MINUTES);
            log.debug("Token set. Valid until: {}", tokenExpiryTime);
        } finally {
            tokenLock.unlock();
        }
    }

    public static boolean hasValidToken() {
        return cachedToken != null && !isTokenExpired();
    }

    public static LocalDateTime getTokenExpiryTime() {
        return tokenExpiryTime;
    }

    private static boolean isTokenExpired() {
        if (cachedToken == null || tokenExpiryTime == null) {
            return true;
        }
        LocalDateTime bufferTime = LocalDateTime.now().plus(TOKEN_BUFFER_MINUTES, ChronoUnit.MINUTES);
        return bufferTime.isAfter(tokenExpiryTime);
    }

    private static void refreshToken() {
        log.info("Refreshing authentication token...");

        String username = ConfigManager.getUsername();
        String password = ConfigManager.getPassword();

        if (username == null || password == null) {
            throw new RuntimeException("Authentication credentials not configured");
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
                log.error("Token refresh failed. Status: {} - {}", response.getStatusCode(),
                        response.getBody().asString());
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

    private static String buildLoginPayload(String email, String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }
}
