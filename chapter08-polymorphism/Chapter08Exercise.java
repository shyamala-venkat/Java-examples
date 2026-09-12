/**
 * EXERCISE — Chapter 8: Serious Polymorphism
 *
 * Problem: Notification System with Strategy + Observer Pattern
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
    }
}
