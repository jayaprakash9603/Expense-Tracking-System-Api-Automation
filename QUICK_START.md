# REST Assured API Automation Framework - Quick Start Guide

## ✅ Framework Setup Complete!

Your professional-grade REST Assured API Automation Framework has been successfully created with the following structure:

## 📁 Project Structure

```
Api-Automation/
│
├── src/test/java/com/jaya/
│   ├── base/
│   │   └── BaseTest.java                    ✅ Base test setup with RequestSpecification
│   │
│   ├── config/
│   │   └── ConfigManager.java               ✅ Configuration management
│   │
│   ├── clients/
│   │   ├── BaseClient.java                  ✅ Base client with HTTP methods
│   │   └── ExpenseClient.java               ✅ Example: Expense API client
│   │
│   ├── payloads/
│   │   └── ExpensePayload.java              ✅ Example: Request payload builder
│   │
│   ├── pojo/
│   │   └── Expense.java                     ✅ Example: POJO model
│   │
│   ├── utils/
│   │   ├── ResponseValidator.java           ✅ Reusable validation methods
│   │   ├── TokenManager.java                ✅ Token caching & auto-refresh
│   │   └── JsonSchemaValidator.java         ✅ Schema validation utility
│   │
│   └── tests/
│       └── ExpenseTest.java                 ✅ Example: Test cases
│
├── src/test/resources/
│   ├── config.properties                    ✅ Environment configuration
│   ├── testng.xml                          ✅ TestNG suite configuration
│   ├── logback-test.xml                    ✅ Logging configuration
│   └── schemas/
│       └── expense-schema.json              ✅ Example: JSON schema
│
├── pom.xml                                  ✅ Maven dependencies configured
└── README.md                                ✅ Documentation
```

## 🚀 Getting Started

### Step 1: Update Configuration

Edit `src/test/resources/config.properties`:

```properties
base.url.qa=http://your-qa-api-url.com/api
auth.username=your-username
auth.password=your-password
```

### Step 2: Run Tests

```bash
# Run all tests in QA environment
mvn clean test -Denv=qa

# Run specific test class
mvn clean test -Dtest=ExpenseTest -Denv=qa

# Run with custom environment
mvn clean test -Denv=dev
```

### Step 3: Generate Allure Report

```bash
# Generate and view report
mvn allure:serve

# Or generate report only
mvn allure:report
```

## 📝 How to Add New API Tests

### 1. Create POJO (if needed)

```java
// src/test/java/com/jaya/pojo/User.java
public class User {
    private Long id;
    private String name;
    private String email;
    // getters, setters, constructors
}
```

### 2. Create Payload Builder

```java
// src/test/java/com/jaya/payloads/UserPayload.java
public class UserPayload {
    public static User createDefaultUser() {
        return new User("John Doe", "john@example.com");
    }
}
```

### 3. Create API Client

```java
// src/test/java/com/jaya/clients/UserClient.java
public class UserClient extends BaseClient {
    public UserClient(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    @Step("Create new user")
    public Response createUser(User user) {
        return post("/users", user);
    }

    @Step("Get user by ID: {userId}")
    public Response getUserById(Long userId) {
        return get("/users/{id}", userId);
    }
}
```

### 4. Write Test Cases

```java
// src/test/java/com/jaya/tests/UserTest.java
@Epic("User Management")
public class UserTest extends BaseTest {

    private UserClient userClient;

    @BeforeClass
    public void setupClient() {
        userClient = new UserClient(getAuthenticatedRequest());
    }

    @Test
    @Story("Create User")
    public void testCreateUser_Success() {
        User user = UserPayload.createDefaultUser();
        Response response = userClient.createUser(user);

        ResponseValidator.validateStatusCode(response, 201);
        ResponseValidator.validateFieldExists(response, "id");
    }
}
```

## 🎯 Framework Features

✅ **Layered Architecture** - Clean separation of concerns  
✅ **Configuration Management** - Environment-specific configs  
✅ **Token Management** - Auto-refresh & caching  
✅ **Reusable Validations** - DRY principle  
✅ **JSON Schema Validation** - Contract testing  
✅ **Allure Reporting** - Beautiful test reports  
✅ **CI/CD Ready** - Jenkins/GitHub Actions compatible  
✅ **TestNG Integration** - Powerful test execution  
✅ **Example Tests** - Reference implementation included

## 📚 Key Principles

1. ❌ No hardcoding of URLs, tokens, or credentials
2. ✅ All API calls through Client classes
3. ✅ Request bodies using POJOs
4. ✅ Single RequestSpecification in BaseTest
5. ✅ Centralized token management
6. ✅ Reusable validation methods
7. ✅ Minimal logging (log().ifValidationFails())
8. ✅ Independent, repeatable tests

## 🔧 Maven Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn clean test

# Run specific test
mvn test -Dtest=ExpenseTest

# Run with environment
mvn test -Denv=qa

# Generate Allure report
mvn allure:serve

# Skip tests
mvn clean install -DskipTests
```

## 📊 Test Execution Flow

```
BaseTest (setup)
    ↓
RequestSpecification created
    ↓
TokenManager initializes authentication
    ↓
Test uses Client class
    ↓
Client calls API
    ↓
Response returned
    ↓
ResponseValidator validates
    ↓
Results logged to Allure
```

## 🎓 Example Test Scenarios Included

✅ **Positive Tests**

- Create expense successfully
- Get expense by ID
- Update expense
- Delete expense

✅ **Negative Tests**

- Invalid data handling
- Not found scenarios
- Unauthorized access

✅ **Boundary Tests**

- Maximum values
- Empty/null values

## 🔐 Authentication Flow

1. `TokenManager.getToken()` called
2. Checks if token is cached and valid
3. If expired, calls login API automatically
4. Caches new token with expiry time
5. Returns valid token

## 📈 Next Steps

1. ✏️ Update `config.properties` with your API details
2. 🏗️ Create your API client classes
3. 📝 Define your POJO models
4. 🧪 Write your test cases
5. 🚀 Run tests and generate reports
6. 🔄 Integrate with CI/CD pipeline

## 💡 Tips

- Always extend `BaseTest` for your test classes
- Use `@Step` annotations for better Allure reporting
- Create payload builders for reusable test data
- Use ResponseValidator for assertions
- Keep tests independent and idempotent
- Run tests frequently during development

## 📞 Support

For questions or issues:

1. Check the README.md
2. Review example test cases
3. Consult framework documentation

---

**Framework Status:** ✅ Ready for Development

**Your framework is production-ready and follows industry best practices!**
