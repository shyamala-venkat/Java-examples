/**
 * CONCEPT: Thread Fundamentals — creation, lifecycle, joining
 *
 * A thread is an independent path of execution within a program.
 * The JVM starts with the main thread; you create more with Thread or Runnable.
 *
 * Why this matters:
 *   - Concurrency is a senior-level mandatory topic
 *   - Thread lifecycle is a common interview diagram question
 *   - Daemon vs non-daemon: JVM exits when only daemon threads remain
 *   - Thread.join() is the primitive for waiting — wrong use causes deadlocks
 */
class ThreadBasics {

    // Way 1: extend Thread — tightly couples task and execution mechanism
    static class DownloadTask extends Thread {
        private final String url;
        private String result;

        DownloadTask(String url) {
            super("Downloader-" + url.hashCode()); // set thread name for debugging
            this.url = url;
        }

        @Override public void run() {
            System.out.println("[" + getName() + "] Starting download: " + url);
            try { Thread.sleep((long)(Math.random() * 300 + 100)); } // simulate network delay
            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            result = "DATA_FROM_" + url;
            System.out.println("[" + getName() + "] Finished: " + url);
        }

        String getResult() { return result; }
    }

    // Way 2: implement Runnable — preferred, decouples task from thread
    static class LogProcessor implements Runnable {
        private final String logLine;
        LogProcessor(String line) { this.logLine = line; }

        @Override public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] Processing: " + logLine.toUpperCase());
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== Thread Lifecycle ===");
        System.out.println("NEW → RUNNABLE → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED");
        System.out.println();

        // --- Thread creation and lifecycle ---
        System.out.println("--- Way 1: extend Thread ---");
        DownloadTask t1 = new DownloadTask("https://api.example.com/data");
        DownloadTask t2 = new DownloadTask("https://cdn.example.com/image.jpg");

        System.out.println("State before start: " + t1.getState()); // NEW
        t1.start();
        t2.start();
        System.out.println("State after start:  " + t1.getState()); // RUNNABLE or TIMED_WAITING

        // join() blocks the current thread until t1 and t2 finish
        t1.join();
        t2.join();
        System.out.println("t1 result: " + t1.getResult());
        System.out.println("t2 result: " + t2.getResult());
        System.out.println("State after join:   " + t1.getState()); // TERMINATED

        // --- Way 2: Runnable (preferred) ---
        System.out.println("\n--- Way 2: Runnable ---");
        String[] logs = {"user login", "file read", "db query", "api call"};
        Thread[] workers = new Thread[logs.length];
        for (int i = 0; i < logs.length; i++) {
            workers[i] = new Thread(new LogProcessor(logs[i]), "Worker-" + i);
            workers[i].start();
        }
        for (Thread w : workers) w.join(); // wait for all

        // --- Lambda as Runnable (most concise) ---
        System.out.println("\n--- Lambda Runnable ---");
        Thread lambda = new Thread(() -> {
            System.out.println("[Lambda] Running on: " + Thread.currentThread().getName());
        }, "LambdaThread");
        lambda.start();
        lambda.join();

        // --- Daemon threads ---
        System.out.println("\n--- Daemon Thread ---");
        Thread daemon = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("[Daemon] Heartbeat — " + System.currentTimeMillis());
                    Thread.sleep(200);
                } catch (InterruptedException e) { break; }
            }
        });
        daemon.setDaemon(true);    // must be set BEFORE start()
        daemon.start();
        Thread.sleep(500);         // let daemon run briefly
        System.out.println("Main thread exiting — daemon will be killed automatically");

        // TRY THIS:
        // 1. Remove t1.join() and t2.join(). Do you always get results? Why not?
        // 2. Call t1.start() a second time — what exception do you get?
        // 3. Set workers[0].setPriority(Thread.MAX_PRIORITY). Does execution order change?
        //    Why is priority just a hint, not a guarantee?
    }
}
