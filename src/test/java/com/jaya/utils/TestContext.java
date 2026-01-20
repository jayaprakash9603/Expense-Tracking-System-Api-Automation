package com.jaya.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe test context for tracking test execution state and correlation.
 * Provides MDC integration for structured logging with test correlation IDs.
 */
public final class TestContext {

    private static final Logger log = LoggerFactory.getLogger(TestContext.class);

    // MDC keys for structured logging
    public static final String MDC_TEST_ID = "testId";
    public static final String MDC_TEST_NAME = "testName";
    public static final String MDC_TEST_CLASS = "testClass";
    public static final String MDC_REQUEST_ID = "requestId";

    // Thread-local storage for test context
    private static final ThreadLocal<TestContextData> CONTEXT = ThreadLocal.withInitial(TestContextData::new);

    // Global test statistics
    private static final Map<String, TestStats> TEST_STATS = new ConcurrentHashMap<>();

    private TestContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== TEST LIFECYCLE ====================

    /**
     * Initializes context for a new test execution.
     */
    public static void startTest(String testClass, String testName) {
        TestContextData data = CONTEXT.get();
        data.testId = generateTestId();
        data.testClass = testClass;
        data.testName = testName;
        data.startTime = System.currentTimeMillis();
        data.requestCount = 0;

        // Set MDC for structured logging
        MDC.put(MDC_TEST_ID, data.testId);
        MDC.put(MDC_TEST_NAME, testName);
        MDC.put(MDC_TEST_CLASS, testClass);

        log.debug("[TEST-START] {} - Test ID: {}", getFullTestName(), data.testId);
    }

    /**
     * Completes the current test execution context.
     */
    public static void endTest(boolean passed, String failureReason) {
        TestContextData data = CONTEXT.get();
        long duration = System.currentTimeMillis() - data.startTime;

        // Record statistics
        String fullName = getFullTestName();
        TEST_STATS.put(fullName, new TestStats(
                data.testId, fullName, duration, data.requestCount, passed, failureReason));

        String status = passed ? "[PASS]" : "[FAIL]";
        log.debug("[TEST-END] {} - {} | Duration: {}ms | API Calls: {}",
                fullName, status, duration, data.requestCount);

        if (!passed && failureReason != null) {
            log.debug("[TEST-END] Failure: {}", failureReason);
        }

        // Clear MDC
        MDC.remove(MDC_TEST_ID);
        MDC.remove(MDC_TEST_NAME);
        MDC.remove(MDC_TEST_CLASS);
        MDC.remove(MDC_REQUEST_ID);

        CONTEXT.remove();
    }

    // ==================== REQUEST TRACKING ====================

    /**
     * Registers a new API request and returns a correlation ID.
     */
    public static String registerRequest() {
        TestContextData data = CONTEXT.get();
        data.requestCount++;

        String requestId = String.format("%s-R%03d",
                Optional.ofNullable(data.testId).orElse("INIT"),
                data.requestCount);

        data.currentRequestId = requestId;
        MDC.put(MDC_REQUEST_ID, requestId);

        return requestId;
    }

    /**
     * Gets the current request ID.
     */
    public static String getCurrentRequestId() {
        return CONTEXT.get().currentRequestId;
    }

    /**
     * Gets the current test ID.
     */
    public static String getCurrentTestId() {
        return CONTEXT.get().testId;
    }

    // ==================== CONTEXT ACCESSORS ====================

    public static String getTestClass() {
        return CONTEXT.get().testClass;
    }

    public static String getTestName() {
        return CONTEXT.get().testName;
    }

    public static String getFullTestName() {
        TestContextData data = CONTEXT.get();
        if (data.testClass == null && data.testName == null) {
            return "Unknown";
        }
        String className = data.testClass != null ? data.testClass.substring(data.testClass.lastIndexOf('.') + 1) : "";
        return className + "." + (data.testName != null ? data.testName : "");
    }

    public static long getElapsedTime() {
        return System.currentTimeMillis() - CONTEXT.get().startTime;
    }

    public static int getRequestCount() {
        return CONTEXT.get().requestCount;
    }

    // ==================== CUSTOM DATA ====================

    /**
     * Stores custom data in the current test context.
     */
    public static void set(String key, Object value) {
        CONTEXT.get().customData.put(key, value);
    }

    /**
     * Retrieves custom data from the current test context.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) CONTEXT.get().customData.get(key);
    }

    /**
     * Retrieves custom data with a default value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key, T defaultValue) {
        Object value = CONTEXT.get().customData.get(key);
        return value != null ? (T) value : defaultValue;
    }

    // ==================== STATISTICS ====================

    /**
     * Gets statistics for all tests run in this session.
     */
    public static Map<String, TestStats> getAllTestStats() {
        return new ConcurrentHashMap<>(TEST_STATS);
    }

    /**
     * Prints a summary of all test execution statistics.
     */
    public static void printTestSummary() {
        if (TEST_STATS.isEmpty()) {
            return;
        }

        log.info("\n+==============================================================================+");
        log.info("|                         TEST EXECUTION SUMMARY                               |");
        log.info("+==============================================================================+");

        long totalDuration = 0;
        int totalRequests = 0;
        int passed = 0;
        int failed = 0;

        for (TestStats stats : TEST_STATS.values()) {
            totalDuration += stats.durationMs;
            totalRequests += stats.requestCount;
            if (stats.passed)
                passed++;
            else
                failed++;
        }

        log.info("| Total Tests     : {} (Passed: {}, Failed: {})",
                TEST_STATS.size(), passed, failed);
        log.info("| Total Duration  : {}ms ({} sec)", totalDuration, totalDuration / 1000);
        log.info("| Total API Calls : {}", totalRequests);
        log.info("| Avg per Test    : {}ms, {} requests",
                TEST_STATS.isEmpty() ? 0 : totalDuration / TEST_STATS.size(),
                TEST_STATS.isEmpty() ? 0 : totalRequests / TEST_STATS.size());
        log.info("+==============================================================================+");

        // Log slow tests
        TEST_STATS.values().stream()
                .filter(s -> s.durationMs > 5000)
                .sorted((a, b) -> Long.compare(b.durationMs, a.durationMs))
                .limit(5)
                .forEach(s -> log.warn("! Slow test: {} - {}ms", s.testName, s.durationMs));
    }

    /**
     * Clears all statistics (typically called at suite start).
     */
    public static void clearStats() {
        TEST_STATS.clear();
    }

    // ==================== PRIVATE HELPERS ====================

    private static String generateTestId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ==================== INNER CLASSES ====================

    private static class TestContextData {
        String testId;
        String testClass;
        String testName;
        String currentRequestId;
        long startTime = System.currentTimeMillis();
        int requestCount;
        Map<String, Object> customData = new ConcurrentHashMap<>();
    }

    public static class TestStats {
        public final String testId;
        public final String testName;
        public final long durationMs;
        public final int requestCount;
        public final boolean passed;
        public final String failureReason;

        TestStats(String testId, String testName, long durationMs, int requestCount,
                boolean passed, String failureReason) {
            this.testId = testId;
            this.testName = testName;
            this.durationMs = durationMs;
            this.requestCount = requestCount;
            this.passed = passed;
            this.failureReason = failureReason;
        }
    }
}
