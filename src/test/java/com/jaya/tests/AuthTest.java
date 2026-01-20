package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AuthClient;
import com.jaya.payloads.AuthPayload;
import com.jaya.pojo.LoginRequest;
import com.jaya.pojo.SignupRequest;
import com.jaya.utils.ResponseValidator;
import com.jaya.utils.TestUserCleanupManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

@Epic("Authentication Management")
@Feature("Auth Operations")
public class AuthTest extends BaseTest {
    
    private AuthClient authClient;
    private String testUserEmail;
    private String testUserPassword = "Test@123";
    private Long testUserId;
    
    @BeforeClass
    public void setupClient() {
        super.setup(); // Ensure parent setup runs first
        authClient = new AuthClient(getUnauthenticatedRequest());
    }
    
    @Test(priority = 1)
    @Story("User Signup")
    @Description("Verify that a new user can successfully register")
    @Severity(SeverityLevel.CRITICAL)
    public void testSignup_Success() {
        // Arrange
        SignupRequest signupRequest = AuthPayload.createDefaultSignupRequest();
        testUserEmail = signupRequest.getEmail();
        
        // Act
        Response response = authClient.signup(signupRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 201);
        ResponseValidator.validateFieldExists(response, "jwt");
        ResponseValidator.validateFieldValue(response, "status", true);
        ResponseValidator.validateFieldValue(response, "message", "Registration Success");
        ResponseValidator.validateContentType(response, "application/json");
        
        String jwt = response.jsonPath().getString("jwt");
        Assert.assertNotNull(jwt, "JWT token should not be null");
        Assert.assertFalse(jwt.isEmpty(), "JWT token should not be empty");
        
        // Register user for cleanup at end of test suite
        TestUserCleanupManager.registerUserForCleanup(testUserEmail, testUserPassword);
    }
    
    @Test(priority = 2)
    @Story("User Signup")
    @Description("Verify that signup fails with duplicate email")
    @Severity(SeverityLevel.NORMAL)
    public void testSignup_DuplicateEmail() {
        // Arrange - Use the same email from previous test
        SignupRequest signupRequest = AuthPayload.createSignupRequest(
                "Another",
                "User",
                testUserEmail, // Duplicate email
                testUserPassword,
                "female"
        );
        
        // Act
        Response response = authClient.signup(signupRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 409);
        ResponseValidator.validateFieldExists(response, "message");
        ResponseValidator.validateFieldValue(response, "status", "error");
        Assert.assertTrue(response.jsonPath().getString("message").contains("already exists"),
                "Error message should indicate user already exists");
    }
    
