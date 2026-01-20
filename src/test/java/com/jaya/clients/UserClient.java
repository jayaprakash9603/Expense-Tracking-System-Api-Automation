package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.pojo.UserUpdateRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {
    
    private static final String USER_PROFILE_ENDPOINT = "/api/user/profile";
    private static final String USER_BY_EMAIL_ENDPOINT = "/api/user/email";
    private static final String USER_BY_ID_ENDPOINT = "/api/user/{id}";
    private static final String UPDATE_USER_ENDPOINT = "/api/user";
    private static final String DELETE_USER_ENDPOINT = "/api/user/{id}";
    private static final String ADD_ROLE_ENDPOINT = "/api/user/{userId}/roles/{roleId}";
    private static final String REMOVE_ROLE_ENDPOINT = "/api/user/{userId}/roles/{roleId}";
    
    public UserClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    @Step("Get user profile from JWT")
    public Response getUserProfile() {
        return get(USER_PROFILE_ENDPOINT);
    }
    
    @Step("Attempt to get user profile without authentication")
    public Response getUserProfileWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .get(USER_PROFILE_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
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

    @Step("Get user by ID: {userId}")
    public Response getUserById(Long userId) {
        return get(USER_BY_ID_ENDPOINT.replace("{id}", String.valueOf(userId)));
    }
    
    @Step("Update user profile")
    public Response updateUser(UserUpdateRequest updateRequest) {
        return put(UPDATE_USER_ENDPOINT, updateRequest);
    }
    
    @Step("Attempt to update user profile without authentication")
    public Response updateUserWithoutAuth(UserUpdateRequest updateRequest) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .body(updateRequest)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Delete user by ID: {userId}")
    public Response deleteUser(Long userId) {
        return delete(DELETE_USER_ENDPOINT.replace("{id}", String.valueOf(userId)));
    }
    
    @Step("Attempt to delete user without authentication")
    public Response deleteUserWithoutAuth(Long userId) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .delete(DELETE_USER_ENDPOINT.replace("{id}", String.valueOf(userId)))
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Add role {roleId} to user {userId}")
    public Response addRoleToUser(Long userId, Long roleId) {
        String endpoint = ADD_ROLE_ENDPOINT
                .replace("{userId}", String.valueOf(userId))
                .replace("{roleId}", String.valueOf(roleId));
        return post(endpoint, "");
    }
    
    @Step("Remove role {roleId} from user {userId}")
    public Response removeRoleFromUser(Long userId, Long roleId) {
        String endpoint = REMOVE_ROLE_ENDPOINT
                .replace("{userId}", String.valueOf(userId))
                .replace("{roleId}", String.valueOf(roleId));
        return delete(endpoint);
    }
    
    @Step("Switch user mode to: {mode}")
    public Response switchUserMode(String mode) {
        return given()
                .spec(requestSpec)
                .queryParam("mode", mode)
                .when()
                .put("/api/user/switch-mode")
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Attempt to switch user mode without authentication")
    public Response switchUserModeWithoutAuth(String mode) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .queryParam("mode", mode)
                .when()
                .put("/api/user/switch-mode")
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
