package com.jaya.utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * TestListener - Custom TestNG listener for enhanced test reporting
 * Provides logging, Allure attachments, and test execution tracking
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Started: {}", context.getName());
        log.info("========================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Finished: {}", context.getName());
        log.info("Passed: {}, Failed: {}, Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
        log.info("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ Starting test: {}.{}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        log.info("✓ PASSED: {} ({}ms)",
                result.getMethod().getMethodName(),
                duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        log.error("✗ FAILED: {} ({}ms)",
                result.getMethod().getMethodName(),
                duration);

        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            log.error("Failure reason: {}", throwable.getMessage());

            // Attach stack trace to Allure report
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            Allure.addAttachment("Stack Trace", "text/plain", sw.toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⊘ SKIPPED: {}", result.getMethod().getMethodName());

        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            log.warn("Skip reason: {}", throwable.getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("⚠ PARTIALLY PASSED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        log.error("⏱ TIMEOUT: {}", result.getMethod().getMethodName());
    }
}
