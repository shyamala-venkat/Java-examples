/**
 * CONCEPT: Null — the billion-dollar mistake and how to handle it
 *
 * Tony Hoare called null his "billion-dollar mistake." NullPointerException (NPE)
 * is still the #1 runtime exception in Java production systems.
 *
 * Senior engineers avoid null defensively using:
 *   - Null checks with early returns
 *   - Optional<T> (Java 8+) for values that may be absent
 *   - Objects.requireNonNull for fail-fast validation
 *   - Null Object pattern to eliminate null checks entirely
 *
 * Why this matters:
 *   - "What if it's null?" is a real interview question for any method you write
 *   - Returning null from a method is a design smell — prefer Optional or throw
 *   - The JDK @Nullable/@NonNull annotations signal intent
 */
import java.util.Objects;
import java.util.Optional;

class NullHandling {

    // --- Bad: returns null — caller must remember to check ---
    static String findUserBad(int id) {
        if (id == 1) return "Alice";
        return null; // caller forgets to check → NPE
    }

    // --- Better: return Optional<String> — forces caller to handle absence ---
    static Optional<String> findUser(int id) {
        if (id == 1) return Optional.of("Alice");
        return Optional.empty();
    }

    // --- Null Object pattern: return a "do-nothing" object instead of null ---
    interface Logger {
        void log(String msg);
    }
    static class ConsoleLogger implements Logger {
        @Override public void log(String msg) { System.out.println("[LOG] " + msg); }
    }
    static class NoOpLogger implements Logger {
        @Override public void log(String msg) {} // does nothing — no NPE risk
    }

    static class Service {
        private final Logger logger;
        // If no logger provided, use NoOpLogger (never null)
        Service(Logger logger) {
            this.logger = (logger != null) ? logger : new NoOpLogger();
        }
        void process(String data) {
            logger.log("Processing: " + data); // safe — never null
            System.out.println("Done: " + data);
        }
    }

    public static void main(String[] args) {

        // --- NPE demo ---
        System.out.println("--- NPE ---");
        String user = findUserBad(99);
        // user.length();  // NPE! Don't do this without a null check

        // --- Defensive null check (classic) ---
        if (user != null) {
            System.out.println("Found: " + user);
        } else {
            System.out.println("User not found");
        }

        // --- Objects.requireNonNull: fail fast in constructors/setters ---
        System.out.println("\n--- requireNonNull ---");
        try {
            String name = Objects.requireNonNull(null, "name must not be null");
        } catch (NullPointerException e) {
            System.out.println("Caught: " + e.getMessage()); // name must not be null
        }

        // --- Optional: the right way to express "might not exist" ---
        System.out.println("\n--- Optional ---");

        Optional<String> found = findUser(1);
        Optional<String> notFound = findUser(99);

        // isPresent / get
        System.out.println("found.isPresent(): " + found.isPresent());
        found.ifPresent(u -> System.out.println("User: " + u));

        // orElse: default value if absent
        String name = notFound.orElse("Anonymous");
        System.out.println("orElse: " + name);

        // orElseGet: lazy default (computed only if needed)
        String computed = notFound.orElseGet(() -> "Guest-" + System.currentTimeMillis());
        System.out.println("orElseGet: " + computed);

        // orElseThrow: throw if absent (use when absence is an error)
        try {
            notFound.orElseThrow(() -> new IllegalStateException("User must exist"));
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // map: transform the value if present
        Optional<Integer> nameLen = found.map(String::length);
        System.out.println("Name length: " + nameLen.orElse(0)); // 5 ("Alice")

        // --- Null Object pattern ---
        System.out.println("\n--- Null Object Pattern ---");
        Service withLogger = new Service(new ConsoleLogger());
        Service withoutLogger = new Service(null); // no NPE — uses NoOpLogger

        withLogger.process("order-123");
        withoutLogger.process("order-456"); // no log output, but no crash

        // TRY THIS:
        // 1. Add a findUserByName(String name) method — what should it return if
        //    the name is null vs if the name isn't found? Are these the same case?
        // 2. Chain Optional: findUser(1).map(String::toUpperCase).filter(u -> u.startsWith("A"))
        //    What does this return? What about findUser(99) with the same chain?
        // 3. Rewrite findUserBad to throw NoSuchElementException instead of returning null.
        //    Which is a better API contract and why?
    }
}
