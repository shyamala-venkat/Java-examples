/**
 * CONCEPT: Composition vs Inheritance — "Favor composition over inheritance"
 *
 * Inheritance couples a subclass tightly to its parent.
 * Composition (HAS-A) gives you the same code reuse with less coupling.
 *
 * Rule of thumb:
 *   IS-A  → inheritance:   "An Engineer IS-A Employee"
 *   HAS-A → composition:   "A Car HAS-A Engine"
 *   CAN-DO → interface:    "A Dog CAN-DO Fetch"
 *
 * The classic antipattern: inheriting just to reuse a method, even when
 * IS-A doesn't hold (e.g., Stack extends Vector in the old JDK — a design mistake).
 *
 * Why this matters:
 *   - This is a standard senior interview question: "when to use each?"
 *   - Composition makes dependencies explicit (easier to test/mock)
 *   - Inheritance hierarchies become hard to change as they grow deep
 */
class CompositionVsInheritance {

    // === WRONG: inheritance used for code reuse, but IS-A doesn't hold ===
    static class Logger {
        void log(String msg) { System.out.println("[LOG] " + msg); }
    }

    // Bad: UserService IS-A Logger? No. We just want to reuse log().
    static class UserServiceBad extends Logger {
        void createUser(String name) {
            log("Creating user: " + name); // works, but wrong conceptually
        }
    }

    // === RIGHT: composition — UserService HAS-A Logger ===
    static class UserService {
        private final Logger logger; // dependency injected → easily mockable in tests

        UserService(Logger logger) { this.logger = logger; }

        void createUser(String name) {
            logger.log("Creating user: " + name);
            System.out.println("User created: " + name);
        }
    }

    // === Composition example: Engine and Car ===
    static class Engine {
        private final int horsepower;
        private boolean running = false;

        Engine(int hp) { this.horsepower = hp; }
        void start()  { running = true;  System.out.println("Engine started (" + horsepower + "hp)"); }
        void stop()   { running = false; System.out.println("Engine stopped"); }
        boolean isRunning() { return running; }
    }

    static class Car {
        private final String model;
        private final Engine engine; // HAS-A Engine

        Car(String model, int hp) {
            this.model  = model;
            this.engine = new Engine(hp);
        }

        void drive() {
            if (!engine.isRunning()) engine.start();
            System.out.println(model + " is driving!");
        }
        void park() {
            engine.stop();
            System.out.println(model + " is parked.");
        }
    }

    // === Composition enables Strategy pattern: swap behavior at runtime ===
    interface SortStrategy {
        void sort(int[] arr);
        String name();
    }

    static class BubbleSort implements SortStrategy {
        @Override public String name() { return "BubbleSort"; }
        @Override public void sort(int[] arr) {
            for (int i = 0; i < arr.length - 1; i++)
                for (int j = 0; j < arr.length - 1 - i; j++)
                    if (arr[j] > arr[j+1]) { int t=arr[j]; arr[j]=arr[j+1]; arr[j+1]=t; }
        }
    }

    static class SelectionSort implements SortStrategy {
        @Override public String name() { return "SelectionSort"; }
        @Override public void sort(int[] arr) {
            for (int i = 0; i < arr.length; i++) {
                int minIdx = i;
                for (int j = i + 1; j < arr.length; j++)
                    if (arr[j] < arr[minIdx]) minIdx = j;
                int t = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = t;
            }
        }
    }

    static class Sorter {
        private SortStrategy strategy;
        Sorter(SortStrategy strategy) { this.strategy = strategy; }
        void setStrategy(SortStrategy s) { this.strategy = s; } // swap at runtime
        void sort(int[] arr) {
            System.out.print(strategy.name() + ": ");
            strategy.sort(arr);
            System.out.println(java.util.Arrays.toString(arr));
        }
    }

    public static void main(String[] args) {

        System.out.println("--- Wrong: inheritance for code reuse ---");
        new UserServiceBad().createUser("Alice");

        System.out.println("\n--- Right: composition (HAS-A) ---");
        Logger logger = new Logger();
        UserService service = new UserService(logger);
        service.createUser("Bob");

        System.out.println("\n--- Car HAS-A Engine ---");
        Car car = new Car("Tesla Model 3", 350);
        car.drive();
        car.park();

        System.out.println("\n--- Strategy Pattern (swap behavior) ---");
        int[] data1 = {5, 3, 8, 1, 9};
        int[] data2 = {5, 3, 8, 1, 9};

        Sorter sorter = new Sorter(new BubbleSort());
        sorter.sort(data1);

        sorter.setStrategy(new SelectionSort()); // swap strategy at runtime
        sorter.sort(data2);

        // TRY THIS:
        // 1. Add a QuickSort strategy and swap to it. No changes to Sorter needed — why?
        // 2. Add a NoOpLogger that does nothing. Pass it to UserService.
        //    This is the Null Object pattern — compare to checking logger != null everywhere.
        // 3. Refactor UserService to accept a Logger via a setter instead of the constructor.
        //    What's the tradeoff? (Constructor injection vs setter injection)
    }
}
