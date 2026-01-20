package com.jaya.utils;

import com.jaya.clients.AuthClient;
import com.jaya.clients.UserClient;
import com.jaya.config.ConfigManager;
import com.jaya.pojo.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestUserCleanupManager {

    private static final Logger log = LoggerFactory.getLogger(TestUserCleanupManager.class);
    private static final List<TestUser> createdUsers = new CopyOnWriteArrayList<>();
    private static final String SEPARATOR = "=".repeat(60);
    private static final String LINE = "-".repeat(60);

    private static boolean isLoggingEnabled() {
        return ConfigManager.isCleanupLoggingEnabled();
    }

    private static void logInfo(String message, Object... args) {
        if (isLoggingEnabled()) {
            log.info(message, args);
        }
    }

    private static void logWarn(String message, Object... args) {
        if (isLoggingEnabled()) {
            log.warn(message, args);
        }
    }

    public static class TestUser {
        private final Long userId;
        private final String email;
        private String password;
        private String token;

        public TestUser(Long userId, String email, String password, String token) {
            this.userId = userId;
            this.email = email;
            this.password = password;
            this.token = token;
        }

        public Long getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getToken() {
            return token;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "TestUser{userId=" + userId + ", email='" + email + "'}";
        }
    }

    @Step("Register user for cleanup: {email}")
    public static void registerUserForCleanup(Long userId, String email, String password, String token) {
        createdUsers.add(new TestUser(userId, email, password, token));
        logInfo("[CLEANUP] Registered: {} (Total: {})", email, createdUsers.size());
    }

    @Step("Register user for cleanup: {email}")
    public static void registerUserForCleanup(String email, String password) {
        createdUsers.add(new TestUser(null, email, password, null));
        logInfo("[CLEANUP] Registered: {} (Total: {})", email, createdUsers.size());
    }

    @Step("Update password for registered user: {email}")
    public static void updateUserPassword(String email, String newPassword) {
        createdUsers.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .ifPresent(user -> {
                    user.setPassword(newPassword);
                    user.setToken(null);
                    logInfo("[CLEANUP] Updated password for: {}", email);
                });
    }

    public static List<TestUser> getRegisteredUsers() {
        return Collections.unmodifiableList(new ArrayList<>(createdUsers));
    }

    public static int getRegisteredUserCount() {
        return createdUsers.size();
    }

    @Step("Cleanup all test users")
    public static void cleanupAllUsers() {
        logInfo("\n{}\nTEST USER CLEANUP STARTING\n{}", SEPARATOR, SEPARATOR);

        if (createdUsers.isEmpty()) {
            logInfo("No test users to cleanup\n{}\n", SEPARATOR);
            return;
        }

        logInfo("Total users to cleanup: {}\n{}", createdUsers.size(), LINE);

        RequestSpecification baseSpec = createBaseSpec();
        AuthClient authClient = new AuthClient(baseSpec);

        int successCount = 0;
        int failCount = 0;

        for (TestUser user : createdUsers) {
            if (deleteUser(user, authClient, baseSpec)) {
                successCount++;
                logInfo("[SUCCESS] Deleted: {}", user.getEmail());
            } else {
                failCount++;
                logWarn("[FAILED] Could not delete: {}", user.getEmail());
            }
        }

        logInfo("{}\nCLEANUP SUMMARY", LINE);
        logInfo("  Total: {}, Deleted: {}, Failed: {}\n{}\n", createdUsers.size(), successCount, failCount, SEPARATOR);

        createdUsers.clear();
    }

    public static void clearRegisteredUsers() {
        createdUsers.clear();
    }

    private static boolean deleteUser(TestUser user, AuthClient authClient, RequestSpecification baseSpec) {
        try {
            String token = user.getToken();
            Long userId = user.getUserId();

            if (token == null || token.isEmpty()) {
                Response loginResponse = authClient.signin(new LoginRequest(user.getEmail(), user.getPassword()));
                if (loginResponse.getStatusCode() != 200) {
                    log.debug("Login failed for {}. Status: {}", user.getEmail(), loginResponse.getStatusCode());
                    return false;
                }
                token = loginResponse.jsonPath().getString("jwt");
            }

            RequestSpecification authSpec = createAuthenticatedSpec(baseSpec, token);
            UserClient userClient = new UserClient(authSpec);

            if (userId == null) {
                Response profileResponse = userClient.getUserProfile();
                if (profileResponse.getStatusCode() != 200) {
                    log.debug("Could not get profile for {}. Status: {}", user.getEmail(),
                            profileResponse.getStatusCode());
                    return false;
                }
                userId = profileResponse.jsonPath().getLong("id");
            }

            Response deleteResponse = userClient.deleteUser(userId);
            return deleteResponse.getStatusCode() == 200 || deleteResponse.getStatusCode() == 204;

        } catch (Exception e) {
            log.debug("Exception deleting {}: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    private static RequestSpecification createBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    private static RequestSpecification createAuthenticatedSpec(RequestSpecification baseSpec, String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
