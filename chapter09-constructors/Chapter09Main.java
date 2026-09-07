/**
 * Chapter 9: Life and Death of an Object — Constructors and Memory
 *
 * Key Concepts:
 *  - Constructors: purpose, default constructor, custom constructors
 *  - Constructor overloading
 *  - this() — calling one constructor from another (constructor chaining)
 *  - super() — calling parent's constructor
 *  - Stack vs Heap memory — where things live
 *  - Object lifecycle: creation, use, garbage collection
 *  - final fields — must be set in constructor, then immutable
 *
 * How to run:
 *   javac Chapter09Main.java
 *   java Chapter09Main
 */
public class Chapter09Main {

    // -------------------------------------------------------
    // CONCEPT 1: Constructors
    //
    // - Same name as the class
    // - No return type (not even void)
    // - Called automatically when you use `new`
    // - If you write NO constructor, Java provides a free DEFAULT
    //   constructor with no args and no body
    // - If you write ANY constructor, Java stops providing the default
    // -------------------------------------------------------
    static class Point {
        int x;
        int y;

        // Default (no-arg) constructor — explicit version
        public Point() {
            this.x = 0;
            this.y = 0;
            System.out.println("  Point() constructor called -> (" + x + ", " + y + ")");
        }

        // Parameterized constructor
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
            System.out.println("  Point(int,int) constructor called -> (" + x + ", " + y + ")");
        }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Constructor chaining with this()
    //
    // this() calls another constructor in the SAME class.
    // MUST be the very first statement in the constructor.
    // Avoids code duplication — one "main" constructor, others delegate to it.
    // -------------------------------------------------------
    static class Coffee {
        private String type;
        private int shots;
        private boolean hasMilk;
        private String size;

        // The "main" constructor — does all the real work
        public Coffee(String type, int shots, boolean hasMilk, String size) {
            this.type = type;
            this.shots = shots;
            this.hasMilk = hasMilk;
            this.size = size;
        }

        // Convenience constructor — delegates to main constructor
        public Coffee(String type) {
            this(type, 1, false, "medium"); // calls the 4-arg constructor
        }

        // Another convenience constructor
        public Coffee(String type, int shots) {
            this(type, shots, false, "medium");
        }

        @Override
        public String toString() {
            return size + " " + type + " (" + shots + " shot" + (shots > 1 ? "s" : "") +
                   (hasMilk ? ", with milk" : "") + ")";
        }
    }

    // -------------------------------------------------------
    // CONCEPT 3: super() — calling parent constructor
    //
    // Java automatically inserts `super()` (no-arg) if you don't
    // explicitly call super(). If the parent has NO no-arg constructor,
    // you MUST explicitly call super(args) as the first statement.
    // -------------------------------------------------------
    static class Vehicle {
        private String make;
        private String model;
        private int year;

        public Vehicle(String make, String model, int year) {
            this.make = make;
            this.model = model;
            this.year = year;
            System.out.println("  Vehicle constructor: " + make + " " + model);
        }

        public String getMake()  { return make; }
        public String getModel() { return model; }
        public int getYear()     { return year; }

        @Override
        public String toString() {
            return year + " " + make + " " + model;
        }
    }

    static class Car extends Vehicle {
        private int numDoors;

        public Car(String make, String model, int year, int numDoors) {
            super(make, model, year); // MUST be first — calls Vehicle constructor
            this.numDoors = numDoors;
            System.out.println("  Car constructor: " + numDoors + " doors");
        }

        @Override
        public String toString() {
            return super.toString() + " (" + numDoors + "-door)";
        }
    }

    static class ElectricCar extends Car {
        private int rangeKm;

        public ElectricCar(String make, String model, int year, int numDoors, int rangeKm) {
            super(make, model, year, numDoors); // calls Car constructor -> calls Vehicle constructor
            this.rangeKm = rangeKm;
            System.out.println("  ElectricCar constructor: range=" + rangeKm + "km");
        }

        @Override
        public String toString() {
            return super.toString() + " [Electric, range=" + rangeKm + "km]";
        }
    }

    // -------------------------------------------------------
    // CONCEPT 4: final fields
    //
    // final = set once, never changed.
    // Must be set in the constructor (or at declaration).
    // Good for immutable data — thread-safe, easier to reason about.
    // -------------------------------------------------------
    static class ImmutablePoint {
        private final int x;  // final — can only be set once
        private final int y;

        public ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
            // this.x = 10; // would be a compile error — already assigned!
        }

        public int getX() { return x; }
        public int getY() { return y; }

