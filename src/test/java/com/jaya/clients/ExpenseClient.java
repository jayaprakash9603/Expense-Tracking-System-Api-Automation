package com.jaya.clients;

import com.jaya.pojo.Expense;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * ExpenseClient - API client for Expense-related endpoints
 * Encapsulates all HTTP calls for expense operations
 */
public class ExpenseClient extends BaseClient {
    
    private static final String EXPENSES_ENDPOINT = "/expenses";
    private static final String EXPENSE_BY_ID_ENDPOINT = "/expenses/{id}";
    private static final String EXPENSES_BY_USER_ENDPOINT = "/expenses/user/{userId}";
    
    /**
     * Constructor
     * @param requestSpec RequestSpecification instance
     */
    public ExpenseClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    /**
     * Create a new expense
     * @param expense Expense object
     * @return Response object
     */
    @Step("Create new expense: {expense}")
    public Response createExpense(Expense expense) {
        return post(EXPENSES_ENDPOINT, expense);
    }
    
    /**
     * Get all expenses
     * @return Response object
     */
    @Step("Get all expenses")
    public Response getAllExpenses() {
        return get(EXPENSES_ENDPOINT);
    }
    
    /**
     * Get expense by ID
     * @param expenseId Expense ID
     * @return Response object
     */
    @Step("Get expense by ID: {expenseId}")
    public Response getExpenseById(Long expenseId) {
        return get(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)));
    }
    
    /**
     * Get expenses by user ID
     * @param userId User ID
     * @return Response object
     */
    @Step("Get expenses for user ID: {userId}")
    public Response getExpensesByUserId(Long userId) {
        return get(EXPENSES_BY_USER_ENDPOINT.replace("{userId}", String.valueOf(userId)));
    }
    
    /**
     * Update an existing expense
     * @param expenseId Expense ID
     * @param expense Updated expense object
     * @return Response object
     */
    @Step("Update expense ID {expenseId} with data: {expense}")
    public Response updateExpense(Long expenseId, Expense expense) {
        return put(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)), expense);
    }
    
    /**
     * Delete an expense
     * @param expenseId Expense ID
     * @return Response object
     */
    @Step("Delete expense ID: {expenseId}")
    public Response deleteExpense(Long expenseId) {
        return delete(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)));
    }
    
    /**
     * Partially update an expense
     * @param expenseId Expense ID
     * @param expense Partial expense data
     * @return Response object
     */
    @Step("Partially update expense ID {expenseId}")
    public Response partialUpdateExpense(Long expenseId, Expense expense) {
        return patch(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)), expense);
    }
}
