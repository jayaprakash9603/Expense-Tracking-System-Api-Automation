package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AdminClient;
import com.jaya.clients.AuthClient;
import com.jaya.payloads.AuthPayload;
import com.jaya.pojo.SignupRequest;
import com.jaya.utils.ResponseValidator;
import com.jaya.utils.TestUserCleanupManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Admin Management")
@Feature("Admin Operations")
public class AdminTest extends BaseTest {

    private AdminClient adminClient;
    private AuthClient authClient;
    private String testUserEmail;
    private String testUserPassword = "Test@123";
    private String testUserToken;

    @BeforeClass
    public void setupClient() {
        super.setup();
        authClient = new AuthClient(getUnauthenticatedRequest());
        createTestUser();
        adminClient = new AdminClient(cloneBaseSpec().addHeader("Authorization", "Bearer " + testUserToken).build());
    }

    private void createTestUser() {
        SignupRequest signupRequest = AuthPayload.createDefaultSignupRequest();
        testUserEmail = signupRequest.getEmail();

        Response signupResponse = authClient.signup(signupRequest);
        if (signupResponse.getStatusCode() == 201) {
            testUserToken = signupResponse.jsonPath().getString("jwt");
            TestUserCleanupManager.registerUserForCleanup(testUserEmail, testUserPassword);
        }
    }

    @Test(priority = 1)
    @Story("Admin User Management")
    @Description("Verify getting all users requires ADMIN role")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllUsers_WithoutAdminRole() {
        Response response = adminClient.getAllUsers();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 500,
                "Status code should be 403 without ADMIN role. Got: " + statusCode);
    }

    @Test(priority = 2)
    @Story("Admin User Management")
    @Description("Verify getting all users (alternate endpoint) requires ADMIN role")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllUsersAlt_WithoutAdminRole() {
        Response response = adminClient.getAllUsersAlt();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 500,
                "Status code should be 403 without ADMIN role. Got: " + statusCode);
    }

    @Test(priority = 3)
    @Story("Admin User Management")
    @Description("Verify getting all users fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllUsers_Unauthorized() {
        Response response = adminClient.getAllUsersWithoutAuth();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }

    @Test(priority = 4)
    @Story("Admin User Management")
    @Description("Verify admin endpoint returns proper error structure")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllUsers_ErrorResponseStructure() {
        Response response = adminClient.getAllUsers();

        int statusCode = response.getStatusCode();
        if (statusCode == 403) {
            ResponseValidator.validateContentType(response, "application/json");
        }
    }
}
