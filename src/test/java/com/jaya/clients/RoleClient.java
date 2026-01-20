package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.constants.Endpoints;
import com.jaya.pojo.RoleRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RoleClient extends BaseClient {
    
    public RoleClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    @Step("Create new role: {roleRequest.name}")
    public Response createRole(RoleRequest roleRequest) {
        return post(Endpoints.ROLE.CREATE, roleRequest);
    }
    
    @Step("Get all roles")
    public Response getAllRoles() {
        return get(Endpoints.ROLE.ALL);
    }
    
    @Step("Get role by ID: {roleId}")
    public Response getRoleById(Integer roleId) {
        return get(Endpoints.ROLE.BY_ID.replace("{id}", String.valueOf(roleId)));
    }
    
    @Step("Get role by name: {name}")
    public Response getRoleByName(String name) {
        return get(Endpoints.ROLE.BY_NAME.replace("{name}", name));
    }
    
    @Step("Update role: {roleId}")
    public Response updateRole(Integer roleId, RoleRequest roleRequest) {
        return put(Endpoints.ROLE.UPDATE.replace("{id}", String.valueOf(roleId)), roleRequest);
    }
    
    @Step("Delete role: {roleId}")
    public Response deleteRole(Integer roleId) {
        return delete(Endpoints.ROLE.DELETE.replace("{id}", String.valueOf(roleId)));
    }
    
    @Step("Attempt to create role without authentication")
    public Response createRoleWithoutAuth(RoleRequest roleRequest) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .body(roleRequest)
                .when()
                .post(Endpoints.ROLE.CREATE)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Attempt to get all roles without authentication")
    public Response getAllRolesWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .get(Endpoints.ROLE.ALL)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
