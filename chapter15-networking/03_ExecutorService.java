/**
 * CONCEPT: ExecutorService — managed thread pools
 *
 * Creating a raw Thread per task is expensive and uncontrolled.
 * ExecutorService manages a reusable pool of threads.
 *
 * Why this matters:
 *   - Raw Thread creation = expensive OS resource allocation
 *   - Unbounded thread creation = OOM (classic server under load bug)
 *   - Thread pool = bounded resource + reuse + graceful shutdown
 *   - Different pool types for different workloads (IO-bound vs CPU-bound)
 *
 * Interview: "How would you handle 1000 concurrent API requests?"
 * Answer involves thread pools, queue strategies, and backpressure.
 */
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

class ExecutorService_ { // underscore to avoid conflict with java.util.concurrent.ExecutorService

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // --- Fixed thread pool: CPU-bound tasks ---
        System.out.println("--- FixedThreadPool ---");
        // Rule of thumb for CPU-bound: pool size = Runtime.availableProcessors()
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService cpuPool = Executors.newFixedThreadPool(cores);
        System.out.println("CPUs: " + cores + " → pool size: " + cores);

        List<Future<Long>> futures = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            final int n = i * 10;
            futures.add(cpuPool.submit(() -> {
                long sum = 0;
                for (int j = 1; j <= n * 1_000_000; j++) sum += j;
                System.out.println("[" + Thread.currentThread().getName() + "] sum to " + n + "M done");
                return sum;
            }));
        }
        long total = 0;
        for (Future<Long> f : futures) total += f.get(); // get() blocks until result ready
        System.out.println("Combined total: " + total);
        cpuPool.shutdown();

        // --- Cached thread pool: IO-bound tasks (many threads, short-lived) ---
        System.out.println("\n--- CachedThreadPool (IO-bound) ---");
        ExecutorService ioPool = Executors.newCachedThreadPool(); // grows on demand
        List<Future<String>> ioResults = new ArrayList<>();
        String[] urls = {"/api/users", "/api/orders", "/api/products", "/api/analytics"};

        for (String url : urls) {
            ioResults.add(ioPool.submit(() -> {
                Thread.sleep((long)(Math.random() * 200 + 50)); // simulate IO
                return "Response from " + url;
            }));
        }
        for (Future<String> f : ioResults) System.out.println("  " + f.get());
        ioPool.shutdown();

        // --- ScheduledExecutorService: periodic tasks ---
        System.out.println("\n--- ScheduledExecutorService ---");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Run once after 200ms
        scheduler.schedule(() -> System.out.println("  One-shot at " + System.currentTimeMillis() % 10000), 200, TimeUnit.MILLISECONDS);

        // Run every 150ms (fixed rate — doesn't adjust for task duration)
        ScheduledFuture<?> periodic = scheduler.scheduleAtFixedRate(
            () -> System.out.println("  Heartbeat at " + System.currentTimeMillis() % 10000),
            0, 150, TimeUnit.MILLISECONDS
        );

        Thread.sleep(600);
        periodic.cancel(false); // stop the periodic task
        scheduler.shutdown();
        scheduler.awaitTermination(1, TimeUnit.SECONDS);

        // --- Shutdown vs shutdownNow ---
        System.out.println("\n--- Shutdown behavior ---");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> { try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } System.out.println("Task finished"); });
        pool.shutdown();                               // no new tasks; existing ones finish
        // pool.submit(() -> {}); // RejectedExecutionException — pool is shut down
        boolean done = pool.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("Clean shutdown: " + done);

        // TRY THIS:
        // 1. What happens with newFixedThreadPool(1) and 10 tasks?
        //    Tasks are queued — observe sequential execution from the same thread.
        // 2. Use invokeAll() to submit a List of Callables and wait for all results at once.
        //    Compare to submitting individually and calling get() on each Future.
        // 3. Set a timeout on Future.get(2, TimeUnit.SECONDS). What exception is thrown
        //    if the task takes longer? Is the task cancelled?
    }
}
