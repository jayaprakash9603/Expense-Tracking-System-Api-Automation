package com.jaya.clients;

import com.jaya.constants.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AdminClient extends BaseClient {

    public AdminClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Get all users")
    public Response getAllUsers() {
        return get(Endpoints.ADMIN.USERS);
    }

    @Step("Get all users (alternate)")
    public Response getAllUsersAlt() {
        return get(Endpoints.ADMIN.ALL);
    }

    @Step("Get all users without auth")
    public Response getAllUsersWithoutAuth() {
        return unauthenticatedGet(Endpoints.ADMIN.USERS);
    }
}
