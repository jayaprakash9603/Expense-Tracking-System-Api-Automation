package com.jaya.clients;

import com.jaya.constants.Endpoints;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
        return post(Endpoints.DASHBOARD_PREFERENCE.SAVE, layoutConfig);
    }

    @Step("Reset dashboard preference")
    public Response resetDashboardPreference() {
        return delete(Endpoints.DASHBOARD_PREFERENCE.RESET);
    }

    @Step("Get dashboard preference without auth")
    public Response getDashboardPreferenceWithoutAuth() {
        return unauthenticatedGet(Endpoints.DASHBOARD_PREFERENCE.GET);
    }

    @Step("Save dashboard preference without auth")
    public Response saveDashboardPreferenceWithoutAuth(String layoutConfig) {
        return unauthenticatedPost(Endpoints.DASHBOARD_PREFERENCE.SAVE, layoutConfig);
    }

    @Step("Reset dashboard preference without auth")
    public Response resetDashboardPreferenceWithoutAuth() {
        return unauthenticatedDelete(Endpoints.DASHBOARD_PREFERENCE.RESET);
    }
}
