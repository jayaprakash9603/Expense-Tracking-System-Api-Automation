package com.jaya.payloads;

import com.jaya.pojo.UserUpdateRequest;

public class UserPayload {
    
    public static UserUpdateRequest createDefaultUpdateRequest() {
        return new UserUpdateRequest(
                "Updated User Name",
                "9999888877"
        );
    }
    
    public static UserUpdateRequest createUpdateRequest(String fullName, String mobile) {
        return new UserUpdateRequest(fullName, mobile);
    }
    
    public static UserUpdateRequest createUpdateRequestWithPassword(String fullName, String mobile, String password) {
        return new UserUpdateRequest(fullName, mobile, password);
    }
    
    public static UserUpdateRequest createInvalidUpdateRequest() {
        return new UserUpdateRequest(
                "",
                "123"
        );
    }
    
    public static UserUpdateRequest createBoundaryUpdateRequest() {
        return new UserUpdateRequest(
                "A".repeat(255),
                "9999999999"
        );
    }
    
    public static UserUpdateRequest createNameOnlyUpdateRequest(String fullName) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName(fullName);
        return request;
    }
    
    public static UserUpdateRequest createMobileOnlyUpdateRequest(String mobile) {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setMobile(mobile);
        return request;
    }
}
