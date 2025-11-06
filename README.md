# REST Assured API Automation Framework

## Overview

This is a professional-grade REST Assured API Automation Framework following a layered, clean, and scalable architecture.

## Framework Architecture

```
src/test/java/
├── base/          → Base setup files (BaseTest.java)
├── config/        → Configuration handling (ConfigManager.java)
├── clients/       → API Client classes (ExpenseClient.java, UserClient.java, etc.)
├── payloads/      → Request body builders (ExpensePayload.java, etc.)
├── pojo/          → Request/Response model classes
├── utils/         → Shared utilities (ResponseValidator, TokenManager, etc.)
└── tests/         → Test files (TestNG-based)

src/test/resources/
├── config.properties  → Environment and configuration settings
├── schemas/           → JSON schema files for validation
└── testng.xml         → TestNG suite configuration
```

## Key Features

- **Layered Architecture**: Separation of concerns with dedicated layers for clients, payloads, POJOs, and utilities
- **Configuration Management**: Centralized configuration with environment-specific settings
- **Token Management**: Automatic token caching and refresh mechanism
- **Reusable Validations**: Common validation methods in ResponseValidator
- **JSON Schema Validation**: Support for schema-based validation
- **Allure Reporting**: Integrated Allure reports for better test visualization
- **CI/CD Ready**: Compatible with Jenkins and GitHub Actions

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- REST Assured
- TestNG
- Allure (for reporting)

## Configuration

Edit `src/test/resources/config.properties` to set:

- Base URLs for different environments (qa, dev, prod)
- Authentication credentials
- Timeout values
- Logging preferences

## Running Tests

### Run all tests in QA environment:

```bash
mvn clean test -Denv=qa
```

### Run all tests in DEV environment:

```bash
mvn clean test -Denv=dev
```

### Run specific test class:

```bash
mvn clean test -Dtest=ExpenseTest -Denv=qa
```

### Run with TestNG XML:

```bash
mvn clean test -DsuiteXmlFile=testng.xml -Denv=qa
```

## Generating Allure Reports

### Generate and open report:

```bash
mvn allure:serve
```

### Generate report only:

```bash
mvn allure:report
```

## Coding Standards

1. ✅ No hardcoding of URLs, tokens, or credentials
2. ✅ All API calls through Client classes
3. ✅ Request bodies using POJOs, not inline JSON
4. ✅ RequestSpecification created once in BaseTest
5. ✅ Token handling centralized in TokenManager
6. ✅ Validations using ResponseValidator methods
7. ✅ Minimal logging (log().ifValidationFails())
8. ✅ JSON Schema validation for key APIs
9. ✅ Tests are independent, repeatable, and idempotent

## Project Structure Benefits

- **Maintainability**: Easy to update and extend
- **Readability**: Clear separation of concerns
- **Reusability**: Common utilities and base classes
- **Scalability**: Easy to add new modules/features
- **Testability**: Independent and repeatable tests

## Implemented API Modules

### 1. Authentication API (`AuthTest.java`)

The framework includes comprehensive test coverage for authentication endpoints:

#### **Endpoints Covered:**

- `POST /auth/signup` - User registration
- `POST /auth/signin` - User login
- `POST /auth/refresh-token` - Token refresh
- `GET /auth/{userId}` - Get user by ID
- `GET /auth/email?email=` - Get user by email
- `GET /auth/all-users` - Get all users
- `POST /auth/check-email` - Check email availability
- `POST /auth/send-otp` - Send OTP for password reset
- `POST /auth/verify-otp` - Verify OTP
- `PATCH /auth/reset-password` - Reset password

#### **Test Scenarios (22 tests):**

✅ **Positive Tests:**

- Successful user registration with JWT token generation
- Successful login with valid credentials
- Token refresh with valid authentication
- Get user by email and ID
- Email availability check
- OTP send functionality

✅ **Negative Tests:**

- Duplicate email registration (409 Conflict)
- Invalid login credentials (401 Unauthorized)
- Non-existent user login
- Empty credentials validation
- Unauthorized token refresh
- OTP send to non-existent email
- Invalid OTP verification

✅ **Boundary Tests:**

- Maximum field lengths in signup
- Invalid email format validation

