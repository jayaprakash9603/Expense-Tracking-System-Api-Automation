package com.jaya.clients;

import com.jaya.constants.Endpoints;
import com.jaya.pojo.RoleRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RoleClient extends BaseClient {

    public RoleClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Create role: {roleRequest.name}")
    public Response createRole(RoleRequest roleRequest) {
        return post(Endpoints.ROLE.CREATE, roleRequest);
    }

    @Step("Get all roles")
    public Response getAllRoles() {
        return get(Endpoints.ROLE.ALL);
    }

    @Step("Get role by ID: {roleId}")
    public Response getRoleById(Integer roleId) {
        return getWithPathParam(Endpoints.ROLE.BY_ID, "id", roleId);
    }

    @Step("Get role by name: {name}")
    public Response getRoleByName(String name) {
        return getWithPathParam(Endpoints.ROLE.BY_NAME, "name", name);
    }

    @Step("Update role: {roleId}")
    public Response updateRole(Integer roleId, RoleRequest roleRequest) {
        return putWithPathParam(Endpoints.ROLE.UPDATE, "id", roleId, roleRequest);
    }

    @Step("Delete role: {roleId}")
    public Response deleteRole(Integer roleId) {
        return deleteWithPathParam(Endpoints.ROLE.DELETE, "id", roleId);
    }

    @Step("Create role without auth")
    public Response createRoleWithoutAuth(RoleRequest roleRequest) {
        return unauthenticatedPost(Endpoints.ROLE.CREATE, roleRequest);
    }

    @Step("Get all roles without auth")
    public Response getAllRolesWithoutAuth() {
        return unauthenticatedGet(Endpoints.ROLE.ALL);
    }
}
