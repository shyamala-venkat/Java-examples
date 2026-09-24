/**
 * EXERCISE — Chapter 15: Make a Connection (Concurrency)
 *
 * Problem 1: Producer–Consumer with BlockingQueue
 * (Classic OS concurrency problem, commonly asked at FAANG)
 * -------------------------------------------------------
 *
 * Design:
 *   - A Producer generates work items (numbers 1..N) with a simulated delay
 *   - A Consumer processes items from the queue (prints them, simulates processing)
 *   - Use LinkedBlockingQueue<Integer> (bounded, thread-safe, blocks on full/empty)
 *   - Multiple producers and multiple consumers can run concurrently
 *   - Graceful shutdown: after producer finishes, consumers drain remaining items
 *
 * BlockingQueue contract:
 *   put(item)  — add item; BLOCKS if queue is full (backpressure!)
 *   take()     — remove item; BLOCKS if queue is empty
 *   offer(item, timeout, unit) — try to add within timeout, return false if full
 *   poll(timeout, unit)        — try to take within timeout, return null if empty
 *
 * Shutdown signal: use a "poison pill" — a sentinel value (e.g., -1) that consumers
 * recognize as a signal to stop. Each producer puts one poison pill when done.
 * Each consumer that reads a poison pill puts it back (so all consumers stop).
 *
 * Requirements:
 *   - Producer: generates items 1..50, each after 10ms
 *   - Consumer: processes each item in 20ms (slower than producer — watch queue fill)
 *   - Queue capacity: 10 (bounded — puts backpressure on producers)
 *   - Run with 2 producers, 3 consumers
 *   - After all items processed, print: total produced, total consumed, time taken
 *
 * Bonus: Use AtomicInteger to count produced/consumed safely across threads.
 * Bonus: Add a monitoring thread that prints queue size every 200ms.
 *
 * Problem 2: Bounded Connection Pool (Semaphore) + Startup Barrier (CountDownLatch)
 * -------------------------------------------------------------------------------------
 * BlockingQueue solves producer-consumer; these two primitives solve different,
 * equally common problems: capping concurrent access to a limited resource, and
 * making N threads wait until everyone is ready before starting.
 *
 * 1. ConnectionPool(int maxConnections):
 *      - Semaphore permits = new Semaphore(maxConnections)
 *      - Connection acquire() throws InterruptedException — permits.acquire(),
 *        then return a (fake) Connection
 *      - void release(Connection c) — permits.release()
 *    Test: with maxConnections=3, launch 10 threads that acquire(), sleep 50ms,
 *    release(). Track the max concurrently-held count with an AtomicInteger
 *    (increment on acquire, decrement on release, record the running max) and
 *    assert it never exceeds 3.
 *
 * 2. Startup barrier: 5 "service" threads each take a random 0-200ms to become
 *    ready, then call latch.countDown(). A CountDownLatch(5) blocks a "traffic"
 *    thread via await() until all 5 have counted down. Print "All services warm
 *    — starting traffic" only after await() returns.
 *
 * 3. Bonus — reproduce then fix a deadlock: two ReentrantLocks (lockA, lockB).
 *    ThreadA acquires lockA then tries lockB; ThreadB acquires lockB then tries
 *    lockA at the same time — classic lock-ordering deadlock. Detect it with
 *    tryLock(timeout) instead of lock() so the program can print "deadlock
 *    avoided" instead of hanging forever. Then fix it for real by making BOTH
 *    threads always acquire lockA before lockB (consistent global ordering).
 */
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Chapter15Exercise {

    static final int    POISON_PILL     = -1;
    static final int    QUEUE_CAPACITY  = 10;
    static final int    ITEMS_PER_PROD  = 25;  // 2 producers × 25 = 50 total
    static final int    NUM_PRODUCERS   = 2;
    static final int    NUM_CONSUMERS   = 3;

    static final AtomicInteger produced = new AtomicInteger(0);
    static final AtomicInteger consumed = new AtomicInteger(0);

    static class Producer implements Runnable {
        private final BlockingQueue<Integer> queue;
        private final int id;

        Producer(BlockingQueue<Integer> queue, int id) {
            this.queue = queue;
            this.id    = id;
        }

        @Override public void run() {
            // TODO: generate ITEMS_PER_PROD items, put each into queue
            // Simulate 10ms production delay per item
            // After all items, put POISON_PILL into queue
            // Count produced items with atomic counter
        }
    }

    static class Consumer implements Runnable {
        private final BlockingQueue<Integer> queue;
        private final int id;

        Consumer(BlockingQueue<Integer> queue, int id) {
            this.queue = queue;
            this.id    = id;
        }

        @Override public void run() {
            // TODO: loop taking items from queue
            // If item == POISON_PILL: put it back (for other consumers) and stop
            // Otherwise: process (sleep 20ms, print), increment consumed counter
        }
    }

    // ========= Problem 2: Semaphore-bounded ConnectionPool =========
    static class Connection {
        final int id;
        Connection(int id) { this.id = id; }
    }

    static class ConnectionPool {
        private final Semaphore permits;
        private final AtomicInteger nextId = new AtomicInteger(0);

        ConnectionPool(int maxConnections) {
            this.permits = new Semaphore(maxConnections);
        }

        Connection acquire() throws InterruptedException {
            // TODO: permits.acquire(); then return new Connection(nextId.getAndIncrement());
            return null;
        }

        void release(Connection c) {
            // TODO: permits.release();
        }
    }

    // ========= Problem 2: CountDownLatch startup barrier =========
    static void runStartupBarrierDemo() throws InterruptedException {
        // TODO: CountDownLatch latch = new CountDownLatch(5);
        // TODO: launch 5 "service" threads: sleep(random 0-200ms), print "service N ready",
        //       then latch.countDown()
        // TODO: launch a "traffic" thread: latch.await(), then print
        //       "All services warm — starting traffic"
        // TODO: join all threads
    }

    // ========= Problem 2 Bonus: deadlock, then fix via lock ordering =========
    static void runDeadlockDemo() throws InterruptedException {
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();
        // TODO: ThreadA: tryLock lockA, then tryLock lockB (both with a timeout, e.g. 500ms)
        // TODO: ThreadB: tryLock lockB, then tryLock lockA (same timeout)
        // TODO: if either tryLock times out, print "deadlock avoided" and release what was held
        // TODO: join both threads
    }

    static void runFixedLockOrderingDemo() throws InterruptedException {
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();
        // TODO: BOTH threads acquire lockA before lockB (consistent global ordering) —
        //       re-run the same scenario as runDeadlockDemo() and confirm it completes
        //       cleanly every time, no timeout needed
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        ExecutorService pool = Executors.newFixedThreadPool(NUM_PRODUCERS + NUM_CONSUMERS + 1);
        long startTime = System.currentTimeMillis();

        // TODO: submit producers and consumers to the pool

        // Optional monitor thread:
        // pool.submit(() -> {
        //     while (!Thread.currentThread().isInterrupted()) {
        //         System.out.println("  [Monitor] Queue size: " + queue.size());
        //         try { Thread.sleep(200); } catch (InterruptedException e) { break; }
        //     }
        // });

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("\n=== Results ===");
        System.out.println("Produced: " + produced.get());
        System.out.println("Consumed: " + consumed.get());
        System.out.println("Time: " + elapsed + "ms");
        System.out.println("Expected: 50 produced, 50 consumed");

        System.out.println("\n=== Problem 2: Semaphore Connection Pool ===");
        // TODO: ConnectionPool pool2 = new ConnectionPool(3);
        // TODO: launch 10 threads: acquire(), track running max held (AtomicInteger),
        //       sleep 50ms, release(); join all; print observed max (expected <= 3)

        System.out.println("\n=== Problem 2: CountDownLatch Startup Barrier ===");
        runStartupBarrierDemo();

        System.out.println("\n=== Problem 2 Bonus: Deadlock, Then Fix ===");
        runDeadlockDemo();
        runFixedLockOrderingDemo();
    }
}