#### **POJOs Used:**

- `SignupRequest` - User registration payload
- `LoginRequest` - User authentication payload
- `AuthResponse` - Authentication response with JWT
- `User` - User entity model

#### **Payload Builders:**

- `AuthPayload.createDefaultSignupRequest()`
- `AuthPayload.createDefaultLoginRequest()`
- `AuthPayload.createEmailCheckPayload(email)`
- `AuthPayload.createSendOtpPayload(email)`
- `AuthPayload.createVerifyOtpPayload(email, otp)`
- `AuthPayload.createPasswordResetPayload(email, password)`

### 2. User Management API (`UserTest.java`)

Comprehensive test coverage for user management operations:

#### **Endpoints Covered:**

- `GET /api/user/profile` - Get current user profile from JWT
- `GET /api/user/email?email=` - Get user by email
- `GET /api/user/{id}` - Get user by ID (with authorization)
- `PUT /api/user` - Update user profile
- `DELETE /api/user/{id}` - Delete user account
- `POST /api/user/{userId}/roles/{roleId}` - Add role to user (Admin only)
- `DELETE /api/user/{userId}/roles/{roleId}` - Remove role from user (Admin only)

#### **Test Scenarios (23 tests):**

✅ **Positive Tests:**

- Get user profile from JWT token
- Get user by email with authentication
- Get own profile by ID
- Update user profile (full name, mobile)
- Update with password change
- Partial updates (name only, mobile only)
- Delete own account

✅ **Negative Tests:**

- Unauthorized profile access (401/403)
- Get non-existent user (404)
- Invalid email format
- Access other user's profile without admin role (403)
- Unauthorized update and delete operations

✅ **Boundary Tests:**

- Maximum field lengths in update
- Zero and negative ID validation
- Very large ID values (Long.MAX_VALUE)

✅ **Authorization Tests:**

- Role management without admin privileges
- Own account vs other user access control

#### **POJOs Used:**

- `User` - User entity with roles
- `UserUpdateRequest` - Profile update payload

#### **Payload Builders:**

- `UserPayload.createDefaultUpdateRequest()`
- `UserPayload.createUpdateRequest(name, mobile)`
- `UserPayload.createUpdateRequestWithPassword(name, mobile, password)`
- `UserPayload.createNameOnlyUpdateRequest(name)`
- `UserPayload.createMobileOnlyUpdateRequest(mobile)`

### 3. Example Expense API (`ExpenseTest.java`)

Reference implementation showing framework usage:

#### **Endpoints Covered:**

- `POST /expenses` - Create expense
- `GET /expenses` - Get all expenses
- `GET /expenses/{id}` - Get expense by ID
- `PUT /expenses/{id}` - Update expense
- `DELETE /expenses/{id}` - Delete expense

## JSON Schema Validation

The framework includes JSON schemas for contract testing:

- `user-schema.json` - User entity validation
- `auth-response-schema.json` - Authentication response validation
- `expense-schema.json` - Expense entity validation

**Usage Example:**

```java
JsonSchemaValidator.validateSchema(response, "user-schema.json");
```

## Token Management

The framework implements automatic token management:

1. **Automatic Refresh**: Tokens are automatically refreshed before expiry
2. **Caching**: Tokens are cached to avoid unnecessary login calls
3. **Thread-Safe**: Token manager is designed for parallel test execution
4. **Configurable**: Token validity period can be configured

**Configuration in `config.properties`:**

```properties
auth.username=your-email@example.com
auth.password=YourPassword@123
```

## Running Specific Test Suites

### Run Authentication Tests Only:

```bash
mvn clean test -Dtest=AuthTest -Denv=qa
```

### Run User Management Tests Only:

```bash
mvn clean test -Dtest=UserTest -Denv=qa
```

### Run All Tests:

```bash
mvn clean test -Denv=qa
```

## Test Execution Summary

| Module            | Test Class         | Test Count   | Coverage                            |
| ----------------- | ------------------ | ------------ | ----------------------------------- |
| Authentication    | `AuthTest.java`    | 22           | Signup, Signin, OTP, User Retrieval |
| User Management   | `UserTest.java`    | 23           | Profile, Update, Delete, Roles      |
| Expense (Example) | `ExpenseTest.java` | 10           | CRUD Operations                     |
| **Total**         | **3 Classes**      | **55 Tests** | **Complete API Coverage**           |

