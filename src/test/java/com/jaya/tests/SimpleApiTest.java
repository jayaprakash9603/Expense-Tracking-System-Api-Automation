package com.jaya.tests;

import com.jaya.base.BaseTest;
import com.jaya.clients.AuthClient;
import com.jaya.pojo.SignupRequest;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * SimpleApiTest - Basic API test demonstrating the framework usage
 * 
 * This test class shows how to use the framework properly:
 * 1. Extend BaseTest (provides base URL automatically)
 * 2. Use API clients (AuthClient, UserClient, etc.)
 * 3. Use Endpoints constants (no hardcoded URLs)
 * 4. Use HttpStatus constants for assertions
 * 
 * NO URL CONFIGURATION NEEDED IN THIS CLASS!
 * 
 * @author Expense Tracking System Team
 */
public class SimpleApiTest extends BaseTest {
    
    private AuthClient authClient;
    
    @BeforeClass
    public void setupClient() {
        // Initialize the auth client with unauthenticated request spec
        // Base URL is already configured in BaseTest
        authClient = new AuthClient(getUnauthenticatedRequest());
    }
    
    @Test
    public void testSimpleSignup() {
        // Create signup request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setEmail("test" + System.currentTimeMillis() + "@example.com");
        signupRequest.setPassword("Test@123");
        signupRequest.setGender("male");
        
        // Execute signup using the client
        Response response = authClient.signup(signupRequest);
        
        // Log results
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
    }
}
