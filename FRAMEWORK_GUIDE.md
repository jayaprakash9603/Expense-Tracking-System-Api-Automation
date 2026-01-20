# Expense Tracking System - API Automation Framework

A robust, maintainable API automation framework for testing the Expense Tracking System REST APIs.

## 🏗️ Framework Architecture

```
src/test/java/com/jaya/
├── base/                    # Base test classes
│   └── BaseTest.java        # Foundation class for all tests
├── clients/                 # API client classes
│   ├── BaseClient.java      # Base client with HTTP methods
│   ├── AuthClient.java      # Authentication API client
│   ├── UserClient.java      # User API client
│   └── ExpenseClient.java   # Expense API client
├── config/                  # Configuration management
│   └── ConfigManager.java   # Centralized configuration
├── constants/               # Constants and enums
│   ├── Endpoints.java       # All API endpoints
│   ├── HttpStatus.java      # HTTP status codes
│   ├── ErrorMessages.java   # Error message constants
│   └── TestGroups.java      # TestNG test groups
├── payloads/                # Request payload builders
│   └── AuthPayload.java     # Auth request payloads
├── pojo/                    # Data transfer objects
│   ├── SignupRequest.java
│   ├── LoginRequest.java
│   └── ...
├── tests/                   # Test classes
│   ├── AuthTest.java
│   ├── UserTest.java
│   └── ...
└── utils/                   # Utility classes
    ├── ResponseValidator.java  # Response validation
    ├── TokenManager.java       # JWT token management
    ├── TestDataFactory.java    # Test data generation
    └── JsonSchemaValidatorUtil.java
```

## 🔑 Key Features

### 1. **Single URL Configuration**

Configure the base URL **ONCE** in `config.properties`. No URL hardcoding in tests!

```properties
# config.properties
base.url.qa=http://localhost:9090
base.url.dev=http://localhost:8080
environment=qa
```

### 2. **Centralized Endpoint Management**

All endpoints defined in `Endpoints.java`:

```java
// No hardcoded URLs in tests!
import static com.jaya.constants.Endpoints.*;

authClient.post(AUTH.SIGNUP, signupRequest);
userClient.get(USER.PROFILE);
expenseClient.get(EXPENSE.BY_ID, expenseId);
```

### 3. **Clean Test Classes**

Tests focus on business logic, not setup:

```java
public class AuthTest extends BaseTest {

    private AuthClient authClient;

    @BeforeClass
    public void setupClient() {
        authClient = new AuthClient(getUnauthenticatedRequest());
    }

    @Test
    public void testSignup_Success() {
        SignupRequest request = TestDataFactory.createUniqueSignupRequest();
        Response response = authClient.signup(request);

        ResponseValidator.validateStatusCode(response, HttpStatus.CREATED);
        ResponseValidator.validateFieldExists(response, "jwt");
    }
}
```

### 4. **Automatic Token Management**

`TokenManager` handles JWT tokens automatically:

- Auto-refresh when expired
- Cached for performance
- Easy to clear for logout tests

### 5. **Comprehensive Validation Utils**

`ResponseValidator` provides fluent validation:

```java
ResponseValidator.validateStatusCode(response, 200);
ResponseValidator.validateFieldExists(response, "user.id");
ResponseValidator.validateFieldValue(response, "status", true);
ResponseValidator.validateResponseTime(response, 5000);
ResponseValidator.validateListNotEmpty(response, "expenses");
```

## 🚀 Getting Started

### Prerequisites

- Java 11+
- Maven 3.6+
- Running Expense Tracking System backend

### Running Tests

```bash
# Run all tests (default: QA environment)
mvn test

# Run tests in specific environment
mvn test -Denv=dev
mvn test -Denv=staging

# Run specific test groups
mvn test -Dgroups=smoke
mvn test -Dgroups=regression
mvn test -Dgroups=auth

# Run with custom credentials
mvn test -Dauth.username=test@email.com -Dauth.password=Test@123

# Disable verbose logging
mvn test -Denable.request.logging=false
```

### Generating Allure Reports

```bash
# Run tests and generate results
mvn test

# Generate and open report
mvn allure:serve

# Generate report only
mvn allure:report
```

## 📁 Configuration

### config.properties

| Property                  | Description             | Default               |
| ------------------------- | ----------------------- | --------------------- |
| `base.url.qa`             | QA environment URL      | http://localhost:9090 |
| `base.url.dev`            | DEV environment URL     | http://localhost:9090 |
| `environment`             | Active environment      | qa                    |
| `auth.username`           | Test user email         | -                     |
| `auth.password`           | Test user password      | -                     |
| `connection.timeout`      | Connection timeout (ms) | 5000                  |
| `response.timeout`        | Response timeout (ms)   | 10000                 |
| `enable.request.logging`  | Log requests            | true                  |
| `enable.response.logging` | Log responses           | true                  |

### Environment Override Priority

1. Command line (`-Dproperty=value`)
2. Environment variables
3. config.properties file

## 📝 Writing Tests

### 1. Create Test Class

```java
@Epic("Feature Name")
@Feature("Sub-feature")
public class MyTest extends BaseTest {

    private MyClient myClient;

    @BeforeClass
    public void setupClient() {
        // For authenticated endpoints
        myClient = new MyClient(getAuthenticatedRequest());

        // For public endpoints
        myClient = new MyClient(getUnauthenticatedRequest());
    }

    @Test(groups = {TestGroups.SMOKE, TestGroups.POSITIVE})
    @Story("User Story")
    @Description("Test description")
    @Severity(SeverityLevel.CRITICAL)
    public void testSomething() {
        // Arrange
        var request = TestDataFactory.createRequest();

        // Act
        Response response = myClient.doSomething(request);

        // Assert
        ResponseValidator.validateStatusCode(response, HttpStatus.OK);
    }
}
```

### 2. Create API Client

```java
public class MyClient extends BaseClient {

    public MyClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Do something")
    public Response doSomething(MyRequest request) {
        return post(Endpoints.MY.ENDPOINT, request);
    }
}
```

### 3. Add New Endpoints

```java
// In Endpoints.java
public static final class MY_FEATURE {
    public static final String BASE = "/api/my-feature";
    public static final String CREATE = BASE;
    public static final String BY_ID = BASE + "/{id}";
}
```

## 🧪 Test Groups

| Group        | Description          | Command                        |
| ------------ | -------------------- | ------------------------------ |
| `smoke`      | Critical path tests  | `mvn test -Dgroups=smoke`      |
| `regression` | Full regression      | `mvn test -Dgroups=regression` |
| `auth`       | Authentication tests | `mvn test -Dgroups=auth`       |
| `positive`   | Happy path tests     | `mvn test -Dgroups=positive`   |
| `negative`   | Error scenario tests | `mvn test -Dgroups=negative`   |

## 📊 Reporting

The framework uses Allure for comprehensive test reporting:

- Test execution timeline
- Step-by-step execution details
- Request/Response attachments
- Test categorization by severity, feature, story

## 🛠️ Best Practices

1. **Never hardcode URLs** - Use `Endpoints` constants
2. **Use TestDataFactory** - Generate unique test data
3. **Extend BaseTest** - Inherit common setup
4. **Use ResponseValidator** - Consistent validations
5. **Add Allure annotations** - Better reporting
6. **Group tests appropriately** - Easier selective execution
7. **Clean up test data** - Use @AfterClass for cleanup

## 📄 License

This project is licensed under the MIT License.
