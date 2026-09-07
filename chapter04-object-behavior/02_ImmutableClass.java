/**
 * CONCEPT: Immutable Classes — designing objects that never change
 *
 * An immutable object's state cannot change after construction.
 * String, Integer, BigDecimal, and LocalDate are all immutable.
 *
 * Rules for an immutable class:
 *   1. All fields are private and final
 *   2. No setters
 *   3. Class is final (or use private constructor + factory) — prevents subclassing
 *   4. Deep-copy mutable fields in constructor and defensive-copy in getters
 *   5. Methods that "change" the object return a NEW object
 *
 * Why this matters:
 *   - Immutable objects are inherently thread-safe — no synchronization needed
 *   - Safe to share and cache — no defensive copying needed by callers
 *   - Functional programming style: transformations produce new values
 *   - Java Records (Java 16+) are immutable by default — know both
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ImmutableClass {

    // --- Properly immutable class ---
    static final class Money {
        private final long   cents;    // store as cents to avoid float imprecision
        private final String currency;

        Money(long cents, String currency) {
            if (cents < 0) throw new IllegalArgumentException("Cannot be negative");
            if (currency == null || currency.isBlank()) throw new IllegalArgumentException("Currency required");
            this.cents    = cents;
            this.currency = currency;
        }

        // Factory methods (cleaner than constructors for named creation)
        static Money of(double amount, String currency) {
            return new Money(Math.round(amount * 100), currency);
        }
        static Money zero(String currency) { return new Money(0, currency); }

        // "Mutating" operations return NEW Money objects
        Money add(Money other) {
            if (!currency.equals(other.currency))
                throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
            return new Money(cents + other.cents, currency);
        }
        Money multiply(double factor) {
            return new Money(Math.round(cents * factor), currency);
        }
        Money subtract(Money other) {
            if (cents < other.cents) throw new IllegalArgumentException("Result would be negative");
            return new Money(cents - other.cents, currency);
        }

        boolean isGreaterThan(Money other) { return cents > other.cents; }

        @Override public String toString() {
            return String.format("%s %.2f", currency, cents / 100.0);
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Money m)) return false;
            return cents == m.cents && currency.equals(m.currency);
        }
        @Override public int hashCode() {
            return 31 * Long.hashCode(cents) + currency.hashCode();
        }
    }

    // --- Immutable class with a mutable field — needs defensive copy ---
    static final class ImmutableOrder {
        private final String id;
        private final List<String> items; // List is mutable — must copy!

        ImmutableOrder(String id, List<String> items) {
            this.id    = id;
            this.items = new ArrayList<>(items); // defensive copy in constructor
        }

        String getId() { return id; }

        List<String> getItems() {
            return Collections.unmodifiableList(items); // defensive copy in getter
        }

        ImmutableOrder withItem(String item) {
            List<String> newItems = new ArrayList<>(items);
            newItems.add(item);
            return new ImmutableOrder(id, newItems);  // returns new object
        }
    }

    // --- Java Record (Java 16+) — immutable by default, much less boilerplate ---
    record Point(int x, int y) {
        // Records automatically generate: constructor, getters (x(), y()), equals, hashCode, toString
        // Custom compact constructor for validation:
        Point {
            if (x < 0 || y < 0) throw new IllegalArgumentException("Coordinates must be non-negative");
        }

        Point translate(int dx, int dy) { return new Point(x + dx, y + dy); }
        double distanceTo(Point other) {
            int dx = x - other.x, dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    public static void main(String[] args) {

        System.out.println("--- Immutable Money ---");
        Money price    = Money.of(19.99, "USD");
        Money tax      = Money.of(1.60,  "USD");
        Money total    = price.add(tax);
        Money discounted = total.multiply(0.9); // 10% off

        System.out.println("Price:      " + price);
        System.out.println("Tax:        " + tax);
        System.out.println("Total:      " + total);
        System.out.println("Discounted: " + discounted);
        System.out.println("price unchanged: " + price); // still 19.99

        System.out.println("\n--- Immutable Order ---");
        ImmutableOrder order = new ImmutableOrder("ORD-1", List.of("Widget", "Gadget"));
        ImmutableOrder bigger = order.withItem("Doohickey");

        System.out.println("Original: " + order.getItems());
        System.out.println("Extended: " + bigger.getItems());
        // order.getItems().add("Hack"); // throws UnsupportedOperationException — protected!

        System.out.println("\n--- Java Record ---");
        Point p1 = new Point(3, 4);
        Point p2 = p1.translate(1, 2);
        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("Distance: " + String.format("%.2f", p1.distanceTo(p2)));
        System.out.println("p1 equals p1 copy: " + p1.equals(new Point(3, 4))); // true

        // TRY THIS:
        // 1. Try to add to order.getItems() — what exception do you get and why?
        // 2. Create a Money in EUR and try to add it to a USD Money. What happens?
        // 3. Add a `negate()` method to Money that returns a negative version.
        //    Which rule of immutable classes does this violate? How do you fix it?
    }
}
