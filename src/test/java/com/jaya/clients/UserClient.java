package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.constants.Endpoints;
import com.jaya.pojo.UserUpdateRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * UserClient - API client for user management operations
 */
public class UserClient extends BaseClient {

    public UserClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Get user profile from JWT")
    public Response getUserProfile() {
        return get(Endpoints.USER.PROFILE);
    }

    @Step("Attempt to get user profile without authentication")
    public Response getUserProfileWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .get(Endpoints.USER.PROFILE)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    @Step("Get user by email: {email}")
    public Response getUserByEmail(String email) {
        return getWithQueryParam(Endpoints.USER.BY_EMAIL, "email", email);
    }

    @Step("Get user by ID: {userId}")
    public Response getUserById(Long userId) {
        return getWithPathParam(Endpoints.USER.BY_ID, "id", userId);
    }

    @Step("Update user profile")
    public Response updateUser(UserUpdateRequest updateRequest) {
        return put(Endpoints.USER.UPDATE, updateRequest);
    }

    @Step("Attempt to update user profile without authentication")
    public Response updateUserWithoutAuth(UserUpdateRequest updateRequest) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .body(updateRequest)
                .when()
                .put(Endpoints.USER.UPDATE)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    @Step("Delete user by ID: {userId}")
    public Response deleteUser(Long userId) {
        return deleteWithPathParam(Endpoints.USER.DELETE, "id", userId);
    }

    @Step("Attempt to delete user without authentication")
    public Response deleteUserWithoutAuth(Long userId) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .delete(replacePath(Endpoints.USER.DELETE, "id", userId))
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    @Step("Get all users")
    public Response getAllUsers() {
        return get(Endpoints.USER.ALL);
    }

    @Step("Search users with query: {searchQuery}")
    public Response searchUsers(String searchQuery) {
        return getWithQueryParam(Endpoints.USER.SEARCH, "query", searchQuery);
    }

    @Step("Add role {roleId} to user {userId}")
    public Response addRoleToUser(Long userId, Long roleId) {
        return postWithPathParam(
                replacePath(Endpoints.USER.ADD_ROLE, "userId", userId),
                "roleId", roleId, "");
    }

    @Step("Remove role {roleId} from user {userId}")
    public Response removeRoleFromUser(Long userId, Long roleId) {
        return deleteWithPathParam(
                replacePath(Endpoints.USER.REMOVE_ROLE, "userId", userId),
                "roleId", roleId);
    }

    @Step("Switch user mode to: {mode}")
    public Response switchUserMode(String mode) {
        return putWithQueryParam(Endpoints.USER.SWITCH_MODE, "mode", mode, "");
    }

    @Step("Attempt to switch user mode without authentication")
    public Response switchUserModeWithoutAuth(String mode) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .queryParam("mode", mode)
                .when()
                .put(Endpoints.USER.SWITCH_MODE)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
