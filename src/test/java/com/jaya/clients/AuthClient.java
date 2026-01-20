package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.constants.Endpoints;
import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthClient extends BaseClient {
    
    public AuthClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    @Step("Register new user with email: {signupRequest.email}")
    public Response signup(SignupRequest signupRequest) {
        return post(Endpoints.AUTH.SIGNUP, signupRequest);
    }
    
    @Step("Login user with email: {loginRequest.email}")
    public Response signin(LoginRequest loginRequest) {
        return post(Endpoints.AUTH.SIGNIN, loginRequest);
    }
    
    @Step("Refresh authentication token")
    public Response refreshToken() {
        return postWithoutBody(Endpoints.AUTH.REFRESH_TOKEN);
    }
    
    @Step("Attempt to refresh token without authentication")
    public Response refreshTokenWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .post(Endpoints.AUTH.REFRESH_TOKEN)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Get user by ID: {userId}")
    public Response getUserById(Long userId) {
        return get(replacePath(Endpoints.AUTH.USER_BY_ID, "userId", userId));
    }
    
    @Step("Get user by email: {email}")
    public Response getUserByEmail(String email) {
        return getWithQueryParam(Endpoints.AUTH.USER_BY_EMAIL, "email", email);
    }
    
    @Step("Get all users")
    public Response getAllUsers() {
        return get(Endpoints.AUTH.ALL_USERS);
    }
    
    @Step("Check email availability")
    public Response checkEmail(Map<String, String> emailPayload) {
        return post(Endpoints.AUTH.CHECK_EMAIL, emailPayload);
    }
    
    @Step("Send OTP to email")
    public Response sendOtp(Map<String, String> otpPayload) {
        return post(Endpoints.AUTH.SEND_OTP, otpPayload);
    }
    
    @Step("Verify OTP for email")
    public Response verifyOtp(Map<String, String> verifyPayload) {
        return post(Endpoints.AUTH.VERIFY_OTP, verifyPayload);
    }
    
    @Step("Reset password for user")
    public Response resetPassword(Map<String, String> resetPayload) {
        return patch(Endpoints.AUTH.RESET_PASSWORD, resetPayload);
    }
    
    @Step("Get user by ID (alternate endpoint): {userId}")
    public Response getUserByIdAlt(Long userId) {
        return get(replacePath(Endpoints.AUTH.USER_BY_ID_ALT, "userId", userId));
    }
    
    @Step("Initiate forgot password for email")
    public Response forgotPassword(Map<String, String> emailPayload) {
        return post(Endpoints.AUTH.FORGOT_PASSWORD, emailPayload);
    }
    
    @Step("Logout user")
    public Response logout() {
        return postWithoutBody(Endpoints.AUTH.LOGOUT);
    }
}
