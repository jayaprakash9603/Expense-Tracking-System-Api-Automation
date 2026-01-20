package com.jaya.utils;

import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for detailed request/response logging with correlation
 * tracking.
 * Provides structured logging for API automation debugging.
 */
public final class RequestResponseLogger {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLogger.class);
    private static final AtomicLong REQUEST_COUNTER = new AtomicLong(0);

    // ANSI colors for console output (disabled in CI)
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";

    // ASCII-compatible separators (works in all terminals including Windows
    // CMD/PowerShell)
    private static final String SEPARATOR = "================================================================================";
    private static final String SECTION_SEPARATOR = "--------------------------------------------------------------------------------";

    private RequestResponseLogger() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generates a unique request ID for correlation tracking.
     */
    public static String generateRequestId() {
        return String.format("REQ-%06d", REQUEST_COUNTER.incrementAndGet());
    }

    /**
     * Logs a complete API request with all details.
     */
    public static void logRequest(String requestId, String method, String endpoint,
            RequestSpecification spec, Object body) {
        if (!log.isDebugEnabled()) {
            log.info("[{}] → {} {}", requestId, method, endpoint);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(SEPARATOR);
        sb.append("\n| ").append(colorize("REQUEST", CYAN)).append(" [").append(requestId).append("]");
        sb.append("\n").append(SEPARATOR);
        sb.append("\n| Method   : ").append(colorize(method, YELLOW));
        sb.append("\n| Endpoint : ").append(endpoint);

        // Extract headers and base URI from request spec
        if (spec != null) {
            try {
                QueryableRequestSpecification queryable = SpecificationQuerier.query(spec);

                sb.append("\n| Base URI : ").append(queryable.getBaseUri());

                // Log headers (mask sensitive ones)
                Map<String, String> headers = queryable.getHeaders().asList().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                h -> h.getName(),
                                h -> maskSensitiveHeader(h.getName(), h.getValue()),
                                (v1, v2) -> v1));

                if (!headers.isEmpty()) {
                    sb.append("\n").append(SECTION_SEPARATOR);
                    sb.append("\n| Headers:");
                    headers.forEach((name, value) -> sb.append("\n|   ").append(name).append(": ").append(value));
                }

                // Log query params
                Map<String, String> queryParams = queryable.getQueryParams();
                if (queryParams != null && !queryParams.isEmpty()) {
                    sb.append("\n").append(SECTION_SEPARATOR);
                    sb.append("\n| Query Params:");
                    queryParams.forEach((name, value) -> sb.append("\n|   ").append(name).append("=").append(value));
                }

                // Log path params
                Map<String, String> pathParams = queryable.getPathParams();
                if (pathParams != null && !pathParams.isEmpty()) {
                    sb.append("\n").append(SECTION_SEPARATOR);
                    sb.append("\n| Path Params:");
                    pathParams.forEach((name, value) -> sb.append("\n|   ").append(name).append("=").append(value));
                }
            } catch (Exception e) {
                log.trace("Could not query request spec: {}", e.getMessage());
            }
        }

        // Log request body
        if (body != null) {
            sb.append("\n").append(SECTION_SEPARATOR);
            sb.append("\n| Body:");
            String bodyStr = formatBody(body);
            for (String line : bodyStr.split("\n")) {
                sb.append("\n|   ").append(maskSensitiveData(line));
            }
        }

        sb.append("\n").append(SEPARATOR);
        log.debug(sb.toString());
    }

    /**
     * Logs a complete API response with all details.
     */
    public static void logResponse(String requestId, Response response, long durationMs) {
        int statusCode = response.getStatusCode();
        String statusColor = getStatusColor(statusCode);

        if (!log.isDebugEnabled()) {
            log.info("[{}] ← {} {} ({}ms)", requestId, statusCode,
                    response.getStatusLine().replaceFirst("HTTP/\\d\\.\\d ", ""), durationMs);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(SEPARATOR);
        sb.append("\n| ").append(colorize("RESPONSE", CYAN)).append(" [").append(requestId).append("]");
        sb.append("\n").append(SEPARATOR);
        sb.append("\n| Status   : ").append(colorize(String.valueOf(statusCode), statusColor))
                .append(" ").append(response.getStatusLine().replaceFirst("HTTP/\\d\\.\\d ", ""));
        sb.append("\n| Duration : ").append(colorize(durationMs + "ms", getDurationColor(durationMs)));
        sb.append("\n| Size     : ").append(formatSize(response.getBody().asByteArray().length));

        // Log response headers
        sb.append("\n").append(SECTION_SEPARATOR);
        sb.append("\n| Headers:");
        response.getHeaders().asList()
                .forEach(header -> sb.append("\n|   ").append(header.getName()).append(": ").append(header.getValue()));

        // Log response body
        String responseBody = response.getBody().asString();
        if (responseBody != null && !responseBody.isEmpty()) {
            sb.append("\n").append(SECTION_SEPARATOR);
            sb.append("\n| Body:");
            String formattedBody = formatJsonBody(responseBody);
            for (String line : formattedBody.split("\n")) {
                sb.append("\n|   ").append(maskSensitiveData(line));
            }
        }

        sb.append("\n").append(SEPARATOR);

        // Use appropriate log level based on status code
        if (statusCode >= 500) {
            log.error(sb.toString());
        } else if (statusCode >= 400) {
            log.warn(sb.toString());
        } else {
            log.debug(sb.toString());
        }
    }

    /**
     * Logs a retry attempt.
     */
    public static void logRetry(String requestId, String operation, int attempt, int maxRetries,
            int statusCode, String reason) {
        log.warn("[{}] ⟳ RETRY {}/{} for {} - Status: {}, Reason: {}",
                requestId, attempt, maxRetries, operation, statusCode, reason);
    }

    /**
     * Logs a request failure.
     */
    public static void logRequestFailure(String requestId, String operation, Exception e) {
        log.error("[{}] ✗ REQUEST FAILED: {} - {}: {}",
                requestId, operation, e.getClass().getSimpleName(), e.getMessage());
        if (log.isDebugEnabled()) {
            log.debug("[{}] Stack trace:", requestId, e);
        }
    }

    /**
     * Logs an assertion/validation step.
     */
    public static void logValidation(String description, boolean passed, String expected, String actual) {
        if (passed) {
            log.debug("  ✓ {} - Expected: {}, Actual: {}", description, expected, actual);
        } else {
            log.error("  ✗ {} - Expected: {}, Actual: {}", description, expected, actual);
        }
    }

    /**
     * Logs the start of a test step.
     */
    public static void logStep(String stepDescription) {
        log.info("  → {}", stepDescription);
    }

    /**
     * Logs test data being used.
     */
    public static void logTestData(String description, Object data) {
        if (log.isDebugEnabled()) {
            log.debug("  📋 Test Data - {}: {}", description, maskSensitiveData(String.valueOf(data)));
        }
    }

    // ==================== PRIVATE HELPERS ====================

    private static String maskSensitiveHeader(String name, String value) {
        String lowerName = name.toLowerCase();
        if (lowerName.contains("authorization") || lowerName.contains("token") ||
                lowerName.contains("api-key") || lowerName.contains("secret")) {
            if (value != null && value.length() > 20) {
                return value.substring(0, 15) + "..." + value.substring(value.length() - 5) + " [MASKED]";
            }
            return "***[MASKED]***";
        }
        return value;
    }

    private static String maskSensitiveData(String data) {
        if (data == null)
            return null;
        // Mask passwords, tokens, secrets in response body
        return data.replaceAll("(\"password\"\\s*:\\s*\")[^\"]+\"", "$1***\"")
                .replaceAll("(\"jwt\"\\s*:\\s*\")[^\"]{20}[^\"]*\"", "$1[TOKEN_MASKED]...\"")
                .replaceAll("(\"token\"\\s*:\\s*\")[^\"]{20}[^\"]*\"", "$1[TOKEN_MASKED]...\"")
                .replaceAll("(\"secret\"\\s*:\\s*\")[^\"]+\"", "$1***\"");
    }

    private static String formatBody(Object body) {
        if (body == null)
            return "null";
        if (body instanceof String) {
            return formatJsonBody((String) body);
        }
        try {
            // Try to convert to JSON string
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        } catch (Exception e) {
            return body.toString();
        }
    }

    private static String formatJsonBody(String body) {
        if (body == null || body.isEmpty())
            return "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Object json = mapper.readValue(body, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            // Not valid JSON, return as-is but truncate if too long
            if (body.length() > 1000) {
                return body.substring(0, 1000) + "\n... [TRUNCATED - " + body.length() + " chars total]";
            }
            return body;
        }
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < 1024 * 1024)
            return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private static String getStatusColor(int statusCode) {
        if (statusCode >= 200 && statusCode < 300)
            return GREEN;
        if (statusCode >= 300 && statusCode < 400)
            return YELLOW;
        if (statusCode >= 400 && statusCode < 500)
            return YELLOW;
        return RED;
    }

    private static String getDurationColor(long durationMs) {
        if (durationMs < 500)
            return GREEN;
        if (durationMs < 2000)
            return YELLOW;
        return RED;
    }

    private static String colorize(String text, String color) {
        // Check if running in CI or non-interactive environment
        if (System.getenv("CI") != null || System.getenv("JENKINS_HOME") != null ||
                System.console() == null) {
            return text;
        }
        return color + text + RESET;
    }
}
