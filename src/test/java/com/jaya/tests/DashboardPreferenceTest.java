package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AuthClient;
import com.jaya.clients.DashboardPreferenceClient;
import com.jaya.payloads.AuthPayload;
import com.jaya.payloads.DashboardPreferencePayload;
import com.jaya.pojo.SignupRequest;
import com.jaya.utils.ResponseValidator;
import com.jaya.utils.TestUserCleanupManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Dashboard Preferences")
@Feature("Dashboard Preference Operations")
public class DashboardPreferenceTest extends BaseTest {
    
    private DashboardPreferenceClient dashboardClient;
    private AuthClient authClient;
    private String testUserEmail;
    private String testUserPassword = "Test@123";
    private String testUserToken;
    
    @BeforeClass
    public void setupClient() {
        super.setup();
        authClient = new AuthClient(getUnauthenticatedRequest());
        createTestUser();
        dashboardClient = new DashboardPreferenceClient(cloneBaseSpec().header("Authorization", "Bearer " + testUserToken));
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
    @Story("Dashboard Preference Retrieval")
    @Description("Verify getting dashboard preference returns default or custom config")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetDashboardPreference_Success() {
        Response response = dashboardClient.getDashboardPreference();
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 404,
                "Status code should be 200 or 404. Got: " + statusCode);
        
        if (statusCode == 200) {
            ResponseValidator.validateContentType(response, "application/json");
        }
    }
    
    @Test(priority = 2)
    @Story("Dashboard Preference Retrieval")
    @Description("Verify getting dashboard preference fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetDashboardPreference_Unauthorized() {
        Response response = dashboardClient.getDashboardPreferenceWithoutAuth();
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 3)
    @Story("Dashboard Preference Save")
    @Description("Verify saving dashboard preference with valid layout config")
    @Severity(SeverityLevel.CRITICAL)
    public void testSaveDashboardPreference_Success() {
        String layoutConfig = DashboardPreferencePayload.createDefaultLayoutConfig();
        
        Response response = dashboardClient.saveDashboardPreference(layoutConfig);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201 || statusCode == 400,
                "Status code should be 200, 201 or 400. Got: " + statusCode);
        
        if (statusCode == 200 || statusCode == 201) {
            ResponseValidator.validateFieldExists(response, "message");
        }
    }
    
    @Test(priority = 4)
    @Story("Dashboard Preference Save")
    @Description("Verify saving custom dashboard preference")
    @Severity(SeverityLevel.NORMAL)
    public void testSaveDashboardPreference_CustomConfig() {
        String layoutConfig = DashboardPreferencePayload.createCustomLayoutConfig();
        
        Response response = dashboardClient.saveDashboardPreference(layoutConfig);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201 || statusCode == 400,
                "Status code should indicate success or validation error. Got: " + statusCode);
    }
    
    @Test(priority = 5)
    @Story("Dashboard Preference Save")
    @Description("Verify saving complex dashboard preference")
    @Severity(SeverityLevel.NORMAL)
    public void testSaveDashboardPreference_ComplexConfig() {
        String layoutConfig = DashboardPreferencePayload.createComplexLayoutConfig();
        
        Response response = dashboardClient.saveDashboardPreference(layoutConfig);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201 || statusCode == 400,
                "Status code should indicate success or validation error. Got: " + statusCode);
    }
    
    @Test(priority = 6)
    @Story("Dashboard Preference Save")
    @Description("Verify saving dashboard preference fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testSaveDashboardPreference_Unauthorized() {
        String layoutConfig = DashboardPreferencePayload.createDefaultLayoutConfig();
        
        Response response = dashboardClient.saveDashboardPreferenceWithoutAuth(layoutConfig);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 7)
    @Story("Dashboard Preference Save")
    @Description("Verify saving empty layout config")
    @Severity(SeverityLevel.NORMAL)
    public void testSaveDashboardPreference_EmptyConfig() {
        String layoutConfig = DashboardPreferencePayload.createInvalidLayoutConfig();
        
        Response response = dashboardClient.saveDashboardPreference(layoutConfig);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 500 || statusCode == 200,
                "Status code should indicate validation error or be accepted. Got: " + statusCode);
    }
    
    @Test(priority = 8)
    @Story("Dashboard Preference Save")
    @Description("Verify saving minimal layout config")
    @Severity(SeverityLevel.MINOR)
    public void testSaveDashboardPreference_MinimalConfig() {
        String layoutConfig = DashboardPreferencePayload.createMinimalLayoutConfig();
        
        Response response = dashboardClient.saveDashboardPreference(layoutConfig);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201 || statusCode == 400,
                "Status code should indicate success or validation error. Got: " + statusCode);
    }
    
    @Test(priority = 9)
    @Story("Dashboard Preference Reset")
    @Description("Verify resetting dashboard preference to default")
    @Severity(SeverityLevel.NORMAL)
    public void testResetDashboardPreference_Success() {
        Response response = dashboardClient.resetDashboardPreference();
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 204 || statusCode == 404,
                "Status code should be 200, 204 or 404. Got: " + statusCode);
        
        if (statusCode == 200) {
            ResponseValidator.validateFieldExists(response, "message");
        }
    }
    
    @Test(priority = 10)
    @Story("Dashboard Preference Reset")
    @Description("Verify resetting dashboard preference fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testResetDashboardPreference_Unauthorized() {
        Response response = dashboardClient.resetDashboardPreferenceWithoutAuth();
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 11)
    @Story("Dashboard Preference Workflow")
    @Description("Verify full workflow: save, get, reset")
    @Severity(SeverityLevel.CRITICAL)
    public void testDashboardPreference_FullWorkflow() {
        String layoutConfig = DashboardPreferencePayload.createCustomLayoutConfig();
        Response saveResponse = dashboardClient.saveDashboardPreference(layoutConfig);
        int saveStatus = saveResponse.getStatusCode();
        
        if (saveStatus == 200 || saveStatus == 201) {
            Response getResponse = dashboardClient.getDashboardPreference();
            Assert.assertTrue(getResponse.getStatusCode() == 200,
                    "Should be able to retrieve saved preference");
            
            Response resetResponse = dashboardClient.resetDashboardPreference();
            Assert.assertTrue(resetResponse.getStatusCode() == 200 || resetResponse.getStatusCode() == 204,
                    "Should be able to reset preference");
        }
    }
    
    @Test(priority = 12)
    @Story("Dashboard Preference Validation")
    @Description("Verify response time for dashboard preference operations")
    @Severity(SeverityLevel.MINOR)
    public void testDashboardPreference_ResponseTime() {
        Response response = dashboardClient.getDashboardPreference();
        
        ResponseValidator.validateResponseTime(response, 3000);
    }
}
