/**
 * Chapter 10: Numbers and Statics
 *
 * Key Concepts:
 *  - static variables: shared by ALL instances of a class
 *  - static methods: belong to the class, not instances; no `this`
 *  - static final constants: naming convention (ALL_CAPS)
 *  - Math class (all static methods)
 *  - Wrapper classes: Integer, Double, Boolean, Character, etc.
 *  - Number formatting: String.format() and printf()
 *  - Autoboxing/Unboxing (reinforced from Chapter 6)
 *
 * How to run:
 *   javac Chapter10Main.java
 *   java Chapter10Main
 */
public class Chapter10Main {

    // -------------------------------------------------------
    // CONCEPT 1: Static Variables
    //
    // static = belongs to the CLASS, not to any instance.
    // All instances SHARE the same static variable.
    // Change it through one instance, all instances see the change.
    // -------------------------------------------------------
    static class Counter {
        // Instance variable — each Counter object has its OWN count
        private int instanceCount = 0;

        // Static variable — ONE copy shared by ALL Counter objects
        private static int totalCreated = 0;

        // Static constant — all caps by convention
        public static final int MAX_COUNT = 100;

        public Counter() {
            totalCreated++; // increments the shared counter every time an object is created
        }

        public void increment() {
            if (instanceCount < MAX_COUNT) {
                instanceCount++;
            }
        }

        public int getInstanceCount()      { return instanceCount; }

        // Static method to access static variable
        // Cannot use `this` or access instance variables — there's no specific object
        public static int getTotalCreated() { return totalCreated; }

        public static void resetTotal()    { totalCreated = 0; }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Static utility class
    //
    // A class with ONLY static methods and constants.
    // You never need to create an instance.
    // Examples from Java: Math, Arrays, Collections.
    // -------------------------------------------------------
    static class MathUtils {
        // Private constructor prevents instantiation
        private MathUtils() {}

        public static final double GOLDEN_RATIO = 1.618033988749895;

        public static boolean isPrime(int n) {
            if (n < 2) return false;
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        }

        public static int factorial(int n) {
            if (n < 0) throw new IllegalArgumentException("No factorial for negative: " + n);
            if (n == 0 || n == 1) return 1;
            return n * factorial(n - 1); // recursion!
        }

        public static int fibonacci(int n) {
            if (n <= 1) return n;
            return fibonacci(n - 1) + fibonacci(n - 2);
        }

        public static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    // -------------------------------------------------------
    // CONCEPT 3: Singleton pattern using static
    //
    // Ensures only ONE instance of a class ever exists.
    // Common pattern in senior-level interviews.
    // -------------------------------------------------------
    static class AppConfig {
        private static AppConfig instance; // holds the single instance
        private String environment;
        private int maxConnections;

        // Private constructor — nobody outside can call `new AppConfig()`
        private AppConfig() {
            environment = "production";
            maxConnections = 100;
        }

        // Static method to get (or create) the single instance
        public static AppConfig getInstance() {
            if (instance == null) {
                instance = new AppConfig(); // created only once
            }
            return instance;
        }

        public String getEnvironment()   { return environment; }
        public int getMaxConnections()   { return maxConnections; }
        public void setEnvironment(String env) { this.environment = env; }
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 10: Numbers and Statics ===\n");

        // -------------------------------------------------------
        // Static variable demo
        // -------------------------------------------------------
        System.out.println("--- Static Variables ---");
        System.out.println("totalCreated before any object: " + Counter.getTotalCreated());

        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();

        // Each has its own instanceCount
        c1.increment(); c1.increment(); c1.increment();
        c2.increment();

        System.out.println("c1.instanceCount: " + c1.getInstanceCount()); // 3
        System.out.println("c2.instanceCount: " + c2.getInstanceCount()); // 1
        System.out.println("c3.instanceCount: " + c3.getInstanceCount()); // 0

        // All share the same totalCreated
        System.out.println("totalCreated (shared): " + Counter.getTotalCreated()); // 3
        System.out.println("MAX_COUNT constant: " + Counter.MAX_COUNT);            // 100

        // -------------------------------------------------------
        // Static utility methods
        // -------------------------------------------------------
        System.out.println("\n--- Static Utility Methods ---");
        System.out.println("isPrime(7):    " + MathUtils.isPrime(7));
        System.out.println("isPrime(10):   " + MathUtils.isPrime(10));
        System.out.println("factorial(5):  " + MathUtils.factorial(5));   // 120
        System.out.println("fibonacci(10): " + MathUtils.fibonacci(10));  // 55
        System.out.println("clamp(150, 0, 100): " + MathUtils.clamp(150, 0, 100)); // 100
        System.out.println("GOLDEN_RATIO: " + MathUtils.GOLDEN_RATIO);

