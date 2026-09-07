/**
 * CONCEPT: Race Conditions and Synchronization
 *
 * A race condition occurs when the outcome depends on which thread runs first.
 * They are the #1 source of concurrency bugs — hard to reproduce, hard to fix.
 *
 * The fix: mutual exclusion — only one thread runs a critical section at a time.
 * Java provides: synchronized, volatile, and java.util.concurrent atomic classes.
 *
 * Why this matters:
 *   - "Tell me about a concurrency bug you've debugged" — this is usually it
 *   - synchronized guarantees visibility + atomicity
 *   - volatile guarantees visibility only (not atomicity for compound ops)
 *   - AtomicInteger/AtomicLong for lock-free counter increment
 */
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class RaceCondition {

    // --- Broken counter: race condition ---
    static class UnsafeCounter {
        int count = 0;
        void increment() { count++; } // NOT atomic: read-modify-write is 3 ops
        int get()        { return count; }
    }

    // --- Fixed with synchronized ---
    static class SynchronizedCounter {
        private int count = 0;
        synchronized void increment()  { count++; } // only one thread at a time
        synchronized int get()         { return count; }
    }

    // --- Fixed with AtomicInteger (lock-free, faster) ---
    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        void increment() { count.incrementAndGet(); } // atomic compare-and-swap
        int  get()       { return count.get(); }
    }

    // --- volatile: visibility without mutual exclusion ---
    // Use for a single variable that is written by one thread and read by others.
    // NOT sufficient for compound operations like count++.
    static volatile boolean running = true;

    static void runCounterTest(String label, Runnable action, Runnable getCount)
            throws InterruptedException {
        int THREADS = 10, OPS_PER_THREAD = 1_000;
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPS_PER_THREAD; j++) action.run();
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        System.out.print(label + " result: ");
        getCount.run();
        System.out.println(" (expected " + (THREADS * OPS_PER_THREAD) + ")");
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Race Condition Demo ===");
        System.out.println("10 threads × 1000 increments = expected 10,000\n");

        // --- Unsafe: race condition shows count < 10,000 ---
        UnsafeCounter unsafe = new UnsafeCounter();
        runCounterTest("Unsafe",      () -> unsafe.increment(), () -> System.out.print(unsafe.get()));

        // --- Synchronized: always 10,000 ---
        SynchronizedCounter sync = new SynchronizedCounter();
        runCounterTest("Synchronized", () -> sync.increment(), () -> System.out.print(sync.get()));

        // --- Atomic: always 10,000, faster than synchronized ---
        AtomicCounter atomic = new AtomicCounter();
        runCounterTest("Atomic",       () -> atomic.increment(), () -> System.out.print(atomic.get()));

        // --- volatile: stop signal pattern ---
        System.out.println("\n--- volatile stop flag ---");
        Thread worker = new Thread(() -> {
            int ops = 0;
            while (running) { ops++; } // reads `running` from main memory each time
            System.out.println("[Worker] Stopped after " + ops + " ops");
        });
        worker.start();
        Thread.sleep(10);
        running = false; // visible to worker immediately because of volatile
        worker.join();

        // --- synchronized block vs method ---
        System.out.println("\n--- synchronized block (finer granularity) ---");
        Object lock = new Object();
        Runnable task = () -> {
            synchronized (lock) { // block: lock only what's needed
                System.out.println(Thread.currentThread().getName() + " in critical section");
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        };
        Thread t1 = new Thread(task, "Thread-A");
        Thread t2 = new Thread(task, "Thread-B");
        t1.start(); t2.start();
        t1.join(); t2.join();

        // TRY THIS:
        // 1. Run UnsafeCounter multiple times. Does it always produce different results?
        //    Race conditions are non-deterministic — that's what makes them dangerous.
        // 2. Replace AtomicInteger with AtomicLong.accumulateAndGet() to add a delta atomically.
        // 3. Try using volatile for a compound operation like count++.
        //    Show that it's still racy (10 threads × 1000 increments won't reach 10000).
    }
}
