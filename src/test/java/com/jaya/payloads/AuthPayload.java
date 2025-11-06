package com.jaya.payloads;

import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AuthPayload - Builder class for creating Auth request payloads
 * Provides helper methods to create test data for authentication
 */
public class AuthPayload {
    
    /**
     * Create default signup request
     * @return SignupRequest with default test data
     */
    public static SignupRequest createDefaultSignupRequest() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return new SignupRequest(
                "Test",
                "User" + uniqueId,
                "testuser" + uniqueId + "@example.com",
                "Test@123",
                "male"
        );
    }
    
    /**
     * Create signup request with custom values
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @param gender Gender
     * @return SignupRequest object
     */
    public static SignupRequest createSignupRequest(String firstName, String lastName, String email, String password, String gender) {
        return new SignupRequest(firstName, lastName, email, password, gender);
    }
    
    /**
     * Create signup request with invalid data (for negative testing)
     * @return SignupRequest with invalid data
     */
    public static SignupRequest createInvalidSignupRequest() {
        return new SignupRequest(
                "", // Empty first name
                "", // Empty last name
                "invalid-email", // Invalid email format
                "123", // Weak password
                "" // Empty gender
        );
    }
    
    /**
     * Create signup request with missing required fields
     * @return SignupRequest with null values
     */
    public static SignupRequest createSignupRequestWithMissingFields() {
        SignupRequest request = new SignupRequest();
        request.setFirstName(null);
        request.setLastName(null);
        request.setEmail(null);
        request.setPassword(null);
        return request;
    }
    
    /**
     * Create default login request
     * Uses credentials from config
     * @return LoginRequest with default credentials
     */
    public static LoginRequest createDefaultLoginRequest() {
        return new LoginRequest(
                "testuser@example.com",
                "Test@123"
        );
    }
    
    /**
     * Create login request with custom credentials
     * @param email Email
     * @param password Password
     * @return LoginRequest object
     */
    public static LoginRequest createLoginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }
    
    /**
     * Create login request with invalid credentials
     * @return LoginRequest with wrong password
     */
    public static LoginRequest createInvalidLoginRequest() {
        return new LoginRequest(
                "testuser@example.com",
                "WrongPassword123"
        );
    }
    
    /**
     * Create login request with non-existent user
     * @return LoginRequest for non-existent user
     */
    public static LoginRequest createNonExistentUserLoginRequest() {
        return new LoginRequest(
                "nonexistent@example.com",
                "Test@123"
        );
    }
    
    /**
     * Create email check payload
     * @param email Email to check
     * @return Map with email
     */
    public static Map<String, String> createEmailCheckPayload(String email) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        return payload;
    }
    
    /**
     * Create OTP send payload
     * @param email Email for OTP
     * @return Map with email
     */
    public static Map<String, String> createSendOtpPayload(String email) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        return payload;
    }
    
    /**
     * Create OTP verify payload
     * @param email Email
     * @param otp OTP code
     * @return Map with email and OTP
     */
    public static Map<String, String> createVerifyOtpPayload(String email, String otp) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("otp", otp);
        return payload;
    }
    
    /**
     * Create password reset payload
     * @param email Email
     * @param newPassword New password
     * @return Map with email and password
     */
    public static Map<String, String> createPasswordResetPayload(String email, String newPassword) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", newPassword);
        return payload;
    }
}
