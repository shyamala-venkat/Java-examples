/**
 * CONCEPT: Interfaces — contracts, multiple implementation, default methods
 *
 * An interface is a pure contract (mostly). Since Java 8, interfaces can also
 * have default methods (with a body) for backwards-compatible API evolution.
 *
 * A class can implement MULTIPLE interfaces — Java's answer to multiple inheritance.
 *
 * Why this matters:
 *   - "Code to the interface, not the implementation" is a senior design principle
 *   - Dependency injection relies on interfaces (inject List, not ArrayList)
 *   - Testing: mock objects implement the same interface as real objects
 *   - @FunctionalInterface enables lambda expressions
 */
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

class Interfaces {

    // --- Pure contract: what a notifier can do ---
    interface Notifier {
        void send(String recipient, String message); // implicitly public abstract

        // Default method: subclasses get this for free, but can override
        default void sendBulk(List<String> recipients, String message) {
            recipients.forEach(r -> send(r, message));
        }

        // Static factory method on the interface (Java 8+)
        static Notifier noOp() { return (r, m) -> {}; } // null object
    }

    // --- Multiple implementations ---
    static class EmailNotifier implements Notifier {
        @Override public void send(String recipient, String message) {
            System.out.println("[EMAIL → " + recipient + "] " + message);
        }
    }

    static class SlackNotifier implements Notifier {
        private final String channel;
        SlackNotifier(String channel) { this.channel = channel; }
        @Override public void send(String recipient, String message) {
            System.out.println("[SLACK #" + channel + " → " + recipient + "] " + message);
        }
    }

    // --- Composite: one class implementing multiple interfaces ---
    interface Auditable {
        String auditLog();
    }
    interface Retryable {
        int maxRetries();
        default int retryDelayMs() { return 500; } // default implementation
    }

    static class RobustEmailNotifier implements Notifier, Auditable, Retryable {
        private final List<String> log = new ArrayList<>();

        @Override public void send(String recipient, String message) {
            for (int attempt = 1; attempt <= maxRetries(); attempt++) {
                try {
                    System.out.println("[EMAIL attempt " + attempt + " → " + recipient + "] " + message);
                    log.add("SENT to " + recipient + " attempt " + attempt);
                    return; // success
                } catch (Exception e) {
                    log.add("FAIL attempt " + attempt + ": " + e.getMessage());
                    if (attempt == maxRetries()) throw e;
                }
            }
        }

        @Override public String auditLog() { return String.join(", ", log); }
        @Override public int maxRetries()  { return 3; }
        @Override public int retryDelayMs(){ return 100; } // override default
    }

    // --- Functional interface + lambda ---
    @FunctionalInterface
    interface Transformer<T, R> {
        R transform(T input);
        // Can have static and default methods — still functional (one abstract method)
        static <T> Transformer<T, T> identity() { return t -> t; }
    }

    public static void main(String[] args) {

        System.out.println("--- Interface polymorphism ---");
        Notifier email = new EmailNotifier();
        Notifier slack = new SlackNotifier("alerts");
        Notifier noOp  = Notifier.noOp();

        // Same interface call — completely different behavior
        email.send("alice@example.com", "Deploy complete");
        slack.send("Alice",              "Deploy complete");
        noOp.send("nobody",              "this does nothing");

        // Default method: bulk send — all implementations get it free
        System.out.println("\n--- Bulk send ---");
        email.sendBulk(List.of("alice@example.com", "bob@example.com"), "Server maintenance tonight");

        // --- Multiple interfaces ---
        System.out.println("\n--- Multiple interfaces ---");
        RobustEmailNotifier robust = new RobustEmailNotifier();
        robust.send("carol@example.com", "Critical alert");
        System.out.println("Audit: " + robust.auditLog());
        System.out.println("Retry delay: " + robust.retryDelayMs() + "ms");

        // --- Functional interface with lambda ---
        System.out.println("\n--- Functional Interface ---");
        Transformer<String, Integer> length = String::length;
        Transformer<String, String>  upper  = String::toUpperCase;
        Transformer<Integer, Boolean> isEven = n -> n % 2 == 0;

        System.out.println("Length of 'hello': " + length.transform("hello"));
        System.out.println("Upper 'hello':     " + upper.transform("hello"));
        System.out.println("5 is even:         " + isEven.transform(5));

        // --- Comparator is a functional interface ---
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob", "Dave"));
        names.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        System.out.println("Sorted: " + names); // by length, then alpha

        // TRY THIS:
        // 1. Add a SMSNotifier implementing Notifier. Swap it in — zero changes to calling code.
        // 2. Override sendBulk in SlackNotifier to batch all messages into one post.
        // 3. Create a Transformer<String, String> that removes vowels. Chain it with `upper`.
        //    Hint: create a compose() default method that chains two transformers.
    }
}
