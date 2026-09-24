/**
 * EXERCISE — Chapter 18: Testing Like a Professional
 *
 * Problem: Test RetryClient with JUnit 5 + Mockito
 * -----------------------------------------------------------
 * RetryClient below is fully implemented — production code, not what you're
 * writing today. Your job is the test suite: mock its HttpTransport dependency
 * so every scenario is deterministic and instant (no real network, no real sleep).
 * (This mirrors the retry client you built by hand in chapter 11 — same shape,
 * now viewed from the test-writer's side of the boundary.)
 *
 * RetryClient.call(url):
 *   - calls transport.send(url), which returns an int status code
 *   - 2xx  → return the status code immediately (success)
 *   - 5xx  → retry, up to maxRetries times, THEN throw IllegalStateException
 *   - 4xx  → throw IllegalArgumentException immediately, no retry (fatal)
 *
 * Write these tests (method stubs + TODOs below):
 *   1. succeedsOnFirstTry       — transport returns 200 once; call() returns 200;
 *                                  verify transport.send() was called exactly once.
 *   2. retriesThenSucceeds      — transport returns 500, 500, then 200 (Mockito's
 *                                  chained thenReturn); call() returns 200; verify
 *                                  transport.send() was called exactly 3 times.
 *   3. throwsAfterMaxRetries    — transport ALWAYS returns 500; assertThrows
 *                                  IllegalStateException; verify called maxRetries+1 times.
 *   4. failsFastOn4xxNoRetry    — transport returns 404; assertThrows
 *                                  IllegalArgumentException; verify called EXACTLY ONCE
 *                                  (this assertion is what proves "no retry happened" — a
 *                                  test that only checks the exception type would miss a
 *                                  bug where it retried 3 times before giving up).
 *
 * Senior tip: mocking transport instead of driving a real Thread.sleep()-based
 * retry loop is what makes this whole suite run in milliseconds instead of
 * seconds — slow test suites are the #1 reason teams stop running tests on
 * every commit.
 *
 * Run: mvn test   (or run this class directly from IntelliJ)
 */
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Chapter18Exercise {

    interface HttpTransport {
        int send(String url);
    }

    static class RetryClient {
        private final HttpTransport transport;
        private final int maxRetries;

        RetryClient(HttpTransport transport, int maxRetries) {
            this.transport  = transport;
            this.maxRetries = maxRetries;
        }

        int call(String url) {
            int lastStatus = -1;
            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                int status = transport.send(url);
                if (status >= 200 && status < 300) return status;
                if (status >= 400 && status < 500)
                    throw new IllegalArgumentException("Fatal client error: " + status);
                lastStatus = status; // 5xx — retryable, loop again if attempts remain
            }
            throw new IllegalStateException("Max retries exceeded, last status=" + lastStatus);
        }
    }

    @Mock HttpTransport transport;

    @Test
    void succeedsOnFirstTry() {
        // TODO: when(transport.send(anyString())).thenReturn(200);
        // TODO: RetryClient client = new RetryClient(transport, 3);
        // TODO: assertEquals(200, client.call("https://api.example.com"));
        // TODO: verify(transport, times(1)).send(anyString());
    }

    @Test
    void retriesThenSucceeds() {
        // TODO: when(transport.send(anyString())).thenReturn(500, 500, 200);
        // TODO: create client, call it, assert result == 200
        // TODO: verify(transport, times(3)).send(anyString());
    }

    @Test
    void throwsAfterMaxRetries() {
        // TODO: when(transport.send(anyString())).thenReturn(500); // every call
        // TODO: RetryClient client = new RetryClient(transport, 2);
        // TODO: assertThrows(IllegalStateException.class, () -> client.call("https://api.example.com"));
        // TODO: verify(transport, times(3)).send(anyString()); // maxRetries(2) + 1 initial attempt
    }

    @Test
    void failsFastOn4xxNoRetry() {
        // TODO: when(transport.send(anyString())).thenReturn(404);
        // TODO: RetryClient client = new RetryClient(transport, 3);
        // TODO: assertThrows(IllegalArgumentException.class, () -> client.call("https://api.example.com"));
        // TODO: verify(transport, times(1)).send(anyString()); // proves NO retry happened
    }
}
