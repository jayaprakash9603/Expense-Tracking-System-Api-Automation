package com.jaya.clients;

import com.jaya.pojo.Expense;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ExpenseClient extends BaseClient {
    
    private static final String EXPENSES_ENDPOINT = "/expenses";
    private static final String EXPENSE_BY_ID_ENDPOINT = "/expenses/{id}";
    private static final String EXPENSES_BY_USER_ENDPOINT = "/expenses/user/{userId}";
    
    public ExpenseClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }
    
    @Step("Create new expense: {expense}")
    public Response createExpense(Expense expense) {
        return post(EXPENSES_ENDPOINT, expense);
    }
    
    @Step("Get all expenses")
    public Response getAllExpenses() {
        return get(EXPENSES_ENDPOINT);
    }
    
    @Step("Get expense by ID: {expenseId}")
    public Response getExpenseById(Long expenseId) {
        return get(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)));
    }
    
    @Step("Get expenses for user ID: {userId}")
    public Response getExpensesByUserId(Long userId) {
        return get(EXPENSES_BY_USER_ENDPOINT.replace("{userId}", String.valueOf(userId)));
    }
    
    @Step("Update expense ID {expenseId} with data: {expense}")
    public Response updateExpense(Long expenseId, Expense expense) {
        return put(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)), expense);
    }
    
    @Step("Delete expense ID: {expenseId}")
    public Response deleteExpense(Long expenseId) {
        return delete(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)));
    }
    
    @Step("Partially update expense ID {expenseId}")
    public Response partialUpdateExpense(Long expenseId, Expense expense) {
        return patch(EXPENSE_BY_ID_ENDPOINT.replace("{id}", String.valueOf(expenseId)), expense);
    }
}
