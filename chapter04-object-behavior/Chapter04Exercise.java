/**
 * EXERCISE — Chapter 4: How Objects Behave (Encapsulation)
 *
 * Problem: Token Bucket Rate Limiter
 * ------------------------------------
 * Implement a rate limiter using the token bucket algorithm.
 *
 * A token bucket:
 *   - Has a maximum capacity of N tokens
 *   - Refills at a rate of R tokens per second (up to max capacity)
 *   - Each request consumes 1 token
 *   - If no tokens available, the request is rejected (not queued)
 *
 * API to implement:
 *   TokenBucket(int capacity, double refillRatePerSecond)
 *   boolean tryConsume()          — consume 1 token; returns false if none available
 *   boolean tryConsume(int tokens)— consume N tokens atomically
 *   int     availableTokens()     — current token count (rounded down)
 *
 * Design notes (senior-level):
 *   - Store last-refill timestamp; compute elapsed time on each call to lazily refill
 *   - This avoids needing a background thread for refilling
 *   - In production you'd make this thread-safe with synchronized or AtomicLong
 *
 * Test:
 *   capacity=10, refill=2.0/sec
 *   Consume 10 tokens immediately → all succeed
 *   Consume 1 more              → fails (bucket empty)
 *   Wait 1 second               → 2 tokens refilled
 *   Consume 2                   → succeed
 *   Consume 1                   → fails
 *
 * Bonus: Implement tryConsume(int tokens, long timeoutMs) — wait up to timeoutMs
 * for enough tokens to accumulate, then consume. Use Thread.sleep in a loop.
 */
public class Chapter04Exercise {

    static class TokenBucket {
        private final int    capacity;
        private final double refillRatePerSecond;
        private double       tokens;
        private long         lastRefillNanos;

        TokenBucket(int capacity, double refillRatePerSecond) {
            this.capacity            = capacity;
            this.refillRatePerSecond = refillRatePerSecond;
            this.tokens              = capacity;  // start full
            this.lastRefillNanos     = System.nanoTime();
        }

        // Lazily refill based on elapsed time since last call
        private void refill() {
            long now     = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + elapsedSeconds * refillRatePerSecond);
            lastRefillNanos = now;
        }

        boolean tryConsume() {
            // TODO: call refill(), then check if tokens >= 1, deduct if so
            return false;
        }

        boolean tryConsume(int requested) {
            // TODO: call refill(), check tokens >= requested, deduct atomically
            return false;
        }

        int availableTokens() {
            refill();
            return (int) tokens;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Token Bucket Rate Limiter ===");
        TokenBucket bucket = new TokenBucket(10, 2.0);

        System.out.println("Available: " + bucket.availableTokens()); // 10

        // Drain the bucket
        int success = 0;
        for (int i = 0; i < 12; i++) {
            if (bucket.tryConsume()) success++;
            else System.out.println("  Rejected at request " + (i + 1));
        }
        System.out.println("Succeeded: " + success + " / 12"); // expected: 10

        // Wait for refill
        System.out.println("\nWaiting 1 second for refill...");
        Thread.sleep(1000);
        System.out.println("Available after 1s: " + bucket.availableTokens()); // ~2

        boolean r1 = bucket.tryConsume();
        boolean r2 = bucket.tryConsume();
        boolean r3 = bucket.tryConsume();
        System.out.println("Consume 3 after refill: " + r1 + " " + r2 + " " + r3); // true true false
    }
}