        // Returns a NEW object instead of modifying this one
        public ImmutablePoint translate(int dx, int dy) {
            return new ImmutablePoint(x + dx, y + dy);
        }

        @Override
        public String toString() { return "(" + x + ", " + y + ")"; }
    }

    // -------------------------------------------------------
    // CONCEPT 5: Stack vs Heap
    //
    // STACK: Local variables, method calls, primitive values
    //   - Fast, automatically managed (LIFO)
    //   - Each thread has its own stack
    //   - When a method returns, its stack frame is popped
    //
    // HEAP: All objects (created with `new`)
    //   - Slower, managed by Garbage Collector
    //   - Shared by all threads
    //   - Objects live as long as there are references to them
    //
    // Example:
    //   int x = 5;          <- x (value 5) lives on STACK
    //   Dog d = new Dog();  <- d (reference) on STACK, Dog object on HEAP
    // -------------------------------------------------------
    static void stackDemo() {
        int stackVar = 42;           // lives on the stack for this method call
        Point heapPoint = new Point(1, 2); // heapPoint reference on stack, Point object on heap

        System.out.println("  stackVar: " + stackVar + " (on stack)");
        System.out.println("  heapPoint: " + heapPoint.x + "," + heapPoint.y + " (on heap)");
        // When stackDemo() returns, stackVar and heapPoint reference are destroyed.
        // If no other reference points to the Point object, it becomes eligible for GC.
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 9: Life and Death of an Object ===\n");

        // -------------------------------------------------------
        // Constructor basics
        // -------------------------------------------------------
        System.out.println("--- Constructors ---");
        System.out.println("Creating Point():");
        Point p1 = new Point();
        System.out.println("Creating Point(3, 4):");
        Point p2 = new Point(3, 4);

        // -------------------------------------------------------
        // Constructor chaining (this())
        // -------------------------------------------------------
        System.out.println("\n--- Constructor Chaining (this()) ---");
        Coffee c1 = new Coffee("Espresso");
        Coffee c2 = new Coffee("Latte", 2);
        Coffee c3 = new Coffee("Cappuccino", 2, true, "large");
        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);

        // -------------------------------------------------------
        // super() constructor chain
        // -------------------------------------------------------
        System.out.println("\n--- super() Constructor Chain ---");
        System.out.println("Creating ElectricCar (triggers 3-level constructor chain):");
        ElectricCar tesla = new ElectricCar("Tesla", "Model 3", 2024, 4, 500);
        System.out.println(tesla);

        // -------------------------------------------------------
        // Immutable object with final fields
        // -------------------------------------------------------
        System.out.println("\n--- Immutable Object (final fields) ---");
        ImmutablePoint ip1 = new ImmutablePoint(3, 4);
        ImmutablePoint ip2 = ip1.translate(2, -1); // creates new object
        System.out.println("Original: " + ip1); // unchanged
        System.out.println("Translated: " + ip2);
        // ip1.x = 10; // COMPILE ERROR — x is final

        // -------------------------------------------------------
        // Stack vs Heap
        // -------------------------------------------------------
        System.out.println("\n--- Stack vs Heap ---");
        stackDemo();
        System.out.println("  stackVar and heapPoint reference are gone (out of scope)");
        System.out.println("  The Point object on heap may be GC'd if no other references");

        // -------------------------------------------------------
        // Garbage Collection
        // -------------------------------------------------------
        System.out.println("\n--- Garbage Collection ---");
        Point ref1 = new Point(10, 20);
        Point ref2 = ref1;    // both ref1 and ref2 point to the SAME object

        ref1 = null;          // ref1 no longer points to the object
        // Object still has ref2 pointing to it — NOT eligible for GC yet
        System.out.println("ref2 still sees the object: " + ref2.x);

        ref2 = null;          // NOW no references — object is eligible for GC
        System.out.println("No references remain — object can be garbage collected");
        System.out.println("(GC runs at its own schedule — you can't force it reliably)");

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Design a 'Product' class for an online store:");
        System.out.println("  - final fields: id (int), name (String)");
        System.out.println("  - non-final fields: price (double), stock (int)");
        System.out.println("  - Constructor with all 4 fields");
        System.out.println("  - Convenience constructor: Product(int id, String name, double price)");
        System.out.println("    -> sets stock=0 by default using this()");
        System.out.println("  - Method: restock(int quantity) — adds to stock");
        System.out.println("  - Method: sell(int quantity) — returns true if sold, false if insufficient stock");
        System.out.println("  - Test: create 2 products, sell some, try to oversell");

        // TODO: EXERCISE — implement Product class here
    }
}
