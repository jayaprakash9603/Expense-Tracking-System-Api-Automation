package com.jaya.constants;

public final class Endpoints {
    
    private Endpoints() {}
    
    public static final class AUTH {
        private AUTH() {}
        public static final String BASE = "/auth";
        public static final String SIGNUP = BASE + "/signup";
        public static final String SIGNIN = BASE + "/signin";
        public static final String REFRESH_TOKEN = BASE + "/refresh-token";
        public static final String USER_BY_ID = BASE + "/user/{userId}";
        public static final String USER_BY_EMAIL = BASE + "/email";
        public static final String ALL_USERS = BASE + "/all-users";
        public static final String CHECK_EMAIL = BASE + "/check-email";
        public static final String SEND_OTP = BASE + "/send-otp";
        public static final String VERIFY_OTP = BASE + "/verify-otp";
        public static final String RESET_PASSWORD = BASE + "/reset-password";
        public static final String FORGOT_PASSWORD = BASE + "/forgot-password";
        public static final String LOGOUT = BASE + "/logout";
    }
    
    public static final class USER {
        private USER() {}
        public static final String BASE = "/api/user";
        public static final String PROFILE = BASE + "/profile";
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_EMAIL = BASE + "/email";
        public static final String UPDATE = BASE;
        public static final String DELETE = BASE + "/{id}";
        public static final String ALL = BASE + "/all";
        public static final String SEARCH = BASE + "/search";
    }
    
    public static final class EXPENSE {
        private EXPENSE() {}
        public static final String BASE = "/api/expense";
        public static final String CREATE = BASE;
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_USER = BASE + "/user/{userId}";
        public static final String UPDATE = BASE + "/{id}";
        public static final String DELETE = BASE + "/{id}";
        public static final String ALL = BASE + "/all";
        public static final String FILTER = BASE + "/filter";
        public static final String SUMMARY = BASE + "/summary";
        public static final String BY_CATEGORY = BASE + "/category/{categoryId}";
        public static final String BY_DATE_RANGE = BASE + "/date-range";
        public static final String EXPORT = BASE + "/export";
    }
    
    public static final class CATEGORY {
        private CATEGORY() {}
        public static final String BASE = "/api/category";
        public static final String CREATE = BASE;
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_USER = BASE + "/user/{userId}";
        public static final String UPDATE = BASE + "/{id}";
        public static final String DELETE = BASE + "/{id}";
        public static final String ALL = BASE + "/all";
        public static final String DEFAULT = BASE + "/default";
    }
    
    public static final class BUDGET {
        private BUDGET() {}
        public static final String BASE = "/api/budget";
        public static final String CREATE = BASE;
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_USER = BASE + "/user/{userId}";
        public static final String UPDATE = BASE + "/{id}";
        public static final String DELETE = BASE + "/{id}";
        public static final String ALL = BASE + "/all";
        public static final String STATUS = BASE + "/status";
        public static final String ALERTS = BASE + "/alerts";
    }
    
    public static final class ANALYTICS {
        private ANALYTICS() {}
        public static final String BASE = "/api/analytics";
        public static final String DASHBOARD = BASE + "/dashboard";
        public static final String EXPENSE_TRENDS = BASE + "/expense-trends";
        public static final String CATEGORY_BREAKDOWN = BASE + "/category-breakdown";
        public static final String MONTHLY_SUMMARY = BASE + "/monthly-summary";
        public static final String YEARLY_SUMMARY = BASE + "/yearly-summary";
        public static final String COMPARISON = BASE + "/comparison";
    }
    
    public static final class BILL {
        private BILL() {}
        public static final String BASE = "/api/bill";
        public static final String CREATE = BASE;
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_USER = BASE + "/user/{userId}";
        public static final String UPDATE = BASE + "/{id}";
        public static final String DELETE = BASE + "/{id}";
        public static final String ALL = BASE + "/all";
        public static final String UPCOMING = BASE + "/upcoming";
        public static final String OVERDUE = BASE + "/overdue";
        public static final String MARK_PAID = BASE + "/{id}/mark-paid";
    }
    
    public static final class FRIENDSHIP {
        private FRIENDSHIP() {}
        public static final String BASE = "/api/friendship";
        public static final String REQUEST = BASE + "/request";
        public static final String ACCEPT = BASE + "/accept/{id}";
        public static final String REJECT = BASE + "/reject/{id}";
        public static final String REMOVE = BASE + "/remove/{id}";
        public static final String LIST = BASE + "/list";
        public static final String PENDING = BASE + "/pending";
        public static final String SEARCH_USERS = BASE + "/search";
    }
    
    public static final class PAYMENT {
        private PAYMENT() {}
        public static final String BASE = "/api/payment";
        public static final String CREATE = BASE;
        public static final String BY_ID = BASE + "/{id}";
        public static final String BY_USER = BASE + "/user/{userId}";
        public static final String HISTORY = BASE + "/history";
        public static final String SETTLE = BASE + "/settle";
    }
    
    public static final class NOTIFICATION {
        private NOTIFICATION() {}
        public static final String BASE = "/api/notification";
        public static final String ALL = BASE + "/all";
        public static final String UNREAD = BASE + "/unread";
        public static final String MARK_READ = BASE + "/{id}/read";
        public static final String MARK_ALL_READ = BASE + "/read-all";
        public static final String DELETE = BASE + "/{id}";
        public static final String SETTINGS = BASE + "/settings";
    }
    
    public static final class HEALTH {
        private HEALTH() {}
        public static final String BASE = "/actuator";
        public static final String HEALTH = BASE + "/health";
        public static final String INFO = BASE + "/info";
        public static final String METRICS = BASE + "/metrics";
    }
}
