package com.jaya.clients;

import com.jaya.constants.Endpoints;
import com.jaya.pojo.UserUpdateRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UserClient extends BaseClient {

    public UserClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Get user profile")
    public Response getUserProfile() {
        return get(Endpoints.USER.PROFILE);
    }

    @Step("Get user profile without auth")
    public Response getUserProfileWithoutAuth() {
        return unauthenticatedGet(Endpoints.USER.PROFILE);
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

    @Step("Update user without auth")
    public Response updateUserWithoutAuth(UserUpdateRequest updateRequest) {
        return unauthenticatedPut(Endpoints.USER.UPDATE, updateRequest);
    }

    @Step("Delete user ID: {userId}")
    public Response deleteUser(Long userId) {
        return deleteWithPathParam(Endpoints.USER.DELETE, "id", userId);
    }

    @Step("Delete user without auth")
    public Response deleteUserWithoutAuth(Long userId) {
        return unauthenticatedDelete(replacePath(Endpoints.USER.DELETE, "id", userId));
    }

    @Step("Get all users")
    public Response getAllUsers() {
        return get(Endpoints.USER.ALL);
    }

    @Step("Search users: {searchQuery}")
    public Response searchUsers(String searchQuery) {
        return getWithQueryParam(Endpoints.USER.SEARCH, "query", searchQuery);
    }

    @Step("Add role {roleId} to user {userId}")
    public Response addRoleToUser(Long userId, Long roleId) {
        return postWithPathParam(replacePath(Endpoints.USER.ADD_ROLE, "userId", userId), "roleId", roleId, "");
    }

    @Step("Remove role {roleId} from user {userId}")
    public Response removeRoleFromUser(Long userId, Long roleId) {
        return deleteWithPathParam(replacePath(Endpoints.USER.REMOVE_ROLE, "userId", userId), "roleId", roleId);
    }

    @Step("Switch user mode to: {mode}")
    public Response switchUserMode(String mode) {
        return putWithQueryParam(Endpoints.USER.SWITCH_MODE, "mode", mode, "");
    }

    @Step("Switch user mode without auth")
    public Response switchUserModeWithoutAuth(String mode) {
        return unauthenticatedPutWithQueryParam(Endpoints.USER.SWITCH_MODE, "mode", mode);
    }
}
