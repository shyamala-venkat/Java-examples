/**
 * CONCEPT: try-with-resources and AutoCloseable
 *
 * Any class implementing AutoCloseable can be used in try-with-resources.
 * close() is called automatically when the try block exits — even on exception.
 *
 * Why this matters:
 *   - Resource leaks (open file handles, DB connections) are silent production killers
 *   - The old finally-close pattern has subtle bugs (exception in close() hides original)
 *   - try-with-resources handles this correctly with suppressed exceptions
 *   - Multiple resources closed in REVERSE order of declaration
 */
class TryWithResources {

    // --- Any class implementing AutoCloseable works ---
    static class DatabaseConnection implements AutoCloseable {
        private final String url;
        private boolean closed = false;

        DatabaseConnection(String url) {
            System.out.println("  [DB] Opened connection to: " + url);
            this.url = url;
        }

        String query(String sql) {
            if (closed) throw new IllegalStateException("Connection is closed");
            System.out.println("  [DB] Executing: " + sql);
            return "result_of_" + sql.hashCode();
        }

        @Override public void close() {
            if (!closed) {
                closed = true;
                System.out.println("  [DB] Connection closed: " + url);
            }
        }
    }

    static class FileWriter implements AutoCloseable {
        private final String filename;
        private boolean closed = false;

        FileWriter(String filename) {
            System.out.println("  [File] Opened: " + filename);
            this.filename = filename;
        }

        void write(String line) {
            if (closed) throw new IllegalStateException("File is closed");
            System.out.println("  [File] Writing: " + line);
        }

        @Override public void close() {
            if (!closed) {
                closed = true;
                System.out.println("  [File] Closed: " + filename);
            }
        }
    }

    // --- A resource whose close() throws — suppressed exception demo ---
    static class FlakeyResource implements AutoCloseable {
        FlakeyResource()     { System.out.println("  [Flakey] Opened"); }
        void doWork()        { System.out.println("  [Flakey] Working..."); throw new RuntimeException("work failed"); }
        @Override public void close() {
            System.out.println("  [Flakey] Closing...");
            throw new RuntimeException("close also failed!");
        }
    }

    static String readFromDb(String url, String sql) {
        // Old way (fragile — exception in close() is silently swallowed):
        // DatabaseConnection conn = null;
        // try { conn = new DatabaseConnection(url); return conn.query(sql); }
        // finally { if (conn != null) conn.close(); }  // BUG if close() throws

        // New way — clean, correct
        try (DatabaseConnection conn = new DatabaseConnection(url)) {
            return conn.query(sql);
        } // close() called here even if query() throws
    }

    public static void main(String[] args) {

        System.out.println("--- Basic try-with-resources ---");
        String result = readFromDb("jdbc:postgresql://localhost/mydb", "SELECT * FROM users");
        System.out.println("Result: " + result);

        System.out.println("\n--- Multiple resources (closed in REVERSE order) ---");
        try (DatabaseConnection db   = new DatabaseConnection("prod-db");
             FileWriter         file = new FileWriter("output.csv")) {
            // db opened first, file second
            String data = db.query("SELECT name, email FROM users");
            file.write("name,email");
            file.write(data);
            System.out.println("Processing complete");
        } // file closed first, then db — reverse of declaration

        System.out.println("\n--- Suppressed exceptions ---");
        try (FlakeyResource res = new FlakeyResource()) {
            res.doWork(); // throws "work failed"
            // close() is then called automatically, also throws "close also failed!"
            // try-with-resources attaches the close() exception as SUPPRESSED to the primary
        } catch (RuntimeException e) {
            System.out.println("Primary: " + e.getMessage());
            System.out.println("Suppressed: " + e.getSuppressed()[0].getMessage());
        }

        System.out.println("\n--- Re-use resource with try-with-resources (Java 9+) ---");
        // In Java 9+, you can use an effectively-final variable directly
        DatabaseConnection existingConn = new DatabaseConnection("existing-db");
        try (existingConn) { // no need to declare new variable
            System.out.println(existingConn.query("SELECT 1"));
        }

        // TRY THIS:
        // 1. Add a connection pool: ConnectionPool that gives out DatabaseConnections.
        //    Returning a connection to the pool should happen in close().
        //    This is the exact pattern used in HikariCP, the most popular Java connection pool.
        // 2. What happens if you try to use a DatabaseConnection after the try block?
        //    It's closed — call query() and observe the error.
        // 3. Make FlakeyResource not throw in close() and observe that suppressed[] is empty.
    }
}
