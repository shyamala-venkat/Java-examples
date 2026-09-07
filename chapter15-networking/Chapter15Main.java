/**
 * Chapter 15: Make a Connection — Sockets and Threads
 *
 * Key Concepts:
 *  - Sockets: client-server communication over TCP/IP
 *  - ServerSocket — server side, waits for connections
 *  - Socket — client side, connects to server
 *  - Threads: running multiple tasks concurrently
 *  - Runnable interface — define a task to run in a thread
 *  - Thread class — wraps a Runnable and provides control
 *  - Thread states: New, Runnable, Blocked, Waiting, Terminated
 *  - Basic thread safety concerns (preview — deep dive in Chapter 18)
 *  - ExecutorService (Java 5+) — preferred over raw Thread
 *
 * How to run:
 *   javac Chapter15Main.java
 *   java Chapter15Main
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Chapter15Main {

    // -------------------------------------------------------
    // CONCEPT 1: Thread — running code concurrently
    //
    // Two ways to create a thread:
    //   1. Extend Thread class (rarely preferred)
    //   2. Implement Runnable (preferred — separates task from thread)
    // -------------------------------------------------------

    // Way 1: Extending Thread (not preferred — ties task to thread lifecycle)
    static class CounterThread extends Thread {
        private String name;
        private int from;
        private int to;
        private int delayMs;

        public CounterThread(String name, int from, int to, int delayMs) {
            super(name); // sets thread name
            this.name = name;
            this.from = from;
            this.to = to;
            this.delayMs = delayMs;
        }

        @Override
        public void run() {
            for (int i = from; i <= to; i++) {
                System.out.println("[" + name + "] counting: " + i);
                try {
                    Thread.sleep(delayMs); // pause this thread for delayMs milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupted status
                    System.out.println("[" + name + "] was interrupted!");
                    return;
                }
            }
            System.out.println("[" + name + "] done!");
        }
    }

    // Way 2: Implementing Runnable (preferred — task is separate from thread)
    static class PrinterTask implements Runnable {
        private String message;
        private int times;

        public PrinterTask(String message, int times) {
            this.message = message;
            this.times = times;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                System.out.println("[" + Thread.currentThread().getName() + "] " + message);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Simple Echo Server (TCP)
    //
    // ServerSocket listens on a port.
    // accept() blocks until a client connects, then returns a Socket.
    // -------------------------------------------------------
    static class EchoServer implements Runnable {
        private int port;
        private volatile boolean running = true; // volatile = visible across threads

        public EchoServer(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("[Server] Listening on port " + port);
                serverSocket.setSoTimeout(5000); // 5 second timeout to allow shutdown

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept(); // blocks until client connects
                        System.out.println("[Server] Client connected: " + clientSocket.getInetAddress());

                        // Handle client in a new thread — otherwise only 1 client at a time
                        new Thread(() -> handleClient(clientSocket)).start();

                    } catch (SocketTimeoutException e) {
                        // Timeout — check if we should keep running
                    }
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("[Server] Error: " + e.getMessage());
                }
            }
            System.out.println("[Server] Shut down.");
        }

        private void handleClient(Socket socket) {
            try (socket;
                 BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)) {

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[Server] Received: " + line);
                    out.println("ECHO: " + line); // echo back to client
                    if (line.equals("bye")) break;
                }
            } catch (IOException e) {
                System.err.println("[Server] Client error: " + e.getMessage());
            }
            System.out.println("[Server] Client disconnected.");
        }

        public void stop() { running = false; }
    }

    // -------------------------------------------------------
    // CONCEPT 3: Simple Echo Client (TCP)
    // -------------------------------------------------------
    static void runEchoClient(int port, String... messages) {
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out  = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("[Client] Connected to server on port " + port);

            for (String msg : messages) {
                out.println(msg);                         // send to server
                String response = in.readLine();         // receive echo
                System.out.println("[Client] Response: " + response);
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("[Client] Error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CONCEPT 4: ExecutorService — managing thread pools
    //
    // Creating raw threads for every task is expensive.
    // ExecutorService manages a pool of threads that are reused.
    // Modern Java best practice — use this instead of new Thread().
    // -------------------------------------------------------
    static void executorServiceDemo() throws InterruptedException {
        System.out.println("\n--- ExecutorService (Thread Pool) ---");

        // Fixed thread pool: at most 3 threads running at once
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit 6 tasks — pool reuses threads as they finish
        for (int i = 1; i <= 6; i++) {
            final int taskNum = i;
            executor.submit(() -> {
                System.out.printf("[%s] Task %d started%n",
                    Thread.currentThread().getName(), taskNum);
                try { Thread.sleep((long)(Math.random() * 200)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.printf("[%s] Task %d done%n",
                    Thread.currentThread().getName(), taskNum);
            });
        }

        // Shutdown: no new tasks, wait for existing to finish
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("All tasks completed.");
    }

    // -------------------------------------------------------
    // CONCEPT 5: Callable and Future — tasks that return a result
    // -------------------------------------------------------
    static void callableFutureDemo() throws ExecutionException, InterruptedException {
        System.out.println("\n--- Callable + Future (tasks with results) ---");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Callable returns a result (unlike Runnable)
        Future<Integer> future1 = executor.submit(() -> {
            Thread.sleep(100);
            return 42 * 42; // compute result
        });

        Future<String> future2 = executor.submit(() -> {
            Thread.sleep(50);
            return "Hello from task!";
        });

        // get() blocks until the result is available
        System.out.println("future1 result: " + future1.get()); // 1764
        System.out.println("future2 result: " + future2.get()); // Hello from task!

        executor.shutdown();
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=== Chapter 15: Make a Connection ===\n");

        // -------------------------------------------------------
        // Thread demo — multiple threads running concurrently
        // -------------------------------------------------------
        System.out.println("--- Thread Basics ---");

        // Way 1: extending Thread
        CounterThread t1 = new CounterThread("FastCounter", 1, 5, 10);
        CounterThread t2 = new CounterThread("SlowCounter", 10, 14, 30);
        t1.start(); // starts the thread — calls run() concurrently
        t2.start();

        // current thread (main) continues here while t1 and t2 run
        System.out.println("[main] Threads started, main continues...");

        t1.join(); // wait for t1 to finish
        t2.join(); // wait for t2 to finish
        System.out.println("[main] Both counter threads finished.");

        // Way 2: Runnable with Thread
        System.out.println("\n--- Runnable interface ---");
        Runnable task = new PrinterTask("Working...", 3);
        Thread worker1 = new Thread(task, "Worker-1");
        Thread worker2 = new Thread(task, "Worker-2"); // same Runnable, different thread!
        worker1.start();
        worker2.start();
        worker1.join();
        worker2.join();

        // Lambda as Runnable (most concise)
        System.out.println("\n--- Lambda as Runnable ---");
        Thread lambdaThread = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("[LambdaThread] step " + i);
            }
        });
        lambdaThread.start();
        lambdaThread.join();

        // -------------------------------------------------------
        // Socket client-server demo
        // -------------------------------------------------------
        System.out.println("\n--- Socket Client-Server Demo ---");
        int port = 9999;
        EchoServer server = new EchoServer(port);
        Thread serverThread = new Thread(server, "EchoServer");
        serverThread.setDaemon(true); // daemon thread dies when main thread dies
        serverThread.start();

        Thread.sleep(200); // give server time to start

        runEchoClient(port, "Hello, Server!", "How are you?", "bye");

        Thread.sleep(200);
        server.stop();

        // -------------------------------------------------------
        // ExecutorService demo
        // -------------------------------------------------------
        executorServiceDemo();
        callableFutureDemo();

        // -------------------------------------------------------
        // Thread states summary
        // -------------------------------------------------------
        System.out.println("\n--- Thread Lifecycle ---");
        System.out.println("NEW        -> Thread created with new Thread(), not yet started");
        System.out.println("RUNNABLE   -> After start(), eligible to run (may be waiting for CPU)");
        System.out.println("BLOCKED    -> Waiting to acquire a lock (synchronized block)");
        System.out.println("WAITING    -> Waiting indefinitely (wait(), join() without timeout)");
        System.out.println("TIMED_WAIT -> Waiting with timeout (sleep(), join(ms), wait(ms))");
        System.out.println("TERMINATED -> run() has completed");

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a multi-threaded word counter:");
        System.out.println("  - Given a large list of sentences, split into 4 chunks");
        System.out.println("  - Process each chunk in its own thread using Callable<Integer>");
        System.out.println("    (count words in that chunk)");
        System.out.println("  - Use ExecutorService with 4 threads");
        System.out.println("  - Use Future<Integer> to collect results");
        System.out.println("  - Sum all Future results for the total word count");
        System.out.println("  - Compare time with sequential counting");

        // TODO: EXERCISE — implement multi-threaded word counter here
    }
}
