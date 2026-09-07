/**
 * Chapter 8: Serious Polymorphism — Abstract Classes and Interfaces
 *
 * Key Concepts:
 *  - Abstract classes: cannot be instantiated, may have abstract methods
 *  - Abstract methods: no body, subclass MUST implement
 *  - Interfaces: pure contract — methods, constants, no state (mostly)
 *  - Implementing interfaces with `implements`
 *  - A class can extend one class but implement MANY interfaces
 *  - Default methods in interfaces (Java 8+)
 *  - When to use abstract class vs interface
 *  - Polymorphism through interfaces and abstract types
 *
 * How to run:
 *   javac Chapter08Main.java
 *   java Chapter08Main
 */
public class Chapter08Main {

    // -------------------------------------------------------
    // CONCEPT 1: Abstract Classes
    //
    // Use `abstract` when:
    //  - You want to provide SOME shared implementation
    //  - But leave some methods for subclasses to fill in
    //  - The class itself represents an incomplete concept
    //    (you wouldn't say "give me a Shape" — which kind?)
    //
    // Abstract class CANNOT be instantiated directly:
    //   Shape s = new Shape(); // COMPILE ERROR
    // -------------------------------------------------------
    static abstract class Shape {
        private String color;

        public Shape(String color) {
            this.color = color;
        }

        public String getColor() { return color; }

        // ABSTRACT method — no body! Subclass MUST override.
        // Forces every Shape to define how it calculates its area.
        public abstract double area();
        public abstract double perimeter();

        // Non-abstract method — shared implementation for ALL shapes
        public void printInfo() {
            System.out.printf("%-12s color=%-8s area=%-10.2f perimeter=%.2f%n",
                getClass().getSimpleName(), color, area(), perimeter());
        }
    }

    // -------------------------------------------------------
    // Concrete subclasses — must implement all abstract methods
    // -------------------------------------------------------
    static class Circle extends Shape {
        private double radius;

        public Circle(String color, double radius) {
            super(color);
            this.radius = radius;
        }

        @Override
        public double area() { return Math.PI * radius * radius; }

        @Override
        public double perimeter() { return 2 * Math.PI * radius; }
    }

    static class Rectangle extends Shape {
        private double width, height;

        public Rectangle(String color, double width, double height) {
            super(color);
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() { return width * height; }

        @Override
        public double perimeter() { return 2 * (width + height); }
    }

    static class Triangle extends Shape {
        private double a, b, c; // sides

        public Triangle(String color, double a, double b, double c) {
            super(color);
            this.a = a; this.b = b; this.c = c;
        }

        @Override
        public double area() {
            double s = (a + b + c) / 2; // semi-perimeter (Heron's formula)
            return Math.sqrt(s * (s-a) * (s-b) * (s-c));
        }

        @Override
        public double perimeter() { return a + b + c; }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Interfaces
    //
    // An interface is a PURE CONTRACT — it says "you must be able to do this"
    // but provides no state and (usually) no implementation.
    //
    // Key differences from abstract class:
    //   - No constructor, no instance variables (only constants)
    //   - All methods implicitly public abstract (unless default/static)
    //   - A class can implement MULTIPLE interfaces
    //   - Use for capability ("can do") not identity ("is a kind of")
    // -------------------------------------------------------
    interface Drawable {
        void draw(); // implicitly public abstract
    }

    interface Resizable {
        void resize(double factor);
        double getSize();
    }

    interface Printable {
        void print();

        // CONCEPT 3: Default methods (Java 8+)
        // Provides a default implementation — classes can override or use as-is.
        // This is how Java added methods to old interfaces without breaking existing code.
        default void printWithBorder() {
            System.out.println("--- PRINT ---");
            print();
            System.out.println("-------------");
        }
    }

    // -------------------------------------------------------
    // A class implementing MULTIPLE interfaces
    // -------------------------------------------------------
    static class Square extends Shape implements Drawable, Resizable, Printable {
        private double side;

        public Square(String color, double side) {
            super(color);
            this.side = side;
        }

        @Override public double area()      { return side * side; }
        @Override public double perimeter() { return 4 * side; }

        // Implements Drawable
        @Override
        public void draw() {
            System.out.println("Drawing a " + getColor() + " square with side=" + side);
        }

        // Implements Resizable
        @Override
        public void resize(double factor) {
            side *= factor;
            System.out.println("Resized square to side=" + side);
        }

        @Override
        public double getSize() { return side; }

