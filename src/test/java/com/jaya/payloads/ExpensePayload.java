package com.jaya.payloads;

import com.jaya.pojo.Expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpensePayload {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static Expense createDefaultExpense() {
        return new Expense(
                "Test Expense",
                100.50,
                "Food",
                getCurrentDate(),
                1L
        );
    }
    
    public static Expense createExpense(String description, Double amount, String category) {
        return new Expense(
                description,
                amount,
                category,
                getCurrentDate(),
                1L
        );
    }
    
    public static Expense createExpense(String description, Double amount, String category, String date, Long userId) {
        return new Expense(description, amount, category, date, userId);
    }
    
    public static Expense createInvalidExpense() {
        return new Expense(
                "",
                -100.0,
                null,
                getCurrentDate(),
                null
        );
    }
    
    public static Expense createBoundaryExpense() {
        return new Expense(
                "A".repeat(255),
                999999.99,
                "Category",
                getCurrentDate(),
                Long.MAX_VALUE
        );
    }
    
    private static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
}
