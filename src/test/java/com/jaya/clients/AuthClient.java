package com.jaya.clients;

import com.jaya.constants.Endpoints;
import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class AuthClient extends BaseClient {

    public AuthClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Register new user: {signupRequest.email}")
    public Response signup(SignupRequest signupRequest) {
        return post(Endpoints.AUTH.SIGNUP, signupRequest);
    }

    @Step("Login user: {loginRequest.email}")
    public Response signin(LoginRequest loginRequest) {
        return post(Endpoints.AUTH.SIGNIN, loginRequest);
    }

    @Step("Refresh token")
    public Response refreshToken() {
        return postWithoutBody(Endpoints.AUTH.REFRESH_TOKEN);
    }

    @Step("Refresh token without auth")
    public Response refreshTokenWithoutAuth() {
        return unauthenticatedPost(Endpoints.AUTH.REFRESH_TOKEN, "");
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

    @Step("Send OTP")
    public Response sendOtp(Map<String, String> otpPayload) {
        return post(Endpoints.AUTH.SEND_OTP, otpPayload);
    }

    @Step("Verify OTP")
    public Response verifyOtp(Map<String, String> verifyPayload) {
        return post(Endpoints.AUTH.VERIFY_OTP, verifyPayload);
    }

    @Step("Reset password")
    public Response resetPassword(Map<String, String> resetPayload) {
        return patch(Endpoints.AUTH.RESET_PASSWORD, resetPayload);
    }

    @Step("Get user by ID (alt): {userId}")
    public Response getUserByIdAlt(Long userId) {
        return get(replacePath(Endpoints.AUTH.USER_BY_ID_ALT, "userId", userId));
    }

    @Step("Forgot password")
    public Response forgotPassword(Map<String, String> emailPayload) {
        return post(Endpoints.AUTH.FORGOT_PASSWORD, emailPayload);
    }

    @Step("Logout")
    public Response logout() {
        return postWithoutBody(Endpoints.AUTH.LOGOUT);
    }
}