        // -------------------------------------------------------
        // Singleton demo
        // -------------------------------------------------------
        System.out.println("\n--- Singleton Pattern ---");
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        System.out.println("Same instance? " + (config1 == config2)); // true
        config1.setEnvironment("staging");
        System.out.println("config2 env: " + config2.getEnvironment()); // "staging" — same object!

        // -------------------------------------------------------
        // CONCEPT 4: Number Formatting
        // -------------------------------------------------------
        System.out.println("\n--- Number Formatting ---");

        double price = 1234567.89;
        double pi = Math.PI;
        int count = 42;

        // String.format() — returns a formatted String
        System.out.println(String.format("Price: $%,.2f", price));     // $1,234,567.89
        System.out.println(String.format("Pi: %.4f", pi));             // 3.1416
        System.out.println(String.format("Count: %05d", count));       // 00042 (padded)
        System.out.println(String.format("Sci:   %e", pi));            // 3.141593e+00
        System.out.println(String.format("%-15s %5d", "Items:", 99));  // left-aligned

        // printf() — prints formatted output directly (no println needed for newline)
        System.out.printf("%-12s %-10s %8s%n", "Product", "Category", "Price");
        System.out.printf("%-12s %-10s %8.2f%n", "Laptop",   "Electronics", 999.99);
        System.out.printf("%-12s %-10s %8.2f%n", "Coffee",   "Food",        4.50);
        System.out.printf("%-12s %-10s %8.2f%n", "Java Book","Education",   49.99);

        // -------------------------------------------------------
        // CONCEPT 5: Wrapper Classes in depth
        // -------------------------------------------------------
        System.out.println("\n--- Wrapper Classes ---");

        // Parsing strings to numbers
        int parsed  = Integer.parseInt("123");
        double dbl  = Double.parseDouble("3.14");
        boolean b   = Boolean.parseBoolean("true");
        System.out.println("Parsed int:     " + parsed);
        System.out.println("Parsed double:  " + dbl);
        System.out.println("Parsed boolean: " + b);

        // Converting numbers to different bases
        System.out.println("255 in binary: " + Integer.toBinaryString(255));  // 11111111
        System.out.println("255 in hex:    " + Integer.toHexString(255));     // ff
        System.out.println("255 in octal:  " + Integer.toOctalString(255));   // 377

        // Integer cache: Java caches Integer objects -128 to 127
        Integer x = 100;
        Integer y = 100;
        System.out.println("\nx == y (cached integers 100): " + (x == y));   // true (same cached object)

        Integer a = 200;
        Integer b2 = 200;
        System.out.println("a == b2 (outside cache, 200): " + (a == b2));   // false (different objects!)
        System.out.println("a.equals(b2):                 " + a.equals(b2)); // true (same value)
        // LESSON: Always use .equals() to compare Integer objects, not ==

        // -------------------------------------------------------
        // CONCEPT 6: Math class recap
        // -------------------------------------------------------
        System.out.println("\n--- Math Class ---");
        System.out.println("Math.abs(-42):       " + Math.abs(-42));
        System.out.println("Math.ceil(4.1):      " + Math.ceil(4.1));
        System.out.println("Math.floor(4.9):     " + Math.floor(4.9));
        System.out.println("Math.round(4.5):     " + Math.round(4.5));
        System.out.println("Math.pow(2, 8):      " + Math.pow(2, 8));
        System.out.println("Math.log(Math.E):    " + Math.log(Math.E));    // 1.0
        System.out.println("Math.log10(1000):    " + Math.log10(1000));    // 3.0

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a Statistics class (all static methods):");
        System.out.println("  - min(int[] arr)    -> returns minimum value");
        System.out.println("  - max(int[] arr)    -> returns maximum value");
        System.out.println("  - sum(int[] arr)    -> returns sum");
        System.out.println("  - average(int[] arr)-> returns double average");
        System.out.println("  - Print results for {85, 92, 78, 95, 88, 70} formatted to 2 decimal places");
        System.out.println("  Bonus: Add a static counter that tracks how many times any method was called");

        // TODO: EXERCISE — implement Statistics class here
    }
}