        // Implements Printable
        @Override
        public void print() {
            System.out.println("Square: side=" + side + " area=" + area());
        }
    }

    // -------------------------------------------------------
    // CONCEPT 4: Interface as a type for polymorphism
    //
    // You can store any implementing object in an interface reference.
    // -------------------------------------------------------
    interface Sortable {
        int compareTo(Object other); // simplistic comparison interface
    }

    // -------------------------------------------------------
    // CONCEPT 5: Abstract class vs Interface — when to use which?
    //
    // Abstract class:
    //   - "Is a kind of" relationship (Dog IS-A Animal)
    //   - Share CODE among closely related classes
    //   - Need constructors or instance state in the parent
    //
    // Interface:
    //   - "Can do" capability (Dog can be Trainable, Serializable)
    //   - Unrelated classes share the same capability
    //   - Need multiple "inheritance" of behavior
    // -------------------------------------------------------

    // Functional interface — exactly one abstract method
    // Can be used with lambda expressions (Chapter 17)
    @FunctionalInterface
    interface Transformer {
        double transform(double value);
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 8: Serious Polymorphism ===\n");

        // -------------------------------------------------------
        // Abstract class polymorphism
        // -------------------------------------------------------
        System.out.println("--- Abstract Class: Shape hierarchy ---");

        Shape[] shapes = {
            new Circle("red", 5),
            new Rectangle("blue", 4, 6),
            new Triangle("green", 3, 4, 5),
            new Square("yellow", 4)
        };

        for (Shape s : shapes) {
            s.printInfo(); // calls the concrete area() and perimeter() implementations
        }

        // Shape s = new Shape("red"); // COMPILE ERROR — can't instantiate abstract class

        // -------------------------------------------------------
        // Interface polymorphism
        // -------------------------------------------------------
        System.out.println("\n--- Interface Polymorphism ---");

        Square sq = new Square("purple", 3);
        sq.draw();
        sq.resize(2.0);
        sq.print();
        sq.printWithBorder(); // uses default interface method

        // Store in interface type — only see the interface's methods
        Drawable drawableSq = sq;
        drawableSq.draw(); // only draw() visible through Drawable reference

        Resizable resizableSq = sq;
        resizableSq.resize(0.5);
        System.out.println("Size after resize: " + resizableSq.getSize());

        // -------------------------------------------------------
        // CONCEPT 6: Functional interface + lambda preview
        // -------------------------------------------------------
        System.out.println("\n--- Functional Interface ---");

        Transformer doubler  = value -> value * 2;
        Transformer squarer  = value -> value * value;
        Transformer negater  = value -> -value;

        System.out.println("doubler(5):  " + doubler.transform(5));
        System.out.println("squarer(5):  " + squarer.transform(5));
        System.out.println("negater(5):  " + negater.transform(5));

        // -------------------------------------------------------
        // instanceof with interface
        // -------------------------------------------------------
        System.out.println("\n--- instanceof with Interfaces ---");
        Shape anyShape = new Square("orange", 2);
        System.out.println("Is Drawable:  " + (anyShape instanceof Drawable));
        System.out.println("Is Resizable: " + (anyShape instanceof Resizable));
        System.out.println("Is Circle:    " + (anyShape instanceof Circle));

        // -------------------------------------------------------
        // Summary: abstract class vs interface
        // -------------------------------------------------------
        System.out.println("\n--- When to use Abstract vs Interface ---");
        System.out.println("Abstract class: shared code + state, IS-A relationship");
        System.out.println("  e.g., Shape is abstract — all shapes have color, printInfo()");
        System.out.println("Interface: capability contract, CAN-DO relationship");
        System.out.println("  e.g., Drawable, Resizable, Printable — unrelated classes can all be Printable");
        System.out.println("A class can extend ONE class but implement MANY interfaces.");

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a payment system:");
        System.out.println("  - Interface: Payable { void pay(double amount); String getPaymentMethod(); }");
        System.out.println("  - Implement: CreditCard, PayPal, BankTransfer — each has its own pay() logic");
        System.out.println("  - Abstract class: Discount { abstract double applyDiscount(double amount); }");
        System.out.println("  - Concrete: PercentageDiscount(10%), FlatDiscount($5)");
        System.out.println("  - Create a checkout method that accepts any Payable and any Discount");
        System.out.println("  - Test with different combinations");

        // TODO: EXERCISE — implement payment system here
    }
}
