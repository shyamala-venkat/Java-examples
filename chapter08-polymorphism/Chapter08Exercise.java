/**
 * EXERCISE — Chapter 8: Serious Polymorphism
 *
 * Problem 1: Notification System with Strategy + Observer Pattern
 * -------------------------------------------------------------
 * Build a notification system where:
 *
 * 1. NotificationChannel interface:
 *      void send(Notification n)
 *      String channelName()
 *
 * 2. Implementations: EmailChannel, SMSChannel, PushChannel
 *    Each prints a different formatted message.
 *
 * 3. Notification record: type (String), title (String), body (String), priority (1-5)
 *
 * 4. NotificationRouter (Strategy pattern):
 *    - Holds a routing rule: Predicate<Notification> → determines if this channel handles it
 *    - NotificationRouter(NotificationChannel channel, Predicate<Notification> rule)
 *    - boolean canHandle(Notification n) { return rule.test(n); }
 *    - void route(Notification n) { if (canHandle(n)) channel.send(n); }
 *
 * 5. NotificationDispatcher:
 *    - Holds a List<NotificationRouter>
 *    - void dispatch(Notification n) — routes to ALL matching channels
 *    - void addRouter(NotificationRouter r)
 *
 * Test it with these routing rules:
 *   Email  → priority >= 3
 *   SMS    → priority == 5
 *   Push   → always
 *
 * And these notifications:
 *   "ALERT", "Server Down",   "CPU at 100%",  priority=5 → all three channels
 *   "INFO",  "Deploy Done",   "v2.1 is live", priority=2 → push only
 *   "WARN",  "High Memory",   "85% used",     priority=3 → email + push
 *
 * Bonus: Add a ThrottledChannel wrapper that suppresses sends if the same
 *        title was sent within the last 60 seconds (use a Map<String, Long>).
 *        This is the Decorator pattern.
 *
 * Problem 2: Factory + Adapter — Payment Gateway Integration
 * ------------------------------------------------------------
 * Real systems integrate third-party APIs with incompatible interfaces, and
 * pick an implementation by config rather than hardcoding `new SomeGateway()`
 * everywhere — that hardcoding is a DIP violation.
 *
 * 1. PaymentGateway interface: PaymentResult charge(double amountCents, String currency)
 * 2. PaymentResult record: boolean success, String transactionId
 * 3. StripeGateway implements PaymentGateway directly (matches the interface natively)
 * 4. LegacyPaymentProcessor — a pretend 3rd-party class you CANNOT modify, with an
 *    incompatible signature: String submitPayment(int amountCents, String currencyCode)
 *    returning "OK:<id>" on success or "FAIL" on failure (given below — don't change it)
 * 5. LegacyGatewayAdapter implements PaymentGateway, wraps a LegacyPaymentProcessor, and
 *    translates charge(...) calls into submitPayment(...) calls — this is the ADAPTER
 *    pattern: bridging an incompatible interface without touching the legacy code.
 * 6. PaymentGatewayFactory.create(String provider): "stripe" → new StripeGateway();
 *    "legacy" → a LegacyGatewayAdapter wrapping a new LegacyPaymentProcessor — this is
 *    the FACTORY pattern: callers ask for a capability by name, never call `new` directly.
 *
 * Test: charge $49.99 via both "stripe" and "legacy" providers through the factory;
 * both must report success despite completely different underlying APIs.
 *
 * Problem 3: Sealed Interface + Pattern-Matching Switch (Java 17/21)
 * ---------------------------------------------------------------------
 * Modern Java replaces open-ended instanceof chains with a COMPILER-ENFORCED
 * closed hierarchy: the compiler proves you handled every case — no `default`
 * needed, and no risk of silently forgetting a new subtype.
 *
 * 1. sealed interface Shape permits Circle, Rectangle, Triangle
 * 2. record Circle(double radius) implements Shape
 * 3. record Rectangle(double width, double height) implements Shape
 * 4. record Triangle(double base, double height) implements Shape
 * 5. static double area(Shape shape) — implement with a pattern-matching switch
 *    expression (no default branch needed; try commenting out one case to see
 *    the compiler reject it — that's the whole point of "sealed")
 *
 * Test: compute the area of one of each shape, print each rounded to 2 decimals.
 */
import java.util.*;
import java.util.function.Predicate;

public class Chapter08Exercise {

    record Notification(String type, String title, String body, int priority) {}

    interface NotificationChannel {
        void send(Notification n);
        String channelName();
    }