## Allure Report Features

The framework generates comprehensive Allure reports with:

- ✅ Request/Response logs for each API call
- ✅ Step-by-step test execution visualization
- ✅ Categorization by Epic, Feature, and Story
- ✅ Severity levels for test prioritization
- ✅ Detailed error messages and stack traces
- ✅ Test execution timeline
- ✅ Success/Failure trends

## CI/CD Integration

### Jenkins Pipeline Example:

```groovy
pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'your-repo-url'
            }
        }
        stage('Run Tests') {
            steps {
                sh 'mvn clean test -Denv=qa'
            }
        }
        stage('Generate Report') {
            steps {
                allure includeProperties: false, jdk: '',
                       results: [[path: 'target/allure-results']]
            }
        }
    }
}
```

### GitHub Actions Example:

```yaml
name: API Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: "17"
      - name: Run Tests
        run: mvn clean test -Denv=qa
      - name: Generate Allure Report
        uses: simple-elf/allure-report-action@master
        with:
          allure_results: target/allure-results
```

## Framework Extension Guide

### Adding a New API Module:

1. **Create POJOs** (`src/test/java/com/jaya/pojo/`)

```java
public class Budget {
    private Long id;
    private String name;
    private Double amount;
    // getters, setters, constructors
}
```

2. **Create Payload Builder** (`src/test/java/com/jaya/payloads/`)

```java
public class BudgetPayload {
    public static Budget createDefaultBudget() {
        return new Budget("Monthly Budget", 5000.0);
    }
}
```

3. **Create API Client** (`src/test/java/com/jaya/clients/`)

```java
public class BudgetClient extends BaseClient {
    public BudgetClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Create budget")
    public Response createBudget(Budget budget) {
        return post("/budgets", budget);
    }
}
```

4. **Create Test Class** (`src/test/java/com/jaya/tests/`)

```java
@Epic("Budget Management")
public class BudgetTest extends BaseTest {
    private BudgetClient budgetClient;

    @BeforeClass
    public void setupClient() {
        budgetClient = new BudgetClient(getAuthenticatedRequest());
    }

    @Test
    public void testCreateBudget_Success() {
        Budget budget = BudgetPayload.createDefaultBudget();
        Response response = budgetClient.createBudget(budget);
        ResponseValidator.validateStatusCode(response, 201);
    }
}
```

5. **Add JSON Schema** (`src/test/resources/schemas/budget-schema.json`)

## Best Practices

1. **Always use POJOs**: Never use inline JSON strings
2. **Centralize validations**: Use `ResponseValidator` for all assertions
3. **Independent tests**: Each test should be able to run independently
4. **Clean up**: Delete created test data in test teardown
5. **Meaningful names**: Use descriptive test method names
6. **Allure annotations**: Add `@Step`, `@Description`, `@Story` for better reports
7. **Handle authentication**: Use `getAuthenticatedRequest()` for protected endpoints
8. **Error handling**: Expect and validate error scenarios

## Troubleshooting

### Common Issues:

**1. Token Refresh Failures:**

- Verify credentials in `config.properties`
- Check base URL configuration
- Ensure `/auth/signin` endpoint is accessible

**2. Test Failures:**

- Check API availability
- Verify test data doesn't conflict with existing data
- Review Allure reports for detailed error information

**3. Schema Validation Failures:**

- Update JSON schemas to match actual API responses
- Check for nullable fields
- Verify data types in schema definitions

## Next Steps

1. ✅ **Authentication API** - Complete
2. ✅ **User Management API** - Complete
3. ⏳ **Add more API modules** (Budget, Expense, Groups, etc.)
4. ⏳ **Configure CI/CD pipeline**
5. ⏳ **Add data-driven testing with TestNG DataProviders**
6. ⏳ **Implement API mocking for offline testing**

## Support

For issues or questions:

- Review test execution logs
- Check Allure reports for detailed information
- Refer to `QUICK_START.md` for setup guide
- Contact the automation team
