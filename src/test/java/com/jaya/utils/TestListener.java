package com.jaya.utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enhanced TestNG listener providing detailed logging for test execution.
 * Integrates with TestContext for correlation tracking and structured logging.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    private static final String DOUBLE_LINE = "================================================================================";
    private static final String SINGLE_LINE = "--------------------------------------------------------------------------------";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public void onStart(ITestContext context) {
        TestContext.clearStats();

        log.info("\n{}", DOUBLE_LINE);
        log.info("  TEST SUITE STARTED: {}", context.getName());
        log.info("  Start Time: {}", LocalDateTime.now().format(TIME_FORMAT));
        log.info("  Thread Count: {}", context.getCurrentXmlTest().getThreadCount());
        log.info("  Parallel Mode: {}", context.getCurrentXmlTest().getParallel());
        log.info("{}", DOUBLE_LINE);
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        long duration = context.getEndDate().getTime() - context.getStartDate().getTime();

        log.info("\n{}", DOUBLE_LINE);
        log.info("  TEST SUITE COMPLETED: {}", context.getName());
        log.info("{}", SINGLE_LINE);
        log.info("  Results Summary:");
        log.info("    ✓ Passed  : {} ({}%)", passed, total > 0 ? (passed * 100 / total) : 0);
        log.info("    ✗ Failed  : {} ({}%)", failed, total > 0 ? (failed * 100 / total) : 0);
        log.info("    ⊘ Skipped : {}", skipped);
        log.info("    ═ Total   : {}", total);
        log.info("{}", SINGLE_LINE);
        log.info("  Execution Time: {} ms ({} sec)", duration, duration / 1000);
        log.info("  End Time: {}", LocalDateTime.now().format(TIME_FORMAT));
        log.info("{}", DOUBLE_LINE);

        // Log failed test names for quick reference
        if (failed > 0) {
            log.error("\n  ⚠ FAILED TESTS:");
            context.getFailedTests().getAllResults().forEach(result -> {
                log.error("    • {}.{}",
                        getSimpleClassName(result.getTestClass().getName()),
                        result.getMethod().getMethodName());
                if (result.getThrowable() != null) {
                    log.error("      Reason: {}", result.getThrowable().getMessage());
                }
            });
        }

        // Print test context statistics
        TestContext.printTestSummary();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testClass = result.getTestClass().getName();
        String testName = result.getMethod().getMethodName();

        // Initialize test context for correlation tracking
        TestContext.startTest(testClass, testName);

        log.info("\n{}", SINGLE_LINE);
        log.info("▶ TEST STARTED: {}.{}", getSimpleClassName(testClass), testName);
        log.info("  Test ID     : {}", TestContext.getCurrentTestId());
        log.info("  Description : {}", getTestDescription(result));
        log.info("  Priority    : {}", result.getMethod().getPriority());
        log.info("  Groups      : {}", formatGroups(result.getMethod().getGroups()));

        // Log test parameters if any
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            log.info("  Parameters  : {}", formatParameters(params));
        }

        log.info("{}", SINGLE_LINE);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = getDuration(result);
        int apiCalls = TestContext.getRequestCount();

        log.info("\n✓ TEST PASSED: {}", result.getMethod().getMethodName());
        log.info("  Test ID      : {}", TestContext.getCurrentTestId());
        log.info("  Duration     : {} ms {}", duration, getDurationIndicator(duration));
        log.info("  API Calls    : {}", apiCalls);
        log.info("{}", SINGLE_LINE);

        // End test context
        TestContext.endTest(true, null);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = getDuration(result);
        int apiCalls = TestContext.getRequestCount();
        Throwable throwable = result.getThrowable();

        log.error("\n✗ TEST FAILED: {}", result.getMethod().getMethodName());
        log.error("  Test ID      : {}", TestContext.getCurrentTestId());
        log.error("  Duration     : {} ms", duration);
        log.error("  API Calls    : {}", apiCalls);
        log.error("{}", SINGLE_LINE);

        if (throwable != null) {
            log.error("  FAILURE DETAILS:");
            log.error("  Exception    : {}", throwable.getClass().getSimpleName());
            log.error("  Message      : {}", throwable.getMessage());

            // Log the location where failure occurred
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            if (stackTrace.length > 0) {
                StackTraceElement failureLocation = findRelevantStackFrame(stackTrace);
                if (failureLocation != null) {
                    log.error("  Location     : {}:{} ({})",
                            failureLocation.getFileName(),
                            failureLocation.getLineNumber(),
                            failureLocation.getMethodName());
                }
            }

            // Log assertion details if it's an assertion error
            if (throwable instanceof AssertionError) {
                log.error("  Assertion    : {}", extractAssertionDetails(throwable));
            }

            // Attach full stack trace
            attachStackTrace(throwable);

            // Log abbreviated stack trace to console
            log.debug("  Stack Trace:");
            Arrays.stream(stackTrace)
                    .filter(ste -> ste.getClassName().startsWith("com.jaya"))
                    .limit(10)
                    .forEach(ste -> log.debug("    at {}.{}({}:{})",
                            ste.getClassName(), ste.getMethodName(),
                            ste.getFileName(), ste.getLineNumber()));
        }

        log.error("{}", SINGLE_LINE);

        // End test context
        String failureReason = throwable != null ? throwable.getMessage() : "Unknown failure";
        TestContext.endTest(false, failureReason);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("\n⊘ TEST SKIPPED: {}", result.getMethod().getMethodName());
        log.warn("  Test ID      : {}", TestContext.getCurrentTestId());

        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            log.warn("  Skip Reason  : {}", throwable.getMessage());
        }

        // Check for dependency failures
        String[] dependentMethods = result.getMethod().getMethodsDependedUpon();
        if (dependentMethods != null && dependentMethods.length > 0) {
            log.warn("  Dependencies : {}", Arrays.toString(dependentMethods));
        }

        log.warn("{}", SINGLE_LINE);

        // End test context
        TestContext.endTest(false, "Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("\n⚠ TEST PARTIALLY PASSED: {} (within success percentage)",
                result.getMethod().getMethodName());
        log.warn("  Success %    : {}%", result.getMethod().getSuccessPercentage());
        log.warn("{}", SINGLE_LINE);

        TestContext.endTest(true, "Partial success");
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        long timeout = result.getMethod().getTimeOut();

        log.error("\n⏱ TEST TIMEOUT: {}", result.getMethod().getMethodName());
        log.error("  Test ID      : {}", TestContext.getCurrentTestId());
        log.error("  Timeout      : {} ms", timeout);
        log.error("  API Calls    : {}", TestContext.getRequestCount());
        log.error("{}", SINGLE_LINE);

        TestContext.endTest(false, "Timeout after " + timeout + "ms");
    }

    // ==================== PRIVATE HELPERS ====================

    private long getDuration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    private String getDurationIndicator(long duration) {
        if (duration < 1000)
            return "(fast ⚡)";
        if (duration < 3000)
            return "(normal)";
        if (duration < 5000)
            return "(slow ⚠)";
        return "(very slow 🐢)";
    }

    private String getSimpleClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot > 0 ? fullClassName.substring(lastDot + 1) : fullClassName;
    }

    private String getTestDescription(ITestResult result) {
        String description = result.getMethod().getDescription();
        return (description != null && !description.isEmpty()) ? description : "No description";
    }

    private String formatGroups(String[] groups) {
        if (groups == null || groups.length == 0) {
            return "[default]";
        }
        return Arrays.toString(groups);
    }

    private String formatParameters(Object[] params) {
        return Arrays.stream(params)
                .map(p -> p != null ? p.toString() : "null")
                .collect(Collectors.joining(", "));
    }

    private StackTraceElement findRelevantStackFrame(StackTraceElement[] stackTrace) {
        // Find the first stack frame in test code (com.jaya.tests)
        for (StackTraceElement ste : stackTrace) {
            if (ste.getClassName().startsWith("com.jaya.tests")) {
                return ste;
            }
        }
        // Fallback to first frame in our package
        for (StackTraceElement ste : stackTrace) {
            if (ste.getClassName().startsWith("com.jaya")) {
                return ste;
            }
        }
        return stackTrace.length > 0 ? stackTrace[0] : null;
    }

    private String extractAssertionDetails(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null)
            return "Assertion failed";

        // Try to extract expected/actual from common assertion formats
        if (message.contains("expected") && message.contains("but")) {
            return message;
        }
        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }

    private void attachStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Allure.addAttachment("Stack Trace", "text/plain", sw.toString());
    }
}
