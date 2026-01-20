package com.jaya.payloads;

import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthPayload {
    
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
    
    public static SignupRequest createSignupRequest(String firstName, String lastName, String email, String password, String gender) {
        return new SignupRequest(firstName, lastName, email, password, gender);
    }
    
    public static SignupRequest createInvalidSignupRequest() {
        return new SignupRequest(
                "",
                "",
                "invalid-email",
                "123",
                ""
        );
    }
    
    public static SignupRequest createSignupRequestWithMissingFields() {
        SignupRequest request = new SignupRequest();
        request.setFirstName(null);
        request.setLastName(null);
        request.setEmail(null);
        request.setPassword(null);
        return request;
    }
    
    public static LoginRequest createDefaultLoginRequest() {
        return new LoginRequest(
                "testuser@example.com",
                "Test@123"
        );
    }
    
    public static LoginRequest createLoginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }
    
    public static LoginRequest createInvalidLoginRequest() {
        return new LoginRequest(
                "testuser@example.com",
                "WrongPassword123"
        );
    }
    
    public static LoginRequest createNonExistentUserLoginRequest() {
        return new LoginRequest(
                "nonexistent@example.com",
                "Test@123"
        );
    }
    
    public static Map<String, String> createEmailCheckPayload(String email) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        return payload;
    }
    
    public static Map<String, String> createSendOtpPayload(String email) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        return payload;
    }
    
    public static Map<String, String> createVerifyOtpPayload(String email, String otp) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("otp", otp);
        return payload;
    }
    
    public static Map<String, String> createPasswordResetPayload(String email, String newPassword) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", newPassword);
        return payload;
    }
}
