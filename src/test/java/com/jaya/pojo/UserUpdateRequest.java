package com.jaya.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * UserUpdateRequest POJO - Request model for updating user profile
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {
    
    private String fullName;
    private String mobile;
    private String password;
    
    // Constructors
    public UserUpdateRequest() {
    }
    
    public UserUpdateRequest(String fullName, String mobile) {
        this.fullName = fullName;
        this.mobile = mobile;
    }
    
    public UserUpdateRequest(String fullName, String mobile, String password) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.password = password;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "fullName='" + fullName + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