    @Test(priority = 3)
    @Story("User Signup")
    @Description("Verify that signup fails with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testSignup_InvalidData() {
        // Arrange
        SignupRequest invalidRequest = AuthPayload.createInvalidSignupRequest();
        
        // Act
        Response response = authClient.signup(invalidRequest);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 409 || statusCode == 500, 
                "Status code should be 400, 409, or 500 for invalid data");
        ResponseValidator.validateFieldExists(response, "message");
    }
    
    @Test(priority = 4)
    @Story("User Signup")
    @Description("Verify that signup fails with missing required fields")
    @Severity(SeverityLevel.NORMAL)
    public void testSignup_MissingFields() {
        // Arrange
        SignupRequest missingFieldsRequest = AuthPayload.createSignupRequestWithMissingFields();
        
        // Act
        Response response = authClient.signup(missingFieldsRequest);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 500, 
                "Status code should be 400 or 500 for missing fields");
    }
    
    @Test(priority = 5, dependsOnMethods = "testSignup_Success")
    @Story("User Signin")
    @Description("Verify that user can login with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testSignin_Success() {
        // Arrange
        LoginRequest loginRequest = AuthPayload.createLoginRequest(testUserEmail, testUserPassword);
        
        // Act
        Response response = authClient.signin(loginRequest);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "jwt");
        ResponseValidator.validateFieldValue(response, "status", true);
        ResponseValidator.validateFieldValue(response, "message", "Login Success");
        ResponseValidator.validateResponseTime(response, 3000);
        
        String jwt = response.jsonPath().getString("jwt");
        Assert.assertNotNull(jwt, "JWT token should not be null");
    }
    
    @Test(priority = 6)
    @Story("User Signin")
    @Description("Verify that login fails with invalid password")
    @Severity(SeverityLevel.CRITICAL)
    public void testSignin_InvalidPassword() {
        // Arrange
        LoginRequest invalidLogin = AuthPayload.createLoginRequest(testUserEmail, "WrongPassword123");
        
        // Act
        Response response = authClient.signin(invalidLogin);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 401);
        ResponseValidator.validateFieldValue(response, "status", false);
        ResponseValidator.validateFieldValue(response, "message", "Invalid Username or Password");
    }
    
    @Test(priority = 7)
    @Story("User Signin")
    @Description("Verify that login fails with non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testSignin_NonExistentUser() {
        // Arrange
        LoginRequest nonExistentLogin = AuthPayload.createNonExistentUserLoginRequest();
        
        // Act
        Response response = authClient.signin(nonExistentLogin);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 401);
        ResponseValidator.validateFieldValue(response, "status", false);
    }
    
    @Test(priority = 8)
    @Story("User Signin")
    @Description("Verify that login fails with empty credentials")
    @Severity(SeverityLevel.NORMAL)
    public void testSignin_EmptyCredentials() {
        // Arrange
        LoginRequest emptyLogin = AuthPayload.createLoginRequest("", "");
        
        // Act
        Response response = authClient.signin(emptyLogin);
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 401, 
                "Status code should be 400 or 401 for empty credentials");
    }
    
    @Test(priority = 9, dependsOnMethods = "testSignin_Success")
    @Story("Token Management")
    @Description("Verify that authentication token can be refreshed")
    @Severity(SeverityLevel.NORMAL)
    public void testRefreshToken_Success() {
        // Arrange - Login first to get token
        LoginRequest loginRequest = AuthPayload.createLoginRequest(testUserEmail, testUserPassword);
        Response loginResponse = authClient.signin(loginRequest);
        String token = loginResponse.jsonPath().getString("jwt");
        
        // Create authenticated client
        AuthClient authenticatedClient = new AuthClient(
                requestSpec.header("Authorization", "Bearer " + token)
        );
        
        // Act
        Response response = authenticatedClient.refreshToken();
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "jwt");
        ResponseValidator.validateFieldValue(response, "status", true);
    }
    
    @Test(priority = 10)
    @Story("Token Management")
    @Description("Verify that token refresh fails without authentication")
    @Severity(SeverityLevel.NORMAL)
    public void testRefreshToken_Unauthorized() {
        // Act - Call without Authorization header
        Response response = authClient.refreshTokenWithoutAuth();
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403, 
                "Status code should indicate unauthorized access. Got: " + statusCode);
        
        // Validate error response structure
        ResponseValidator.validateFieldExists(response, "error");
        ResponseValidator.validateFieldExists(response, "message");
        
        // Verify message indicates token issue
        String message = response.jsonPath().getString("message");
        Assert.assertTrue(message.contains("JWT") || message.contains("token") || message.contains("Authorization"),
                "Error message should indicate authentication/token issue. Got: " + message);
    }
    
    @Test(priority = 11)
    @Story("Email Validation")
    @Description("Verify email availability check for new email")
    @Severity(SeverityLevel.NORMAL)
    public void testCheckEmail_Available() {
        // Arrange
        Map<String, String> emailPayload = AuthPayload.createEmailCheckPayload("newemail@example.com");
        
        // Act
        Response response = authClient.checkEmail(emailPayload);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "isAvailable");
        ResponseValidator.validateFieldValue(response, "isAvailable", true);
    }
    
    @Test(priority = 12, dependsOnMethods = "testSignup_Success")
    @Story("Email Validation")
    @Description("Verify email availability check for existing email")
    @Severity(SeverityLevel.NORMAL)
    public void testCheckEmail_NotAvailable() {
        // Arrange
        Map<String, String> emailPayload = AuthPayload.createEmailCheckPayload(testUserEmail);
        
        // Act
        Response response = authClient.checkEmail(emailPayload);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "isAvailable");
        ResponseValidator.validateFieldValue(response, "isAvailable", false);
    }
    
    @Test(priority = 13, dependsOnMethods = "testSignup_Success")
    @Story("User Retrieval")
    @Description("Verify getting user by email")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByEmail_Success() {
        // Act
        Response response = authClient.getUserByEmail(testUserEmail);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldExists(response, "id");
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
        
        // Store user ID for later tests
        testUserId = response.jsonPath().getLong("id");
    }
    
    @Test(priority = 14)
    @Story("User Retrieval")
    @Description("Verify getting user by non-existent email returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserByEmail_NotFound() {
        // Act
        Response response = authClient.getUserByEmail("nonexistent@example.com");
        
        // Assert
        ResponseValidator.validateStatusCode(response, 404);
    }
    
    @Test(priority = 15, dependsOnMethods = "testGetUserByEmail_Success")
    @Story("User Retrieval")
    @Description("Verify getting user by ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserById_Success() {
        // Act
        Response response = authClient.getUserById(testUserId);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateFieldValue(response, "id", testUserId.intValue());
        ResponseValidator.validateFieldValue(response, "email", testUserEmail);
    }
    
    @Test(priority = 16)
    @Story("User Retrieval")
    @Description("Verify getting user by non-existent ID returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserById_NotFound() {
        // Act
        Response response = authClient.getUserById(999999L);
        
        // Assert - Should throw exception or return 404
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 500, 
                "Status code should be 404 or 500 for non-existent user");
    }
    
    @Test(priority = 17)
    @Story("User Retrieval")
    @Description("Verify getting all users")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllUsers_Success() {
        // Act
        Response response = authClient.getAllUsers();
        
        // Assert
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseNotEmpty(response);
        
        // Verify it's a list and contains at least our test user
        Object users = response.jsonPath().getList("$");
        Assert.assertNotNull(users, "Users list should not be null");
    }
    
    @Test(priority = 19)
    @Story("Password Reset")
    @Description("Verify OTP send fails for non-existent email")
    @Severity(SeverityLevel.NORMAL)
    public void testSendOtp_EmailNotFound() {
        // Arrange
        Map<String, String> otpPayload = AuthPayload.createSendOtpPayload("nonexistent@example.com");
        
        // Act
        Response response = authClient.sendOtp(otpPayload);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateErrorMessage(response, "not found");
    }
    
    @Test(priority = 20)
    @Story("Password Reset")
    @Description("Verify OTP verification fails with invalid OTP")
    @Severity(SeverityLevel.NORMAL)
    public void testVerifyOtp_InvalidOtp() {
        // Arrange
        Map<String, String> verifyPayload = AuthPayload.createVerifyOtpPayload(testUserEmail, "000000");
        
        // Act
        Response response = authClient.verifyOtp(verifyPayload);
        
        // Assert
        ResponseValidator.validateStatusCode(response, 400);
        ResponseValidator.validateErrorMessage(response, "Invalid or expired OTP");
    }
    
    @Test(priority = 21)
    @Story("Boundary Conditions")
    @Description("Verify signup with maximum field lengths")
    @Severity(SeverityLevel.MINOR)
    public void testSignup_MaximumFieldLengths() {
        // Arrange
        String boundaryEmail = "verylongemail" + System.currentTimeMillis() + "@example.com";
        String boundaryPassword = "VeryComplexPassword@123456789";
        SignupRequest boundaryRequest = AuthPayload.createSignupRequest(
                "A".repeat(50), // Long first name
                "B".repeat(50), // Long last name
                boundaryEmail,
                boundaryPassword,
                "male"
        );
        
        // Act
        Response response = authClient.signup(boundaryRequest);
        
        // Assert - Should either succeed or fail with proper validation
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 201 || statusCode == 400 || statusCode == 409, 
                "Status code should be 201, 400, or 409");
        
        // Register user for cleanup if signup was successful
        if (statusCode == 201) {
            TestUserCleanupManager.registerUserForCleanup(boundaryEmail, boundaryPassword);
        }
    }
    
    @Test(priority = 22)
    @Story("Boundary Conditions")
    @Description("Verify email validation with invalid format")
    @Severity(SeverityLevel.MINOR)
    public void testGetUserByEmail_InvalidEmailFormat() {
        // Act
        Response response = authClient.getUserByEmail("invalid-email-format");
        
        // Assert
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 404 || statusCode == 500, 
                "Status code should indicate validation error");
    }
}
