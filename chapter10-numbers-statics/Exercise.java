/**
 * EXERCISE — Chapter 10: Numbers and Statics
 *
 * Problem: Thread-safe Singleton Logger
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
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class Exercise {

    enum Level {
        DEBUG(0), INFO(1), WARN(2), ERROR(3);
        final int priority;
        Level(int priority) { this.priority = priority; }
    }

    // TODO: implement enum singleton Logger with: log(Level, String), info, warn, error, debug,
    //       getLogs(), clearLogs(), setMinLevel()

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
    }
}
