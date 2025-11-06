package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.pojo.AuthResponse;
import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;
import com.jaya.pojo.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * AuthClient - API client for Authentication endpoints
 * Encapsulates all HTTP calls for auth operations
 */
public class AuthClient extends BaseClient {
    
    private static final String SIGNUP_ENDPOINT = "/auth/signup";
    private static final String SIGNIN_ENDPOINT = "/auth/signin";
    private static final String REFRESH_TOKEN_ENDPOINT = "/auth/refresh-token";
    private static final String USER_BY_ID_ENDPOINT = "/auth/user/{userId}";
    private static final String USER_BY_EMAIL_ENDPOINT = "/auth/email";
    private static final String ALL_USERS_ENDPOINT = "/auth/all-users";
    private static final String CHECK_EMAIL_ENDPOINT = "/auth/check-email";
    private static final String SEND_OTP_ENDPOINT = "/auth/send-otp";
    private static final String VERIFY_OTP_ENDPOINT = "/auth/verify-otp";
    private static final String RESET_PASSWORD_ENDPOINT = "/auth/reset-password";
    
    /**
     * Constructor
     * @param requestSpec RequestSpecification instance
     */
    public AuthClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    /**
     * User signup/registration
     * @param signupRequest Signup request payload
     * @return Response object
     */
    @Step("Register new user with email: {signupRequest.email}")
    public Response signup(SignupRequest signupRequest) {
        return post(SIGNUP_ENDPOINT, signupRequest);
    }
    
    /**
     * User signin/login
     * @param loginRequest Login request payload
     * @return Response object
     */
    @Step("Login user with email: {loginRequest.email}")
    public Response signin(LoginRequest loginRequest) {
        return post(SIGNIN_ENDPOINT, loginRequest);
    }
    
    /**
     * Refresh authentication token
     * @return Response object
     */
    @Step("Refresh authentication token")
    public Response refreshToken() {
        return post(REFRESH_TOKEN_ENDPOINT, "");
    }
    
    /**
     * Refresh authentication token without Authorization header (for testing unauthorized access)
     * @return Response object
     */
    @Step("Attempt to refresh token without authentication")
    public Response refreshTokenWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .post(REFRESH_TOKEN_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return Response object
     */
    @Step("Get user by ID: {userId}")
    public Response getUserById(Long userId) {
        return get(USER_BY_ID_ENDPOINT.replace("{userId}", String.valueOf(userId)));
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return Response object
     */
    @Step("Get user by email: {email}")
    public Response getUserByEmail(String email) {
        return given()
                .spec(requestSpec)
                .queryParam("email", email)
                .when()
                .get(USER_BY_EMAIL_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    /**
     * Get all users
     * @return Response object
     */
    @Step("Get all users")
    public Response getAllUsers() {
        return get(ALL_USERS_ENDPOINT);
    }
    
    /**
     * Check email availability
     * @param emailPayload Map containing email
     * @return Response object
     */
    @Step("Check email availability")
    public Response checkEmail(Map<String, String> emailPayload) {
        return post(CHECK_EMAIL_ENDPOINT, emailPayload);
    }
    
    /**
     * Send OTP to email
     * @param otpPayload Map containing email
     * @return Response object
     */
    @Step("Send OTP to email")
    public Response sendOtp(Map<String, String> otpPayload) {
        return post(SEND_OTP_ENDPOINT, otpPayload);
    }
    
    /**
     * Verify OTP
     * @param verifyPayload Map containing email and OTP
     * @return Response object
     */
    @Step("Verify OTP for email")
    public Response verifyOtp(Map<String, String> verifyPayload) {
        return post(VERIFY_OTP_ENDPOINT, verifyPayload);
    }
    
    /**
     * Reset password
     * @param resetPayload Map containing email and new password
     * @return Response object
     */
    @Step("Reset password for email: {resetPayload.email}")
    public Response resetPassword(Map<String, String> resetPayload) {
        return patch(RESET_PASSWORD_ENDPOINT, resetPayload);
    }
}
