package com.jaya.pojo;

/**
 * AuthResponse POJO - Response model for authentication
 */
public class AuthResponse {
    
    private Boolean status;
    private String message;
    private String jwt;
    
    // Constructors
    public AuthResponse() {
    }
    
    public AuthResponse(Boolean status, String message, String jwt) {
        this.status = status;
        this.message = message;
        this.jwt = jwt;
    }
    
    // Getters and Setters
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getJwt() {
        return jwt;
    }
    
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", jwt='" + jwt + '\'' +
                '}';
    }
}
