/**
 * CONCEPT: Constructor Chaining — this() and super()
 *
 * Constructor chaining avoids duplicating initialization logic.
 * this()  → calls another constructor in the SAME class (must be first line)
 * super() → calls a constructor in the PARENT class (must be first line)
 *
 * Java inserts super() automatically if you don't — but only if the parent
 * has a no-arg constructor. If the parent doesn't, YOU must call super(...) explicitly.
 *
 * Why this matters:
 *   - Forgetting super() when the parent has no default constructor = compile error
 *   - Calling overridable methods from a constructor = subtle initialization order bug
 *   - Constructor chaining with validation: one constructor does all the real work
 */
class ConstructorChaining {

    static class Connection {
        private final String host;
        private final int    port;
        private final int    timeoutMs;
        private final String protocol;
        private final boolean ssl;

        // The "canonical" constructor — all validation here
        Connection(String host, int port, int timeoutMs, String protocol, boolean ssl) {
            if (host == null || host.isBlank()) throw new IllegalArgumentException("host required");
            if (port <= 0 || port > 65535)     throw new IllegalArgumentException("invalid port");
            if (timeoutMs < 0)                  throw new IllegalArgumentException("timeout negative");
            this.host      = host;
            this.port      = port;
            this.timeoutMs = timeoutMs;
            this.protocol  = protocol;
            this.ssl       = ssl;
        }

        // Convenience overloads — all delegate to canonical constructor
        Connection(String host, int port)           { this(host, port, 5_000, "HTTP", false); }
        Connection(String host, int port, boolean ssl){ this(host, port, 5_000, ssl ? "HTTPS" : "HTTP", ssl); }
        Connection(String host)                      { this(host, 80); }

        @Override public String toString() {
            return (ssl ? "https" : "http") + "://" + host + ":" + port
                 + " timeout=" + timeoutMs + "ms";
        }
    }

    // --- super() in inheritance ---
    static class DatabaseConnection extends Connection {
        private final String database;

        DatabaseConnection(String host, int port, String database) {
            super(host, port, 30_000, "JDBC", true); // MUST be first — calls Connection(...)
            this.database = database;
        }

        DatabaseConnection(String host, String database) {
            this(host, 5432, database); // this() -> calls DatabaseConnection(host, port, db)
        }

        @Override public String toString() {
            return super.toString() + "/" + database;
        }
    }

    // --- Constructor order bug: don't call overridable methods from constructor ---
    static class Base {
        int value;
        Base() {
            init(); // DANGER: calls subclass's overridden init() before subclass is initialized!
        }
        void init() { value = 1; System.out.println("Base.init() value=" + value); }
    }

    static class Derived extends Base {
        int multiplier = 5; // this is initialized AFTER super() completes
        @Override void init() {
            value = multiplier * 10; // multiplier is still 0 here! not yet initialized
            System.out.println("Derived.init() value=" + value + " (multiplier=" + multiplier + ")");
        }
    }

    public static void main(String[] args) {

        System.out.println("--- Constructor Chaining ---");
        System.out.println(new Connection("api.example.com"));
        System.out.println(new Connection("api.example.com", 8080));
        System.out.println(new Connection("api.example.com", 443, true));
        System.out.println(new Connection("db.example.com", 5432, 15_000, "JDBC", true));

        System.out.println("\n--- super() in inheritance ---");
        System.out.println(new DatabaseConnection("localhost", "mydb"));
        System.out.println(new DatabaseConnection("prod.db.example.com", 5432, "orders"));

        System.out.println("\n--- Constructor order bug (don't call overridable methods) ---");
        Derived d = new Derived();
        System.out.println("After construction, value=" + d.value); // likely 0, not 50
        System.out.println("After construction, multiplier=" + d.multiplier); // 5

        // TRY THIS:
        // 1. Add a Connection(String url) constructor that parses "host:port" from a URL string.
        //    It should delegate to this(host, port). Handle malformed URLs.
        // 2. Fix the Derived initialization bug: move the multiplier initialization logic
        //    into an instance initializer block instead. Does it help?
        // 3. Make Connection implement AutoCloseable with a close() method.
        //    Test it with try-with-resources.
    }
}
