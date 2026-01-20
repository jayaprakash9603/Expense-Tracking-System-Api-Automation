package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.constants.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AdminClient extends BaseClient {
    
    public AdminClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    @Step("Get all users (admin endpoint)")
    public Response getAllUsers() {
        return get(Endpoints.ADMIN.USERS);
    }
    
    @Step("Get all users (alternate admin endpoint)")
    public Response getAllUsersAlt() {
        return get(Endpoints.ADMIN.ALL);
    }
    
    @Step("Attempt to get all users without authentication")
    public Response getAllUsersWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .get(Endpoints.ADMIN.USERS)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
