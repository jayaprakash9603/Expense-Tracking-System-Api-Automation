package com.jaya.payloads;

import com.jaya.pojo.Expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ExpensePayload - Builder class for creating Expense request payloads
 * Provides helper methods to create test data
 */
public class ExpensePayload {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Create a default expense payload
     * @return Expense object with default values
     */
    public static Expense createDefaultExpense() {
        return new Expense(
                "Test Expense",
                100.50,
                "Food",
                getCurrentDate(),
                1L
        );
    }
    
    /**
     * Create expense with custom values
     * @param description Expense description
     * @param amount Expense amount
     * @param category Expense category
     * @return Expense object
     */
    public static Expense createExpense(String description, Double amount, String category) {
        return new Expense(
                description,
                amount,
                category,
                getCurrentDate(),
                1L
        );
    }
    
    /**
     * Create expense with all custom values
     * @param description Expense description
     * @param amount Expense amount
     * @param category Expense category
     * @param date Expense date
     * @param userId User ID
     * @return Expense object
     */
    public static Expense createExpense(String description, Double amount, String category, String date, Long userId) {
        return new Expense(description, amount, category, date, userId);
    }
    
    /**
     * Create expense with invalid data (for negative testing)
     * @return Expense object with invalid data
     */
    public static Expense createInvalidExpense() {
        return new Expense(
                "", // Empty description
                -100.0, // Negative amount
                null, // Null category
                getCurrentDate(),
                null // Null userId
        );
    }
    
    /**
     * Create expense with boundary values
     * @return Expense object with boundary values
     */
    public static Expense createBoundaryExpense() {
        return new Expense(
                "A".repeat(255), // Maximum length description
                999999.99, // Maximum amount
                "Category",
                getCurrentDate(),
                Long.MAX_VALUE
        );
    }
    
    /**
     * Get current date in yyyy-MM-dd format
     * @return Current date string
     */
    private static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
}
