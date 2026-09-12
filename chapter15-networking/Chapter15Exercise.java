/**
 * EXERCISE — Chapter 15: Make a Connection (Concurrency)
 *
 * Problem: Producer–Consumer with BlockingQueue
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
 */
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    }
}
