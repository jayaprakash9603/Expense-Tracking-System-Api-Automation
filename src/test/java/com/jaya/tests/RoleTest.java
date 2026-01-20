package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AuthClient;
import com.jaya.clients.RoleClient;
import com.jaya.payloads.AuthPayload;
import com.jaya.payloads.RolePayload;
import com.jaya.pojo.RoleRequest;
import com.jaya.pojo.SignupRequest;
import com.jaya.utils.TestUserCleanupManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Role Management")
@Feature("Role Operations")
public class RoleTest extends BaseTest {
    
    private RoleClient roleClient;
    private AuthClient authClient;
    private String testUserEmail;
    private String testUserPassword = "Test@123";
    private String testUserToken;
    
    @BeforeClass
    public void setupClient() {
        super.setup();
        authClient = new AuthClient(getUnauthenticatedRequest());
        createTestUser();
        roleClient = new RoleClient(cloneBaseSpec().header("Authorization", "Bearer " + testUserToken));
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
    @Story("Role Creation")
    @Description("Verify creating role requires ADMIN role")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateRole_WithoutAdminRole() {
        RoleRequest roleRequest = RolePayload.createDefaultRoleRequest();
        
        Response response = roleClient.createRole(roleRequest);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 500,
                "Status code should be 403 without ADMIN role. Got: " + statusCode);
    }
    
    @Test(priority = 2)
    @Story("Role Creation")
    @Description("Verify creating role fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateRole_Unauthorized() {
        RoleRequest roleRequest = RolePayload.createDefaultRoleRequest();
        
        Response response = roleClient.createRoleWithoutAuth(roleRequest);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 3)
    @Story("Role Retrieval")
    @Description("Verify getting all roles requires ADMIN role")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllRoles_WithoutAdminRole() {
        Response response = roleClient.getAllRoles();
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 500,
                "Status code should be 403 without ADMIN role. Got: " + statusCode);
    }
    
    @Test(priority = 4)
    @Story("Role Retrieval")
    @Description("Verify getting all roles fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllRoles_Unauthorized() {
        Response response = roleClient.getAllRolesWithoutAuth();
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 5)
    @Story("Role Retrieval")
    @Description("Verify getting role by ID requires ADMIN role")
    @Severity(SeverityLevel.NORMAL)
    public void testGetRoleById_WithoutAdminRole() {
        Response response = roleClient.getRoleById(1);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404 || statusCode == 500,
                "Status code should be 403 or 404 without ADMIN role. Got: " + statusCode);
    }
    
    @Test(priority = 6)
    @Story("Role Retrieval")
    @Description("Verify getting role by name requires ADMIN role")
    @Severity(SeverityLevel.NORMAL)
    public void testGetRoleByName_WithoutAdminRole() {
        Response response = roleClient.getRoleByName("USER");
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404 || statusCode == 500,
                "Status code should be 403 or 404 without ADMIN role. Got: " + statusCode);
    }
    
    @Test(priority = 7)
    @Story("Role Retrieval")
    @Description("Verify getting non-existent role by ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetRoleById_NotFound() {
        Response response = roleClient.getRoleById(999999);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404,
                "Status code should be 403 or 404 for non-existent role. Got: " + statusCode);
    }
    
    @Test(priority = 8)
    @Story("Role Retrieval")
    @Description("Verify getting role by non-existent name")
    @Severity(SeverityLevel.NORMAL)
    public void testGetRoleByName_NotFound() {
        Response response = roleClient.getRoleByName("NON_EXISTENT_ROLE");
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404,
                "Status code should be 403 or 404 for non-existent role. Got: " + statusCode);
    }
    
    @Test(priority = 9)
    @Story("Role Update")
    @Description("Verify updating role requires ADMIN role")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateRole_WithoutAdminRole() {
        RoleRequest updateRequest = RolePayload.createUpdateRoleRequest("UPDATED_ROLE", "Updated description");
        
        Response response = roleClient.updateRole(1, updateRequest);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404 || statusCode == 500,
                "Status code should be 403 or 404 without ADMIN role. Got: " + statusCode);
    }
    
    @Test(priority = 10)
    @Story("Role Deletion")
    @Description("Verify deleting role requires ADMIN role")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteRole_WithoutAdminRole() {
        Response response = roleClient.deleteRole(999);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404 || statusCode == 500,
                "Status code should be 403 or 404 without ADMIN role. Got: " + statusCode);
    }
    
    @Test(priority = 11)
    @Story("Role Validation")
    @Description("Verify creating role with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateRole_InvalidData() {
        RoleRequest invalidRequest = RolePayload.createInvalidRoleRequest();
        
        Response response = roleClient.createRole(invalidRequest);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 403 || statusCode == 500,
                "Status code should indicate validation error or forbidden. Got: " + statusCode);
    }
    
    @Test(priority = 12)
    @Story("Role Validation")
    @Description("Verify creating role with empty name")
    @Severity(SeverityLevel.MINOR)
    public void testCreateRole_EmptyName() {
        RoleRequest emptyNameRequest = RolePayload.createRoleRequestWithEmptyName();
        
        Response response = roleClient.createRole(emptyNameRequest);
        
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 403 || statusCode == 500,
                "Status code should indicate validation error or forbidden. Got: " + statusCode);
    }
}
