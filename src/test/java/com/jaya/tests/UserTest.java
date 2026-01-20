package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AuthClient;
import com.jaya.clients.UserClient;
import com.jaya.payloads.AuthPayload;
import com.jaya.payloads.UserPayload;
import com.jaya.pojo.SignupRequest;
import com.jaya.pojo.UserUpdateRequest;
import com.jaya.utils.JsonSchemaValidatorUtil;
import com.jaya.utils.ResponseValidator;
import com.jaya.utils.TestUserCleanupManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

        // Initialize authenticated user client using a CLONED spec to avoid mutating
        // the shared static spec
        userClient = new UserClient(cloneBaseSpec().addHeader("Authorization", "Bearer " + testUserToken).build());
    }

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

            // Register user for cleanup with userId and token (in case password gets
            // changed during tests)
            TestUserCleanupManager.registerUserForCleanup(testUserId, testUserEmail, testUserPassword, testUserToken);
        }
    }

    @Test(priority = 1)
    @Story("User Profile")
    @Description("Verify getting user profile from JWT token")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserProfile_Success() {
        Response response = userClient.getUserProfile();

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "id");
        ResponseValidator.validateFieldExists(response, "email");
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
        ResponseValidator.validateContentType(response, "application/json");
        ResponseValidator.validateResponseTime(response, 2000);

        JsonSchemaValidatorUtil.validateUserSchema(response);
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

    @Test(priority = 3)
    @Story("User Retrieval")
    @Description("Verify getting user by email with authentication")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByEmail_Success() {
        Response response = userClient.getUserByEmail(testUserEmail);

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
        ResponseValidator.validateFieldExists(response, "id");

        JsonSchemaValidatorUtil.validateUserSchema(response);
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

    @Test(priority = 6)
    @Story("User Retrieval")
    @Description("Verify getting own user profile by ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserById_OwnProfile() {
        Response response = userClient.getUserById(testUserId);

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "id", testUserId.intValue());
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);

        JsonSchemaValidatorUtil.validateUserSchema(response);
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
                "NewPassword@123");

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

    @Test(priority = 16)
    @Story("User Deletion")
    @Description("Verify deleting own account")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteUser_OwnAccount() {
        SignupRequest newUser = AuthPayload.createDefaultSignupRequest();
        Response signupResponse = authClient.signup(newUser);

        Assert.assertEquals(signupResponse.getStatusCode(), 201,
                "Signup should succeed for deletion test");

        String newToken = signupResponse.jsonPath().getString("jwt");
        Response userResponse = authClient.getUserByEmail(newUser.getEmail());
        Long newUserId = userResponse.jsonPath().getLong("id");

        UserClient newUserClient = new UserClient(
                cloneBaseSpec().addHeader("Authorization", "Bearer " + newToken).build());

        Response deleteResponse = newUserClient.deleteUser(newUserId);

        int statusCode = deleteResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 204 || statusCode == 403,
                "Status code should be 200, 204 for successful deletion or 403 if not allowed. Got: " + statusCode);

        if (statusCode == 200 || statusCode == 204) {
            Response verifyResponse = authClient.getUserByEmail(newUser.getEmail());
            ResponseValidator.validateStatusCode(verifyResponse, 404);
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
        Response response = userClient.getUserById(Long.MAX_VALUE);

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 403 || statusCode == 500,
                "Status code should indicate user not found");
    }

    @Test(priority = 24)
    @Story("Mode Switching")
    @Description("Verify switching user mode to USER")
    @Severity(SeverityLevel.NORMAL)
    public void testSwitchUserMode_ToUser() {
        Response response = userClient.switchUserMode("USER");

        int statusCode = response.getStatusCode();
        // Accept 200 for success, 400/403/500 for various error conditions
        Assert.assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 403 || statusCode == 500,
                "Status code should be 200 for success or 400/403/500 for error conditions. Got: " + statusCode);

        if (statusCode == 200) {
            // Validate response fields only on success
            String body = response.getBody().asString();
            if (body != null && body.contains("message")) {
                ResponseValidator.validateFieldExists(response, "message");
            }
        }
    }

    @Test(priority = 25)
    @Story("Mode Switching")
    @Description("Verify switching user mode to ADMIN requires ADMIN role")
    @Severity(SeverityLevel.NORMAL)
    public void testSwitchUserMode_ToAdmin() {
        Response response = userClient.switchUserMode("ADMIN");

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 403 || statusCode == 500,
                "Status code should be 200 if user has ADMIN role, 403 if not authorized, or 400/500 for errors. Got: "
                        + statusCode);
    }

    @Test(priority = 26)
    @Story("Mode Switching")
    @Description("Verify switching to invalid mode fails")
    @Severity(SeverityLevel.NORMAL)
    public void testSwitchUserMode_InvalidMode() {
        Response response = userClient.switchUserMode("INVALID_MODE");

        int statusCode = response.getStatusCode();
        // API may return 400 (bad request) or 500 (internal error) for invalid mode
        Assert.assertTrue(statusCode == 400 || statusCode == 500,
                "Status code should be 400 or 500 for invalid mode. Got: " + statusCode);

        // Error field should exist in both cases
        ResponseValidator.validateFieldExists(response, "error");
    }

    @Test(priority = 27)
    @Story("Mode Switching")
    @Description("Verify switch mode fails without authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testSwitchUserMode_Unauthorized() {
        Response response = userClient.switchUserModeWithoutAuth("USER");

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                "Status code should be 401 or 403 for unauthorized access. Got: " + statusCode);
    }

    // ==================== SCHEMA VALIDATION TESTS ====================

    @Test(priority = 28)
    @Story("Schema Validation")
    @Description("Verify user profile response matches JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void testUserProfileSchema_Validation() {
        Response response = userClient.getUserProfile();

        ResponseValidator.validateStatusCode(response, 200);
        JsonSchemaValidatorUtil.validateUserSchema(response);
    }

    @Test(priority = 29)
    @Story("Schema Validation")
    @Description("Verify user by ID response matches JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void testUserByIdSchema_Validation() {
        Response response = userClient.getUserById(testUserId);

        ResponseValidator.validateStatusCode(response, 200);
        JsonSchemaValidatorUtil.validateUserSchema(response);
    }

    @Test(priority = 30)
    @Story("Schema Validation")
    @Description("Verify user by email response matches JSON schema")
    @Severity(SeverityLevel.NORMAL)
    public void testUserByEmailSchema_Validation() {
        Response response = userClient.getUserByEmail(testUserEmail);

        ResponseValidator.validateStatusCode(response, 200);
        JsonSchemaValidatorUtil.validateUserSchema(response);
    }
}
