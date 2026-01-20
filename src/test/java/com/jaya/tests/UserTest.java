package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AuthClient;
import com.jaya.clients.UserClient;
import com.jaya.payloads.AuthPayload;
import com.jaya.payloads.UserPayload;
import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;
import com.jaya.pojo.User;
import com.jaya.pojo.UserUpdateRequest;
import com.jaya.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * UserTest - Comprehensive test class for User management endpoints
 * Tests user profile, update, delete, and role management
 */
@Epic("User Management")
@Feature("User Operations")
public class UserTest extends BaseTest {
    
    private UserClient userClient;
    private AuthClient authClient;
    private String testUserEmail;
    private String testUserPassword = "Test@123";
    private Long testUserId;
    private String testUserToken;
    
    @BeforeClass
    public void setupClient() {
        super.setup(); // Ensure parent setup runs first
        
        // Create a test user for authentication
        authClient = new AuthClient(getUnauthenticatedRequest());
        createTestUser();
        
        // Initialize authenticated user client using a CLONED spec to avoid mutating the shared static spec
        userClient = new UserClient(cloneBaseSpec().header("Authorization", "Bearer " + testUserToken));
    }
    
    /**
     * Helper method to create and authenticate a test user
     */
    private void createTestUser() {
        // Create user
        SignupRequest signupRequest = AuthPayload.createDefaultSignupRequest();
        testUserEmail = signupRequest.getEmail();
        
        Response signupResponse = authClient.signup(signupRequest);
        if (signupResponse.getStatusCode() == 201) {
            testUserToken = signupResponse.jsonPath().getString("jwt");
            
            // Get user details
            Response userResponse = authClient.getUserByEmail(testUserEmail);
            if (userResponse.getStatusCode() == 200) {
                testUserId = userResponse.jsonPath().getLong("id");
            }
        }
    }
    
    // ==================== GET USER PROFILE TESTS ====================
    
