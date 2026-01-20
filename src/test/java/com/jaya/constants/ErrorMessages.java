package com.jaya.constants;

public final class ErrorMessages {
    
    private ErrorMessages() {}
    
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String TOKEN_EXPIRED = "Token has expired";
    public static final String TOKEN_INVALID = "Invalid token";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String FORBIDDEN_ACCESS = "You don't have permission to access this resource";
    
    public static final String INVALID_EMAIL = "Invalid email format";
    public static final String INVALID_PASSWORD = "Password does not meet requirements";
    public static final String REQUIRED_FIELD_MISSING = "Required field is missing";
    public static final String INVALID_ID = "Invalid ID provided";
    
    public static final String EXPENSE_NOT_FOUND = "Expense not found";
    public static final String INVALID_EXPENSE_AMOUNT = "Invalid expense amount";
    public static final String INVALID_DATE_RANGE = "Invalid date range";
    
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CATEGORY_ALREADY_EXISTS = "Category already exists";
    public static final String CANNOT_DELETE_DEFAULT_CATEGORY = "Cannot delete default category";
    
    public static final String BUDGET_NOT_FOUND = "Budget not found";
    public static final String BUDGET_EXCEEDED = "Budget limit exceeded";
    
    public static final String STATUS_CODE_MISMATCH = "Status code mismatch";
    public static final String RESPONSE_BODY_NULL = "Response body should not be null";
    public static final String JWT_TOKEN_NULL = "JWT token should not be null";
    public static final String JWT_TOKEN_EMPTY = "JWT token should not be empty";
    public static final String FIELD_NOT_FOUND = "Expected field not found in response";
    public static final String FIELD_VALUE_MISMATCH = "Field value mismatch";
}
