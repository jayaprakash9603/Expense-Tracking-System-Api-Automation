package com.jaya.clients;

import com.jaya.config.ConfigManager;
import com.jaya.constants.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class DashboardPreferenceClient extends BaseClient {
    
    public DashboardPreferenceClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    @Step("Get dashboard preference")
    public Response getDashboardPreference() {
        return get(Endpoints.DASHBOARD_PREFERENCE.GET);
    }
    
    @Step("Save dashboard preference")
    public Response saveDashboardPreference(String layoutConfig) {
        return given()
                .spec(requestSpec)
                .body(layoutConfig)
                .when()
                .post(Endpoints.DASHBOARD_PREFERENCE.SAVE)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Reset dashboard preference")
    public Response resetDashboardPreference() {
        return delete(Endpoints.DASHBOARD_PREFERENCE.RESET);
    }
    
    @Step("Attempt to get dashboard preference without authentication")
    public Response getDashboardPreferenceWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .get(Endpoints.DASHBOARD_PREFERENCE.GET)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Attempt to save dashboard preference without authentication")
    public Response saveDashboardPreferenceWithoutAuth(String layoutConfig) {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .body(layoutConfig)
                .when()
                .post(Endpoints.DASHBOARD_PREFERENCE.SAVE)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
    
    @Step("Attempt to reset dashboard preference without authentication")
    public Response resetDashboardPreferenceWithoutAuth() {
        return given()
                .contentType("application/json")
                .baseUri(ConfigManager.getBaseUrl())
                .when()
                .delete(Endpoints.DASHBOARD_PREFERENCE.RESET)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
