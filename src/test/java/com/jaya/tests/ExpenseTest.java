//package com.jaya.tests;
//
//import com.jaya.base.BaseTest;
//import com.jaya.clients.ExpenseClient;
//import com.jaya.payloads.ExpensePayload;
//import com.jaya.pojo.Expense;
//import com.jaya.utils.ResponseValidator;
//import io.qameta.allure.*;
//import io.restassured.response.Response;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
///**
// * ExpenseTest - Sample test class demonstrating framework usage
// * Contains example test cases for Expense API
// */
//@Epic("Expense Management")
//@Feature("Expense CRUD Operations")
//public class ExpenseTest extends BaseTest {
//
//    private ExpenseClient expenseClient;
//    private Long createdExpenseId;
//
//    @BeforeClass
//    public void setupClient() {
//        super.setup(); // Ensure parent setup runs first
//        expenseClient = new ExpenseClient(getAuthenticatedRequest());
//    }
//
//    @Test(priority = 1)
//    @Story("Create Expense")
//    @Description("Verify that a new expense can be created successfully")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testCreateExpense_Success() {
//        // Arrange
//        Expense expense = ExpensePayload.createDefaultExpense();
//
//        // Act
//        Response response = expenseClient.createExpense(expense);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 201);
//        ResponseValidator.validateFieldExists(response, "id");
//        ResponseValidator.validateFieldValue(response, "description", expense.getDescription());
//        ResponseValidator.validateFieldValue(response, "amount", expense.getAmount());
//        ResponseValidator.validateContentType(response, "application/json");
//
//        // Store created expense ID for cleanup
//        createdExpenseId = response.jsonPath().getLong("id");
//    }
//
//    @Test(priority = 2)
//    @Story("Create Expense")
//    @Description("Verify that expense creation fails with invalid data")
//    @Severity(SeverityLevel.NORMAL)
//    public void testCreateExpense_InvalidData() {
//        // Arrange
//        Expense invalidExpense = ExpensePayload.createInvalidExpense();
//
//        // Act
//        Response response = expenseClient.createExpense(invalidExpense);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 400);
//        ResponseValidator.validateErrorMessage(response, "validation");
//    }
//
//    @Test(priority = 3, dependsOnMethods = "testCreateExpense_Success")
//    @Story("Read Expense")
//    @Description("Verify that an expense can be retrieved by ID")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testGetExpenseById_Success() {
//        // Act
//        Response response = expenseClient.getExpenseById(createdExpenseId);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 200);
//        ResponseValidator.validateFieldValue(response, "id", createdExpenseId);
//        ResponseValidator.validateResponseTime(response, 2000);
//    }
//
//    @Test(priority = 4)
//    @Story("Read Expense")
//    @Description("Verify that retrieving non-existent expense returns 404")
//    @Severity(SeverityLevel.NORMAL)
//    public void testGetExpenseById_NotFound() {
//        // Arrange
//        Long nonExistentId = 999999L;
//
//        // Act
//        Response response = expenseClient.getExpenseById(nonExistentId);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 404);
//    }
//
//    @Test(priority = 5)
//    @Story("Read Expense")
//    @Description("Verify that all expenses can be retrieved")
//    @Severity(SeverityLevel.NORMAL)
//    public void testGetAllExpenses_Success() {
//        // Act
//        Response response = expenseClient.getAllExpenses();
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 200);
//        ResponseValidator.validateResponseNotEmpty(response);
//        ResponseValidator.validateContentType(response, "application/json");
//    }
//
//    @Test(priority = 6, dependsOnMethods = "testCreateExpense_Success")
//    @Story("Update Expense")
//    @Description("Verify that an expense can be updated successfully")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testUpdateExpense_Success() {
//        // Arrange
//        Expense updatedExpense = ExpensePayload.createExpense(
//                "Updated Expense",
//                200.75,
//                "Transport"
//        );
//
//        // Act
//        Response response = expenseClient.updateExpense(createdExpenseId, updatedExpense);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 200);
//        ResponseValidator.validateFieldValue(response, "description", "Updated Expense");
//        ResponseValidator.validateFieldValue(response, "amount", 200.75);
//    }
//
//    @Test(priority = 7)
//    @Story("Update Expense")
//    @Description("Verify that updating non-existent expense returns 404")
//    @Severity(SeverityLevel.NORMAL)
//    public void testUpdateExpense_NotFound() {
//        // Arrange
//        Long nonExistentId = 999999L;
//        Expense expense = ExpensePayload.createDefaultExpense();
//
//        // Act
//        Response response = expenseClient.updateExpense(nonExistentId, expense);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 404);
//    }
//
//    @Test(priority = 8, dependsOnMethods = "testCreateExpense_Success")
//    @Story("Delete Expense")
//    @Description("Verify that an expense can be deleted successfully")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testDeleteExpense_Success() {
//        // Act
//        Response response = expenseClient.deleteExpense(createdExpenseId);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 204);
//
//        // Verify deletion
//        Response getResponse = expenseClient.getExpenseById(createdExpenseId);
//        ResponseValidator.validateStatusCode(getResponse, 404);
//    }
//
//    @Test(priority = 9)
//    @Story("Delete Expense")
//    @Description("Verify that deleting non-existent expense returns 404")
//    @Severity(SeverityLevel.NORMAL)
//    public void testDeleteExpense_NotFound() {
//        // Arrange
//        Long nonExistentId = 999999L;
//
//        // Act
//        Response response = expenseClient.deleteExpense(nonExistentId);
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 404);
//    }
//
//    @Test(priority = 10)
//    @Story("Authorization")
//    @Description("Verify that unauthorized request returns 401")
//    @Severity(SeverityLevel.CRITICAL)
//    public void testUnauthorizedAccess() {
//        // Arrange - Create client without authentication
//        ExpenseClient unauthClient = new ExpenseClient(getUnauthenticatedRequest());
//
//        // Act
//        Response response = unauthClient.getAllExpenses();
//
//        // Assert
//        ResponseValidator.validateStatusCode(response, 401);
//    }
//}
