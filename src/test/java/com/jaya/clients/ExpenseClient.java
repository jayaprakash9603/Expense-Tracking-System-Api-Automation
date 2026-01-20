package com.jaya.clients;

import com.jaya.constants.Endpoints;
import com.jaya.pojo.Expense;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class ExpenseClient extends BaseClient {

    public ExpenseClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Create expense")
    public Response createExpense(Expense expense) {
        return post(Endpoints.EXPENSE.CREATE, expense);
    }

    @Step("Get all expenses")
    public Response getAllExpenses() {
        return get(Endpoints.EXPENSE.ALL);
    }

    @Step("Get expense by ID: {expenseId}")
    public Response getExpenseById(Long expenseId) {
        return getWithPathParam(Endpoints.EXPENSE.BY_ID, "id", expenseId);
    }

    @Step("Get expenses for user: {userId}")
    public Response getExpensesByUserId(Long userId) {
        return getWithPathParam(Endpoints.EXPENSE.BY_USER, "userId", userId);
    }

    @Step("Update expense: {expenseId}")
    public Response updateExpense(Long expenseId, Expense expense) {
        return putWithPathParam(Endpoints.EXPENSE.UPDATE, "id", expenseId, expense);
    }

    @Step("Delete expense: {expenseId}")
    public Response deleteExpense(Long expenseId) {
        return deleteWithPathParam(Endpoints.EXPENSE.DELETE, "id", expenseId);
    }

    @Step("Partial update expense: {expenseId}")
    public Response partialUpdateExpense(Long expenseId, Expense expense) {
        return patchWithPathParam(Endpoints.EXPENSE.BY_ID, "id", expenseId, expense);
    }

    @Step("Get expenses by category: {categoryId}")
    public Response getExpensesByCategory(Long categoryId) {
        return getWithPathParam(Endpoints.EXPENSE.BY_CATEGORY, "categoryId", categoryId);
    }

    @Step("Get expenses by date range")
    public Response getExpensesByDateRange(String startDate, String endDate) {
        return getWithQueryParams(Endpoints.EXPENSE.BY_DATE_RANGE, Map.of("startDate", startDate, "endDate", endDate));
    }

    @Step("Get expense summary")
    public Response getExpenseSummary() {
        return get(Endpoints.EXPENSE.SUMMARY);
    }

    @Step("Filter expenses")
    public Response filterExpenses(Map<String, Object> filterCriteria) {
        return getWithQueryParams(Endpoints.EXPENSE.FILTER, filterCriteria);
    }

    @Step("Export expenses")
    public Response exportExpenses() {
        return get(Endpoints.EXPENSE.EXPORT);
    }
}
