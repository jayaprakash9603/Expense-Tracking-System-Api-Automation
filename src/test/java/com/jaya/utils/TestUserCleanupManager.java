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

/**
 * TestUserCleanupManager - Manages cleanup of test users created during test execution.
 * Stores user information and deletes all registered users at the end of test suite.
 */
public class TestUserCleanupManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TestUserCleanupManager.class);
    
    // Thread-safe list to store created users
    private static final List<TestUser> createdUsers = new CopyOnWriteArrayList<>();
    
    /**
     * Inner class to hold user details for cleanup
     */
    public static class TestUser {
        private final Long userId;
        private final String email;
        private final String password;
        private final String token;
        
        public TestUser(Long userId, String email, String password, String token) {
            this.userId = userId;
            this.email = email;
            this.password = password;
            this.token = token;
        }
        
        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getToken() { return token; }
        
        @Override
        public String toString() {
            return "TestUser{userId=" + userId + ", email='" + email + "'}";
        }
    }
    
    /**
     * Register a user for cleanup after tests
     * @param userId User ID
     * @param email User email
     * @param password User password
     * @param token JWT token (optional, can be null)
     */
    @Step("Register user for cleanup: {email}")
    public static void registerUserForCleanup(Long userId, String email, String password, String token) {
        TestUser user = new TestUser(userId, email, password, token);
        createdUsers.add(user);
        System.out.println("[CLEANUP] Registered user for cleanup: " + email + " (Total: " + createdUsers.size() + ")");
    }
    
    /**
     * Register a user for cleanup using only email and password
     * User ID will be fetched during cleanup
     * @param email User email
     * @param password User password
     */
    @Step("Register user for cleanup: {email}")
    public static void registerUserForCleanup(String email, String password) {
        TestUser user = new TestUser(null, email, password, null);
        createdUsers.add(user);
        System.out.println("[CLEANUP] Registered user for cleanup: " + email + " (Total: " + createdUsers.size() + ")");
    }
    
    /**
     * Get list of all registered users (read-only)
     * @return Unmodifiable list of registered users
     */
    public static List<TestUser> getRegisteredUsers() {
        return Collections.unmodifiableList(new ArrayList<>(createdUsers));
    }
    
    /**
     * Get count of registered users
     * @return Number of users registered for cleanup
     */
    public static int getRegisteredUserCount() {
        return createdUsers.size();
    }
    
    /**
     * Delete all registered test users
     * Should be called in @AfterSuite
     */
    @Step("Cleanup all test users")
    public static void cleanupAllUsers() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST USER CLEANUP STARTING");
        System.out.println("=".repeat(60));
        
        if (createdUsers.isEmpty()) {
            System.out.println("No test users to cleanup");
            System.out.println("=".repeat(60) + "\n");
            return;
        }
        
        System.out.println("Total users to cleanup: " + createdUsers.size());
        System.out.println("-".repeat(60));
        
        RequestSpecification baseSpec = createBaseSpec();
        AuthClient authClient = new AuthClient(baseSpec);
        
        int successCount = 0;
        int failCount = 0;
        
        for (TestUser user : createdUsers) {
            try {
                boolean deleted = deleteUser(user, authClient, baseSpec);
                if (deleted) {
                    successCount++;
                    System.out.println("[SUCCESS] Deleted user: " + user.getEmail());
                } else {
                    failCount++;
                    System.out.println("[FAILED]  Could not delete user: " + user.getEmail());
                }
            } catch (Exception e) {
                failCount++;
                System.out.println("[ERROR]   Error deleting user " + user.getEmail() + ": " + e.getMessage());
            }
        }
        
        System.out.println("-".repeat(60));
        System.out.println("CLEANUP SUMMARY");
        System.out.println("  Total Users:    " + createdUsers.size());
        System.out.println("  Deleted:        " + successCount);
        System.out.println("  Failed:         " + failCount);
        System.out.println("=".repeat(60) + "\n");
        
        // Clear the list after cleanup
        createdUsers.clear();
    }
    
    /**
     * Delete a single user
     */
    private static boolean deleteUser(TestUser user, AuthClient authClient, RequestSpecification baseSpec) {
        try {
            // First, login to get a valid token
            String token = user.getToken();
            Long userId = user.getUserId();
            
            if (token == null || token.isEmpty()) {
                // Login to get token
                LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
                Response loginResponse = authClient.signin(loginRequest);
                
                if (loginResponse.getStatusCode() == 200) {
                    token = loginResponse.jsonPath().getString("jwt");
                } else {
                    System.out.println("          -> Login failed for " + user.getEmail() + ". Status: " + loginResponse.getStatusCode());
                    return false;
                }
            }
            
            // Get user ID if not available
            if (userId == null) {
                RequestSpecification authSpec = createAuthenticatedSpec(baseSpec, token);
                UserClient userClient = new UserClient(authSpec);
                Response profileResponse = userClient.getUserProfile();
                
                if (profileResponse.getStatusCode() == 200) {
                    userId = profileResponse.jsonPath().getLong("id");
                } else {
                    System.out.println("          -> Could not get profile for " + user.getEmail() + ". Status: " + profileResponse.getStatusCode());
                    return false;
                }
            }
            
            // Delete the user
            RequestSpecification authSpec = createAuthenticatedSpec(baseSpec, token);
            UserClient userClient = new UserClient(authSpec);
            Response deleteResponse = userClient.deleteUser(userId);
            
            if (deleteResponse.getStatusCode() == 200 || deleteResponse.getStatusCode() == 204) {
                return true;
            } else {
                System.out.println("          -> Delete API returned status: " + deleteResponse.getStatusCode());
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("          -> Exception: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create base request specification
     */
    private static RequestSpecification createBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
    
    /**
     * Create authenticated request specification
     */
    private static RequestSpecification createAuthenticatedSpec(RequestSpecification baseSpec, String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
    
    /**
     * Clear all registered users without deleting them (use with caution)
     */
    public static void clearRegisteredUsers() {
        createdUsers.clear();
        logger.info("Cleared all registered users from cleanup list");
    }
}
