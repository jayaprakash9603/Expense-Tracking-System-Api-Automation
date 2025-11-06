package com.jaya.payloads;

import com.jaya.pojo.UserUpdateRequest;

/**
 * UserPayload - Builder class for creating User request payloads
 * Provides helper methods to create test data for user operations
 */
public class UserPayload {
    
    /**
     * Create default user update request
     * @return UserUpdateRequest with default values
     */
    public static UserUpdateRequest createDefaultUpdateRequest() {
        return new UserUpdateRequest(
                "Updated User Name",
                "9999888877"
        );
    }
    
    /**
     * Create user update request with custom values
     * @param fullName Full name
     * @param mobile Mobile number
     * @return UserUpdateRequest object
     */
    public static UserUpdateRequest createUpdateRequest(String fullName, String mobile) {
        return new UserUpdateRequest(fullName, mobile);
    }
    
    /**
     * Create user update request with password change
     * @param fullName Full name
     * @param mobile Mobile number
     * @param password New password
     * @return UserUpdateRequest object
     */
    public static UserUpdateRequest createUpdateRequestWithPassword(String fullName, String mobile, String password) {
        return new UserUpdateRequest(fullName, mobile, password);
    }
    
    /**
     * Create user update request with invalid data
     * @return UserUpdateRequest with invalid data
     */
    public static UserUpdateRequest createInvalidUpdateRequest() {
        return new UserUpdateRequest(
                "", // Empty name
                "123" // Invalid mobile
        );
    }
    
    /**
     * Create user update request with boundary values
     * @return UserUpdateRequest with maximum length values
     */
    public static UserUpdateRequest createBoundaryUpdateRequest() {
        return new UserUpdateRequest(
                "A".repeat(255), // Maximum length name
                "9999999999" // Valid 10-digit mobile
        );
    }
    
    /**
     * Create user update request with only name
     * @param fullName Full name to update
     * @return UserUpdateRequest with only name
     */
    public static UserUpdateRequest createNameOnlyUpdateRequest(String fullName) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName(fullName);
        return request;
    }
    
    /**
     * Create user update request with only mobile
     * @param mobile Mobile to update
     * @return UserUpdateRequest with only mobile
     */
    public static UserUpdateRequest createMobileOnlyUpdateRequest(String mobile) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setMobile(mobile);
        return request;
    }
}
