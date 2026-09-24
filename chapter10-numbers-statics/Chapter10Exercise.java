/**
 * EXERCISE — Chapter 10: Numbers and Statics
 *
 * Problem 1: Thread-safe Singleton Logger
 * --------------------------------------
 * Build a Logger that is:
 *   1. A singleton (only one instance per JVM)
 *   2. Thread-safe (multiple threads can log concurrently)
 *   3. Supports log levels: DEBUG, INFO, WARN, ERROR
 *   4. Filters by a configurable minimum level
 *   5. Formats messages: "[LEVEL] timestamp - message"
 *   6. Collects logs in memory (for testing) — retrievable as a List<String>
 *
 * Singleton strategies (know all three):
 *   - Eager: static final field = new Logger() — simple, always created
 *   - Lazy with double-checked locking: created on first use (shown in 01_StaticMembers)
 *   - Enum singleton: best for thread safety + serialization safety (implement this one)
 *
 * Enum singleton:
 *   enum Logger {
 *     INSTANCE;
 *     // fields and methods here
 *   }
 *   Usage: Logger.INSTANCE.info("message")
 *
 * Also implement:
 *   - format(Level level, String msg) → "[WARN] 2024-01-15T10:30:00 - msg"
 *   - Level enum with: DEBUG(0), INFO(1), WARN(2), ERROR(3) with an `int priority` field
 *   - setMinLevel(Level l) — only log messages at or above this level
 *   - clearLogs() — for test cleanup
 *
 * Test with multiple threads:
 *   Launch 5 threads, each logging 10 messages at different levels.
 *   Verify total log count with minLevel=INFO is correct (no DEBUG messages).
 *
 * Problem 2: Static Field Memory Leak — Find It, Fix It
 * ---------------------------------------------------------
 * "static references prevent GC" is easy to say and easy to ship by accident.
 * LeakyCache below is a real anti-pattern: a static Map that every call adds
 * to and NOTHING ever removes from — it grows for the entire life of the JVM,
 * because the static field is a GC root that keeps every entry reachable forever.
 *
 * 1. LeakyCache: static final Map<String, byte[]> cache = new HashMap<>();
 *    static void put(String key, int sizeBytes) — cache.put(key, new byte[sizeBytes])
 *    (no eviction, no size bound — this IS the bug; given, don't fix this class)
 *
 * 2. Demonstrate the leak: call put() 10,000 times with unique keys and ~1KB
 *    payloads each; print cache.size() before and after — confirm nothing
 *    was ever evicted (size == 10,000).
 *
 * 3. Fix it — implement BoundedCache using a LinkedHashMap with
 *    removeEldestEntry() overridden to evict once size exceeds MAX_ENTRIES.
 *    (WeakHashMap is the OTHER textbook answer here, but it only helps once
 *    NOTHING else holds a strong reference to the key — for a cache keyed by a
 *    String you construct fresh each call, that's rarely true in practice, so
 *    a bounded eviction policy is the more realistic production fix. Note this
 *    tradeoff in a comment.)
 *
 * 4. Repeat the 10,000-put test against BoundedCache(maxEntries=1000); confirm
 *    size never exceeds 1000.
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class Chapter10Exercise {

    enum Level {
        DEBUG(0), INFO(1), WARN(2), ERROR(3);
        final int priority;
        Level(int priority) { this.priority = priority; }
    }

    // TODO: implement enum singleton Logger with: log(Level, String), info, warn, error, debug,
    //       getLogs(), clearLogs(), setMinLevel()

    // ========= Problem 2: given buggy baseline — do not fix this class =========
    static class LeakyCache {
        static final Map<String, byte[]> cache = new HashMap<>();

        static void put(String key, int sizeBytes) {
            cache.put(key, new byte[sizeBytes]);
        }
    }

    // ========= Problem 2: TODO — fix using bounded eviction =========
    static class BoundedCache {
        private final int maxEntries;
        // TODO: private final LinkedHashMap<String, byte[]> cache =
        //     new LinkedHashMap<>(16, 0.75f, false) {
        //         @Override protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
        //             return size() > maxEntries;
        //         }
        //     };

        BoundedCache(int maxEntries) { this.maxEntries = maxEntries; }

        void put(String key, int sizeBytes) {
            // TODO: cache.put(key, new byte[sizeBytes]);
        }

        int size() {
            // TODO: return cache.size();
            return -1;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // TODO: uncomment and make these work after implementing Logger

        // Logger.INSTANCE.setMinLevel(Level.INFO);
        // Logger.INSTANCE.info("Application starting");
        // Logger.INSTANCE.debug("Debug message — should be filtered");
        // Logger.INSTANCE.warn("Low memory");
        // Logger.INSTANCE.error("Connection failed");

        // System.out.println("Logs collected: " + Logger.INSTANCE.getLogs().size()); // 3 (no debug)

        // --- Concurrent test ---
        // ExecutorService pool = Executors.newFixedThreadPool(5);
        // CountDownLatch latch = new CountDownLatch(5);
        // for (int t = 0; t < 5; t++) {
        //     final int threadId = t;
        //     pool.submit(() -> {
        //         for (int i = 0; i < 10; i++) {
        //             if (i % 3 == 0) Logger.INSTANCE.debug("thread " + threadId + " debug " + i);
        //             else if (i % 2 == 0) Logger.INSTANCE.info("thread " + threadId + " info " + i);
        //             else Logger.INSTANCE.warn("thread " + threadId + " warn " + i);
        //         }
        //         latch.countDown();
        //     });
        // }
        // latch.await();
        // pool.shutdown();
        // System.out.println("Total logs (minLevel=INFO): " + Logger.INSTANCE.getLogs().size());
        // Logger.INSTANCE.getLogs().forEach(System.out::println);

        System.out.println("\n=== Problem 2: Static Field Memory Leak ===");
        for (int i = 0; i < 10_000; i++) {
            LeakyCache.put("key-" + i, 1024);
        }
        System.out.println("LeakyCache size after 10,000 puts: " + LeakyCache.cache.size() +
            " (expected 10,000 — nothing was ever evicted)");

        BoundedCache bounded = new BoundedCache(1000);
        for (int i = 0; i < 10_000; i++) {
            bounded.put("key-" + i, 1024);
        }
        System.out.println("BoundedCache size after 10,000 puts: " + bounded.size() +
            " (expected 1000 — capped by eviction)");
    }
}