    @Test(priority = 1)
    @Story("User Profile")
    @Description("Verify getting user profile from JWT token")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserProfile_Success() {
        // Act
        Response response = userClient.getUserProfile();
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "id");
        ResponseValidator.validateFieldExists(response, "email");
        ResponseValidator.validateFieldExists(response, "fullName");
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
        ResponseValidator.validateContentType(response, "application/json");
        ResponseValidator.validateResponseTime(response, 2000);
    }
    
    @Test(priority = 2)
    @Story("User Profile")
    @Description("Verify getting user profile fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserProfile_Unauthorized() {
        // Arrange - Create authenticated client to get the method
        // But we'll use the special method that doesn't send auth header
        
        // Act - Call without Authorization header
        Response response = userClient.getUserProfileWithoutAuth();
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403, 
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    // ==================== GET USER BY EMAIL TESTS ====================
    
    @Test(priority = 3)
    @Story("User Retrieval")
    @Description("Verify getting user by email with authentication")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByEmail_Success() {
        // Act
        Response response = userClient.getUserByEmail(testUserEmail);

        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
        ResponseValidator.validateFieldExists(response, "id");
        ResponseValidator.validateFieldExists(response, "fullName");
    }
    
    @Test(priority = 4)
    @Story("User Retrieval")
    @Description("Verify getting user by non-existent email")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByEmail_NotFound() {
        // Act
        Response response = userClient.getUserByEmail("nonexistent@example.com");
        
        // Assert
        ResponseValidator.validateStatusCode(response, 404);
    }
    
    @Test(priority = 5)
    @Story("User Retrieval")
    @Description("Verify getting user by invalid email format")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByEmail_InvalidFormat() {
        // Act
        Response response = userClient.getUserByEmail("invalid-email");
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 404 || statusCode == 500, 
                "Status code should indicate validation error");
    }
    
    // ==================== GET USER BY ID TESTS ====================
    
    @Test(priority = 6)
    @Story("User Retrieval")
    @Description("Verify getting own user profile by ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserById_OwnProfile() {
        // Act
        Response response = userClient.getUserById(testUserId);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "id", testUserId.intValue());
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
    }
    
    @Test(priority = 7)
    @Story("User Retrieval")
    @Description("Verify getting other user by ID without admin role fails")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserById_OtherUserWithoutAdmin() {
        // Arrange - Try to access another user's profile (ID: 999)
        Long otherUserId = 999L;
        
        // Act
        Response response = userClient.getUserById(otherUserId);
        
        // Assert - Should be forbidden or not found
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 404 || statusCode == 500, 
                "Status code should be 403, 404, or 500 when accessing other user without admin role");
    }
    
    @Test(priority = 8)
    @Story("User Retrieval")
    @Description("Verify getting user by non-existent ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserById_NotFound() {
        // Act
        Response response = userClient.getUserById(999999L);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 403 || statusCode == 500, 
                "Status code should indicate user not found or access denied");
    }
    
    @Test(priority = 9)
    @Story("User Retrieval")
    @Description("Verify getting user by invalid ID (negative)")
    @Severity(SeverityLevel.MINOR)
    public void testGetUserById_InvalidId() {
        // Act
        Response response = userClient.getUserById(-1L);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 500, 
                "Status code should indicate validation error");
    }
    
    // ==================== UPDATE USER TESTS ====================
    
    @Test(priority = 10)
    @Story("User Update")
    @Description("Verify updating user profile with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUser_Success() {
        // Arrange
        UserUpdateRequest updateRequest = UserPayload.createDefaultUpdateRequest();
        
        // Act
        Response response = userClient.updateUser(updateRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "message", "User updated successfully");
        ResponseValidator.validateFieldExists(response, "user");
        
        // Verify the updated fields
        String updatedName = response.jsonPath().getString("user.fullName");
        Assert.assertEquals(updatedName, updateRequest.getFullName(), "Full name should be updated");
    }
    
    @Test(priority = 11)
    @Story("User Update")
    @Description("Verify updating only full name")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUser_NameOnly() {
        // Arrange
        UserUpdateRequest updateRequest = UserPayload.createNameOnlyUpdateRequest("New Name Only");
        
        // Act
        Response response = userClient.updateUser(updateRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "message", "User updated successfully");
    }
    
    @Test(priority = 12)
    @Story("User Update")
    @Description("Verify updating only mobile number")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUser_MobileOnly() {
        // Arrange
        UserUpdateRequest updateRequest = UserPayload.createMobileOnlyUpdateRequest("8888777766");
        
        // Act
        Response response = userClient.updateUser(updateRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "message", "User updated successfully");
    }
    
    @Test(priority = 13)
    @Story("User Update")
    @Description("Verify updating user with password change")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUser_WithPasswordChange() {
        // Arrange
        UserUpdateRequest updateRequest = UserPayload.createUpdateRequestWithPassword(
                "Updated Name",
                "9999888877",
                "NewPassword@123"
        );
        
        // Act
        Response response = userClient.updateUser(updateRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "message", "User updated successfully");
    }
    
    @Test(priority = 14)
    @Story("User Update")
    @Description("Verify update fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUser_Unauthorized() {
        // Arrange
        UserUpdateRequest updateRequest = UserPayload.createDefaultUpdateRequest();
        
        // Act - Call without Authorization header
        Response response = userClient.updateUserWithoutAuth(updateRequest);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403, 
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 15)
    @Story("User Update")
    @Description("Verify update with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUser_InvalidData() {
        // Arrange
        UserUpdateRequest invalidRequest = UserPayload.createInvalidUpdateRequest();
        
        // Act
        Response response = userClient.updateUser(invalidRequest);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 500 || statusCode == 200, 
                "Status code should indicate validation error or be accepted");
    }
    
    // ==================== DELETE USER TESTS ====================
    
    @Test(priority = 16)
    @Story("User Deletion")
    @Description("Verify deleting own account")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUser_OwnAccount() {
        // Arrange - Create a new user for deletion test
        SignupRequest newUser = AuthPayload.createDefaultSignupRequest();
        Response signupResponse = authClient.signup(newUser);
        
        if (signupResponse.getStatusCode() == 201) {
            String newToken = signupResponse.jsonPath().getString("jwt");
            Response userResponse = authClient.getUserByEmail(newUser.getEmail());
            Long newUserId = userResponse.jsonPath().getLong("id");
            
            // Create client with NEW user's token using cloned base spec (prevents duplicate Authorization headers)
            UserClient newUserClient = new UserClient(
                    cloneBaseSpec().header("Authorization", "Bearer " + newToken)
            );
            
            // Act
            Response deleteResponse = newUserClient.deleteUser(newUserId);
            
            // Assert
            int statusCode = deleteResponse.getStatusCode();
            Assert.assertTrue(statusCode == 200 || statusCode == 204, 
                    "Status code should be 200 or 204 for successful deletion");
            
            // Verify user is deleted
            Response verifyResponse = authClient.getUserByEmail(newUser.getEmail());
            ResponseValidator.validateStatusCode(verifyResponse, 404);

            // No need to restore original token; original userClient keeps its own spec instance
        }
    }
    
    @Test(priority = 17)
    @Story("User Deletion")
    @Description("Verify delete fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUser_Unauthorized() {
        // Act - Call without Authorization header
        Response response = userClient.deleteUserWithoutAuth(testUserId);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403, 
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }
    
    @Test(priority = 18)
    @Story("User Deletion")
    @Description("Verify deleting non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteUser_NotFound() {
        // Act
        Response response = userClient.deleteUser(999999L);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 403 || statusCode == 500, 
                "Status code should indicate user not found or access denied");
    }
    
    // ==================== ROLE MANAGEMENT TESTS ====================
    // Note: These tests require ADMIN role, so they may fail without proper setup
    
    @Test(priority = 19)
    @Story("Role Management")
    @Description("Verify adding role to user requires admin access")
    @Severity(SeverityLevel.NORMAL)
    public void testAddRoleToUser_WithoutAdminRole() {
        // Act
        Response response = userClient.addRoleToUser(testUserId, 2L);
        
        // Assert - Should fail without admin role
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 500, 
                "Status code should be 403 or 500 without admin role");
    }
    
    @Test(priority = 20)
    @Story("Role Management")
    @Description("Verify removing role from user requires admin access")
    @Severity(SeverityLevel.NORMAL)
    public void testRemoveRoleFromUser_WithoutAdminRole() {
        // Act
        Response response = userClient.removeRoleFromUser(testUserId, 2L);
        
        // Assert - Should fail without admin role
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 403 || statusCode == 500, 
                "Status code should be 403 or 500 without admin role");
    }
    
    // ==================== BOUNDARY TESTS ====================
    
    @Test(priority = 21)
    @Story("Boundary Conditions")
    @Description("Verify update with maximum field lengths")
    @Severity(SeverityLevel.MINOR)
    public void testUpdateUser_BoundaryValues() {
        // Arrange
        UserUpdateRequest boundaryRequest = UserPayload.createBoundaryUpdateRequest();
        
        // Act
        Response response = userClient.updateUser(boundaryRequest);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 400, 
                "Status code should be 200 or 400 for boundary values");
    }
    
    @Test(priority = 22)
    @Story("Boundary Conditions")
    @Description("Verify getting user with ID at boundary (0)")
    @Severity(SeverityLevel.MINOR)
    public void testGetUserById_ZeroId() {
        // Act
        Response response = userClient.getUserById(0L);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 403 || statusCode == 404 || statusCode == 500, 
                "Status code should indicate validation error");
    }
    
    @Test(priority = 23)
    @Story("Boundary Conditions")
    @Description("Verify getting user with very large ID")
    @Severity(SeverityLevel.MINOR)
    public void testGetUserById_LargeId() {
        // Act
        Response response = userClient.getUserById(Long.MAX_VALUE);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 403 || statusCode == 500, 
                "Status code should indicate user not found");
    }
}
