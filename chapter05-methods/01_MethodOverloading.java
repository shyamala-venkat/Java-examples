/**
 * CONCEPT: Method Overloading and Effective Method Design
 *
 * Overloading: same method name, different parameter types or count.
 * The compiler picks the right version at compile time (static dispatch).
 *
 * Why this matters:
 *   - Overloading vs overriding is a classic interview distinction
 *   - Overloading resolution can surprise you with autoboxing/widening
 *   - Varargs overloading has specific resolution order
 *   - Good method signatures reduce cognitive load on callers
 */
class MethodOverloading {

    // --- Overloaded: same name, different signatures ---
    static int    add(int a, int b)       { return a + b; }
    static double add(double a, double b) { return a + b; }
    static int    add(int a, int b, int c){ return a + b + c; }
    static String add(String a, String b) { return a + b; }   // works on String too

    // --- Overloading resolution surprises ---
    static void print(int x)     { System.out.println("int: " + x); }
    static void print(long x)    { System.out.println("long: " + x); }
    static void print(Integer x) { System.out.println("Integer: " + x); }

    // --- Varargs — always the LAST resort in overload resolution ---
    static int sum(int first, int... rest) {
        int total = first;
        for (int v : rest) total += v;
        return total;
    }

    // --- Practical example: overloading for usability ---
    static class HttpRequest {
        private final String url;
        private final String method;
        private final int    timeoutMs;

        // Full constructor
        HttpRequest(String url, String method, int timeoutMs) {
            this.url       = url;
            this.method    = method;
            this.timeoutMs = timeoutMs;
        }
        // Convenience overloads — reduce boilerplate for callers
        HttpRequest(String url, String method) { this(url, method, 5000); }
        HttpRequest(String url)               { this(url, "GET", 5000); }

        @Override public String toString() {
            return method + " " + url + " (timeout=" + timeoutMs + "ms)";
        }
    }

    public static void main(String[] args) {

        System.out.println("--- Overloading ---");
        System.out.println(add(1, 2));           // int version: 3
        System.out.println(add(1.5, 2.5));       // double version: 4.0
        System.out.println(add(1, 2, 3));        // 3-arg version: 6
        System.out.println(add("hello", " world")); // String version

        // --- Resolution order: exact > widening > boxing > varargs ---
        System.out.println("\n--- Overload resolution order ---");
        print(5);          // exact match: int
        print(5L);         // exact match: long
        Integer wrapped = 5;
        print(wrapped);    // exact match: Integer
        // print(5) does NOT box to Integer — widening (int→long) wins over boxing
        // This order matters in real APIs

        System.out.println("\n--- Varargs ---");
        System.out.println(sum(1));               // just first: 1
        System.out.println(sum(1, 2, 3));         // 6
        System.out.println(sum(10, 1, 2, 3, 4));  // 20

        System.out.println("\n--- Convenience overloads ---");
        System.out.println(new HttpRequest("https://api.example.com/users"));
        System.out.println(new HttpRequest("https://api.example.com/users", "POST"));
        System.out.println(new HttpRequest("https://api.example.com/users", "DELETE", 10_000));

        // TRY THIS:
        // 1. Add print(double x) — now what does print(5) resolve to? Why?
        // 2. Write a log(String level, String... messages) varargs method.
        //    Call it with 0 messages — does it work?
        // 3. Overload sum() so sum(double first, double... rest) also exists.
        //    What happens when you call sum(1, 2) — ambiguous? Test it.
    }
}
