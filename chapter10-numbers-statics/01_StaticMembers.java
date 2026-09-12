/**
 * CONCEPT: Static Variables, Methods, and Initialization
 *
 * static = belongs to the CLASS, not to any instance.
 * One copy shared across all instances.
 *
 * Why this matters:
 *   - Static variables for shared state: counters, caches, registries
 *   - Static initialization blocks for complex setup
 *   - Thread-safety: static mutable state shared across threads = race condition
 *   - Memory leaks: static references prevent GC — a common production issue
 */
import java.util.HashMap;
import java.util.Map;

class StaticMembers {

    // --- Singleton using static (double-checked locking for thread safety) ---
    static class MetricsRegistry {
        private static volatile MetricsRegistry instance; // volatile: visibility across threads
        private final Map<String, Long> counters = new HashMap<>();

        private MetricsRegistry() {}  // private: only we can instantiate

        // Thread-safe lazy initialization
        static MetricsRegistry getInstance() {
            if (instance == null) {                    // first check — no lock (fast path)
                synchronized (MetricsRegistry.class) { // lock only when null
                    if (instance == null) {            // second check — inside lock (safe)
                        instance = new MetricsRegistry();
                    }
                }
            }
            return instance;
        }

        void increment(String metric)             { counters.merge(metric, 1L, Long::sum); }
        void increment(String metric, long delta) { counters.merge(metric, delta, Long::sum); }
        long get(String metric)                   { return counters.getOrDefault(metric, 0L); }
        Map<String, Long> snapshot()              { return new HashMap<>(counters); }
    }

    // --- Static factory methods: prefer over constructors for named creation ---
    static class Color {
        private final int r, g, b;

        private Color(int r, int g, int b) {
            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
                throw new IllegalArgumentException("RGB values must be 0-255");
            this.r = r; this.g = g; this.b = b;
        }

        // Static factory: clear names, can cache, can return subtypes
        static Color rgb(int r, int g, int b) { return new Color(r, g, b); }
        static Color hex(String hex) {
            int v = Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
            return new Color((v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF);
        }

        // Cached constants (static final)
        static final Color RED   = rgb(255, 0, 0);
        static final Color GREEN = rgb(0, 255, 0);
        static final Color BLUE  = rgb(0, 0, 255);
        static final Color BLACK = rgb(0, 0, 0);
        static final Color WHITE = rgb(255, 255, 255);

        Color blend(Color other) {
            return Color.rgb((r + other.r) / 2, (g + other.g) / 2, (b + other.b) / 2);
        }

        @Override public String toString() {
            return String.format("Color(#%02X%02X%02X)", r, g, b);
        }
    }

    // --- Static initialization block: runs once when class is loaded ---
    static class Config {
        static final Map<String, String> DEFAULTS;
        static final int MAX_CONNECTIONS;

        static {
            // Complex initialization that can't be done in a field declaration
            DEFAULTS = new HashMap<>();
            DEFAULTS.put("timeout", "5000");
            DEFAULTS.put("retries", "3");
            DEFAULTS.put("env", System.getenv("APP_ENV") != null ? System.getenv("APP_ENV") : "production");
            MAX_CONNECTIONS = Integer.parseInt(DEFAULTS.get("retries")) * 10; // depends on other value
            System.out.println("[Config] Initialized: env=" + DEFAULTS.get("env"));
        }
    }

    public static void main(String[] args) {

        System.out.println("--- Singleton Metrics Registry ---");
        MetricsRegistry registry = MetricsRegistry.getInstance();
        registry.increment("api.requests");
        registry.increment("api.requests");
        registry.increment("db.queries", 5);

        MetricsRegistry same = MetricsRegistry.getInstance();
        System.out.println("Same instance: " + (registry == same)); // true
        System.out.println("Metrics: " + registry.snapshot());

        System.out.println("\n--- Static Factory Methods ---");
        Color red    = Color.RED;
        Color blue   = Color.BLUE;
        Color purple = red.blend(blue);
        Color custom = Color.hex("#FF8800");

        System.out.println("Red:    " + red);
        System.out.println("Blue:   " + blue);
        System.out.println("Purple: " + purple);
        System.out.println("Custom: " + custom);

        System.out.println("\n--- Static Initialization Block ---");
        System.out.println("Config loaded. Max connections: " + Config.MAX_CONNECTIONS);
        System.out.println("Defaults: " + Config.DEFAULTS);

        // TRY THIS:
        // 1. Make MetricsRegistry.increment() thread-safe using AtomicLong or ConcurrentHashMap.
        //    Create two threads that each increment "requests" 1000 times.
        //    Without synchronization, the final count will be < 2000 due to race conditions.
        // 2. Add a reset() method to MetricsRegistry — what singleton problems does this create?
        // 3. Add a Color.fromHsl(double h, double s, double l) factory method.
        //    Look up the HSL to RGB formula.
    }
}