    // TODO: implement EmailChannel, SMSChannel, PushChannel

    static class NotificationRouter {
        private final NotificationChannel channel;
        private final Predicate<Notification> rule;

        NotificationRouter(NotificationChannel channel, Predicate<Notification> rule) {
            this.channel = channel;
            this.rule    = rule;
        }

        boolean canHandle(Notification n) { return rule.test(n); }
        void route(Notification n) {
            if (canHandle(n)) {
                System.out.print("  → " + channel.channelName() + ": ");
                channel.send(n);
            }
        }
    }

    static class NotificationDispatcher {
        private final List<NotificationRouter> routers = new ArrayList<>();

        void addRouter(NotificationRouter r) { routers.add(r); }

        void dispatch(Notification n) {
            System.out.println("[" + n.type() + "/" + n.priority() + "] " + n.title());
            routers.forEach(r -> r.route(n));
        }
    }

    // ========= Problem 2: Factory + Adapter =========
    record PaymentResult(boolean success, String transactionId) {}

    interface PaymentGateway {
        PaymentResult charge(double amountCents, String currency);
    }

    static class StripeGateway implements PaymentGateway {
        @Override public PaymentResult charge(double amountCents, String currency) {
            // TODO: pretend success, return a fake transactionId
            return null;
        }
    }

    // Given — pretend 3rd-party class, incompatible signature, DO NOT modify
    static class LegacyPaymentProcessor {
        String submitPayment(int amountCents, String currencyCode) {
            return "OK:txn-" + amountCents + "-" + currencyCode;
        }
    }

    static class LegacyGatewayAdapter implements PaymentGateway {
        private final LegacyPaymentProcessor legacy;

        LegacyGatewayAdapter(LegacyPaymentProcessor legacy) { this.legacy = legacy; }

        @Override public PaymentResult charge(double amountCents, String currency) {
            // TODO: call legacy.submitPayment((int) amountCents, currency) and translate
            //       its "OK:<id>" / "FAIL" string response into a PaymentResult
            return null;
        }
    }

    static class PaymentGatewayFactory {
        static PaymentGateway create(String provider) {
            // TODO: "stripe" -> new StripeGateway()
            // TODO: "legacy" -> new LegacyGatewayAdapter(new LegacyPaymentProcessor())
            // TODO: else -> throw new IllegalArgumentException("Unknown provider: " + provider)
            return null;
        }
    }

    // ========= Problem 3: Sealed Interface + Pattern-Matching Switch =========
    // TODO: sealed interface Shape permits Circle, Rectangle, Triangle
    // TODO: record Circle(double radius) implements Shape
    // TODO: record Rectangle(double width, double height) implements Shape
    // TODO: record Triangle(double base, double height) implements Shape

    // TODO: static double area(Shape shape) using a pattern-matching switch expression:
    //   return switch (shape) {
    //       case Circle c -> Math.PI * c.radius() * c.radius();
    //       case Rectangle r -> r.width() * r.height();
    //       case Triangle t -> 0.5 * t.base() * t.height();
    //   };

    public static void main(String[] args) {

        // TODO: wire up channels and routers
        NotificationDispatcher dispatcher = new NotificationDispatcher();

        // TODO: add routers:
        // Email  → priority >= 3
        // SMS    → priority == 5
        // Push   → always (priority >= 1)

        System.out.println("=== Notification Dispatch ===");
        dispatcher.dispatch(new Notification("ALERT", "Server Down",  "CPU at 100%", 5));
        dispatcher.dispatch(new Notification("INFO",  "Deploy Done",  "v2.1 is live", 2));
        dispatcher.dispatch(new Notification("WARN",  "High Memory",  "85% used", 3));

        // Bonus: ThrottledChannel
        // TODO: implement ThrottledChannel that wraps any NotificationChannel
        //       and suppresses duplicate sends within 60 seconds

        System.out.println("\n=== Problem 2: Factory + Adapter ===");
        // TODO: PaymentGateway stripe = PaymentGatewayFactory.create("stripe");
        // TODO: PaymentGateway legacy = PaymentGatewayFactory.create("legacy");
        // TODO: print charge(4999, "USD") results from both — both should succeed

        System.out.println("\n=== Problem 3: Sealed Shapes ===");
        // TODO: Shape circle = new Circle(3.0);
        // TODO: Shape rect   = new Rectangle(4.0, 5.0);
        // TODO: Shape tri    = new Triangle(6.0, 2.0);
        // TODO: print area(circle), area(rect), area(tri) each rounded to 2 decimals
    }
}
