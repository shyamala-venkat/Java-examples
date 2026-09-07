/**
 * EXERCISE — Chapter 11: Exception Handling
 *
 * Problem: HTTP Client with Retry and Exponential Backoff
 * -------------------------------------------------------
 * Build a resilient API client that retries failed requests with exponential backoff.
 *
 * Design:
 *   - Custom exception hierarchy:
 *       ApiException (checked, base)
 *         ├── RetryableException — transient errors (5xx, timeout), should retry
 *         └── FatalException     — permanent errors (4xx), do NOT retry
 *
 *   - ApiClient.execute(Request request) throws ApiException:
 *       Tries up to maxRetries times
 *       On RetryableException: wait delay, double the delay, try again
 *       On FatalException: throw immediately (no retry)
 *       If all retries exhausted: throw RetryableException with message "Max retries exceeded"
 *
 *   - Backoff formula: delay = initialDelayMs * (2 ^ attemptNumber)
 *       Attempt 1: 100ms, Attempt 2: 200ms, Attempt 3: 400ms...
 *       Cap at maxDelayMs (e.g., 5000ms)
 *
 *   - Request record: url, method, body
 *   - Response record: statusCode, body
 *
 *   - Simulate failures with a FakeHttpServer that:
 *       Returns 500 for the first N calls (configurable), then 200
 *       Returns 404 for URLs containing "notfound"
 *       Has a configurable failure count so tests can verify retry behavior
 *
 * Test scenarios:
 *   1. URL that succeeds on first try → no retry
 *   2. URL that fails 2 times then succeeds → 2 retries, then success
 *   3. URL that always fails → max retries exceeded
 *   4. URL returning 404 → FatalException, no retry (verify by checking failCount)
 *
 * Senior concept: this pattern is used in every production HTTP client.
 * Libraries like Resilience4j and Spring Retry implement this on top of the JDK.
 */
public class Exercise {

    record Request(String url, String method, String body) {
        Request(String url) { this(url, "GET", null); }
    }
    record Response(int statusCode, String body) {
        boolean isSuccess()   { return statusCode >= 200 && statusCode < 300; }
        boolean isRetryable() { return statusCode >= 500 || statusCode == 429; }
    }

    // --- Custom exception hierarchy ---
    static class ApiException extends Exception {
        private final int statusCode;
        ApiException(String msg, int statusCode) { super(msg); this.statusCode = statusCode; }
        int getStatusCode() { return statusCode; }
    }
    static class RetryableException extends ApiException {
        RetryableException(String msg, int code) { super(msg, code); }
    }
    static class FatalException extends ApiException {
        FatalException(String msg, int code) { super(msg, code); }
    }

    // --- Fake server to simulate real HTTP behavior ---
    static class FakeHttpServer {
        private int failCount;       // how many times to fail before succeeding
        private int callCount = 0;

        FakeHttpServer(int failCount) { this.failCount = failCount; }

        Response call(Request req) {
            callCount++;
            if (req.url().contains("notfound")) return new Response(404, "Not Found");
            if (callCount <= failCount)          return new Response(500, "Internal Server Error");
            return new Response(200, "Success: " + req.url());
        }

        int getCallCount() { return callCount; }
    }

    static class ApiClient {
        private final FakeHttpServer server;
        private final int  maxRetries;
        private final long initialDelayMs;
        private final long maxDelayMs;

        ApiClient(FakeHttpServer server, int maxRetries, long initialDelayMs, long maxDelayMs) {
            this.server         = server;
            this.maxRetries     = maxRetries;
            this.initialDelayMs = initialDelayMs;
            this.maxDelayMs     = maxDelayMs;
        }

        Response execute(Request request) throws ApiException {
            // TODO: implement retry with exponential backoff
            // Pseudocode:
            //   for attempt 1..maxRetries+1:
            //     response = server.call(request)
            //     if response.isSuccess() → return response
            //     if not response.isRetryable() → throw FatalException
            //     if attempt <= maxRetries → sleep(min(initialDelay * 2^(attempt-1), maxDelay))
            //   throw RetryableException("Max retries exceeded", lastStatusCode)
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Retry with Exponential Backoff ===\n");

        // Test 1: immediate success
        System.out.println("--- Test 1: Immediate success ---");
        // FakeHttpServer s1 = new FakeHttpServer(0);
        // ApiClient c1 = new ApiClient(s1, 3, 100, 5000);
        // try { Response r = c1.execute(new Request("https://api.example.com/data"));
        //       System.out.println("Response: " + r.statusCode() + " calls=" + s1.getCallCount()); }
        // catch (ApiException e) { System.out.println("Unexpected: " + e.getMessage()); }

        // Test 2: fail twice then succeed
        System.out.println("--- Test 2: Fail 2x then succeed ---");
        // FakeHttpServer s2 = new FakeHttpServer(2);
        // ...

        // Test 3: always fail → max retries exceeded
        System.out.println("--- Test 3: Max retries exceeded ---");
        // ...

        // Test 4: 404 → fatal, no retry
        System.out.println("--- Test 4: 404 Fatal (no retry) ---");
        // ...
    }
}
