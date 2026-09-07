/**
 * CONCEPT: CompletableFuture — async pipelines without blocking
 *
 * CompletableFuture (Java 8+) lets you compose async operations that don't block.
 * It replaces the old Future pattern where get() blocks the calling thread.
 *
 * Key operations:
 *   supplyAsync(supplier)   — start async computation returning a value
 *   thenApply(function)     — transform result (like Stream.map)
 *   thenAccept(consumer)    — consume result (like Stream.forEach)
 *   thenCompose(function)   — chain another CompletableFuture (like Stream.flatMap)
 *   thenCombine(other, fn)  — combine two independent futures
 *   exceptionally(handler)  — recover from failures
 *   allOf / anyOf           — wait for multiple futures
 *
 * Why this matters:
 *   - Modern Java backends are async — Spring WebFlux, reactive programming build on this
 *   - Prevents thread starvation: IO-waiting doesn't block a thread pool thread
 *   - Parallel API calls with thenCombine = critical for latency reduction
 */
import java.util.*;
import java.util.concurrent.*;

class CompletableFuture_ {

    // Simulate async service calls
    static CompletableFuture<String> fetchUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            if (userId <= 0) throw new RuntimeException("Invalid user ID: " + userId);
            return "User{id=" + userId + ", name=Alice}";
        });
    }

    static CompletableFuture<String> fetchOrders(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "Orders[3 items] for " + userId;
        });
    }

    static CompletableFuture<Double> fetchExchangeRate(String currency) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(80); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "EUR".equals(currency) ? 1.08 : 1.0;
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // --- Basic chain: supplyAsync → thenApply → thenAccept ---
        System.out.println("--- Basic async chain ---");
        CompletableFuture<Void> chain = fetchUser(1)
            .thenApply(user -> user.toUpperCase())           // transform result
            .thenApply(upper -> "Formatted: " + upper)      // transform again
            .thenAccept(System.out::println);                // consume — returns Void
        chain.get(); // wait for chain to complete

        // --- thenCompose: sequential async (fetch user, THEN fetch their orders) ---
        System.out.println("\n--- Sequential async (thenCompose) ---");
        long start = System.currentTimeMillis();
        String result = fetchUser(1)
            .thenCompose(user -> fetchOrders(user))  // second call depends on first result
            .get();
        System.out.println(result + " [" + (System.currentTimeMillis() - start) + "ms]");

        // --- thenCombine: parallel async (fetch user AND exchange rate simultaneously) ---
        System.out.println("\n--- Parallel async (thenCombine) ---");
        start = System.currentTimeMillis();
        String combined = fetchUser(1)
            .thenCombine(fetchExchangeRate("EUR"),
                (user, rate) -> user + " | rate=" + rate) // both run in parallel!
            .get();
        System.out.println(combined + " [" + (System.currentTimeMillis() - start) + "ms]");
        // Sequential would take 100+80=180ms; parallel takes max(100,80)=100ms

        // --- allOf: wait for multiple independent futures ---
        System.out.println("\n--- allOf (fan-out, fan-in) ---");
        start = System.currentTimeMillis();
        CompletableFuture<String> f1 = fetchUser(1);
        CompletableFuture<String> f2 = fetchOrders("user-1");
        CompletableFuture<Double> f3 = fetchExchangeRate("EUR");

        CompletableFuture.allOf(f1, f2, f3).get(); // wait for ALL
        System.out.println("All done in " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("  user:   " + f1.get());
        System.out.println("  orders: " + f2.get());
        System.out.println("  rate:   " + f3.get());

        // --- exceptionally: error recovery ---
        System.out.println("\n--- Error handling ---");
        String fallback = fetchUser(-1)                         // throws
            .exceptionally(ex -> "FALLBACK_USER (error: " + ex.getMessage() + ")")
            .get();
        System.out.println("Result: " + fallback);

        // --- Timeout (Java 9+) ---
        System.out.println("\n--- Timeout ---");
        try {
            fetchOrders("slow")
                .orTimeout(50, TimeUnit.MILLISECONDS)  // fail fast
                .get();
        } catch (ExecutionException e) {
            System.out.println("Timed out: " + e.getCause().getClass().getSimpleName());
        }

        // TRY THIS:
        // 1. Chain fetchUser → fetchOrders → fetchExchangeRate all in one pipeline.
        //    Make orders and exchange rate fetch in parallel, then combine.
        // 2. Use anyOf() to send the same request to 3 different servers and take
        //    whichever responds first. How does this implement a "hedged request"?
        // 3. Add retry logic: if fetchUser fails, retry once after 100ms.
        //    Build it using exceptionallyCompose (Java 12+) or manual chaining.
    }
}
