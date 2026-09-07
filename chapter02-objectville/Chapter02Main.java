/**
 * Chapter 2: A Trip to Objectville
 *
 * Key Concepts:
 *  - The difference between a class and an object
 *  - Instance variables (fields) — state of an object
 *  - Methods — behavior of an object
 *  - Creating objects with the `new` keyword
 *  - Calling methods on objects using dot notation
 *  - Multiple objects from the same class
 *
 * How to run:
 *   javac Chapter02Main.java
 *   java Chapter02Main
 */
public class Chapter02Main {

    // -------------------------------------------------------
    // CONCEPT 1: Defining a Class
    //
    // A class is a BLUEPRINT — it describes what an object
    // looks like (fields) and what it can do (methods).
    // An object is a specific INSTANCE created from that blueprint.
    //
    // Think of it like: Class = cookie cutter, Object = cookie.
    // -------------------------------------------------------

    // Inner class — Dog is defined inside Chapter02Main so
    // the whole file compiles as one unit.
    static class Dog {

        // Instance variables (fields) — each Dog object gets its OWN copy
        String name;
        String breed;
        int age;
        boolean isBarking;

        // -------------------------------------------------------
        // CONCEPT 2: Methods — what the object CAN DO
        // Methods operate on the object's own instance variables.
        // -------------------------------------------------------
        void bark() {
            isBarking = true;
            System.out.println(name + " says: Woof! Woof!");
        }

        void stopBarking() {
            isBarking = false;
            System.out.println(name + " is quiet now.");
        }

        // A method that RETURNS a value
        String describe() {
            return name + " is a " + age + "-year-old " + breed + ".";
        }

        // A method that takes a PARAMETER
        void eat(String food) {
            System.out.println(name + " is eating " + food + ". Yum!");
        }
    }

    // -------------------------------------------------------
    // CONCEPT 3: Another class — shows classes relate to each other
    // -------------------------------------------------------
    static class Cat {
        String name;
        int livesRemaining = 9; // instance variables can have default values

        void meow() {
            System.out.println(name + " says: Meow~");
        }

        void loseLife() {
            if (livesRemaining > 0) {
                livesRemaining--;
                System.out.println(name + " lost a life! Lives remaining: " + livesRemaining);
            } else {
                System.out.println(name + " has no lives left.");
            }
        }
    }

    public static void main(String[] args) {

        // -------------------------------------------------------
        // CONCEPT 4: Creating Objects with `new`
        //
        // `new Dog()` allocates memory on the heap for a Dog object
        // and returns a REFERENCE to that object.
        // `Dog myDog` is a reference variable — it holds the address,
        // not the object itself.
        // -------------------------------------------------------
        System.out.println("=== Chapter 2: A Trip to Objectville ===\n");

        Dog dog1 = new Dog();      // creates first Dog object
        dog1.name = "Rex";         // set instance variables using dot notation
        dog1.breed = "Labrador";
        dog1.age = 3;

        Dog dog2 = new Dog();      // creates SECOND, completely separate Dog object
        dog2.name = "Bella";
        dog2.breed = "Poodle";
        dog2.age = 5;

        // -------------------------------------------------------
        // CONCEPT 5: Calling methods on objects
        // Each object has its OWN state — changing dog1 does NOT affect dog2
        // -------------------------------------------------------
        System.out.println("--- Dog Objects ---");
        System.out.println(dog1.describe());
        System.out.println(dog2.describe());

        dog1.bark();
        dog2.bark();
        dog1.stopBarking();

        dog1.eat("bone");
        dog2.eat("kibble");

        // -------------------------------------------------------
        // CONCEPT 6: References — multiple variables can point to the SAME object
        //
        // This is a key concept! dog3 is NOT a new dog — it points to dog1's object.
        // Changing dog3.name also changes dog1.name because they're the SAME object.
        // -------------------------------------------------------
        System.out.println("\n--- Reference vs Object ---");
        Dog dog3 = dog1; // dog3 now points to the SAME object as dog1
        dog3.name = "Max"; // this changes the object that BOTH dog1 and dog3 point to
        System.out.println("dog1.name: " + dog1.name); // prints "Max" — same object!
        System.out.println("dog3.name: " + dog3.name); // also "Max"

        // -------------------------------------------------------
        // CONCEPT 7: null — a reference that points to NOTHING
        // Calling a method on null causes NullPointerException (NPE)!
        // NPEs are one of the most common Java bugs — always check for null.
        // -------------------------------------------------------
        System.out.println("\n--- null reference ---");
        Dog noDog = null;
        System.out.println("noDog is: " + noDog); // prints "null"
        // noDog.bark(); // THIS WOULD THROW NullPointerException — don't do it!

        // -------------------------------------------------------
        // Cat example
        // -------------------------------------------------------
        System.out.println("\n--- Cat Objects ---");
        Cat cat1 = new Cat();
        cat1.name = "Whiskers";
        cat1.meow();
        cat1.loseLife();
        cat1.loseLife();
        System.out.println("Lives left: " + cat1.livesRemaining);

        // -------------------------------------------------------
        // KEY INSIGHT: Class vs Object summary
        // -------------------------------------------------------
        System.out.println("\n--- Key Insight ---");
        System.out.println("Dog class = the blueprint (defined once)");
        System.out.println("dog1, dog2 = actual Dog objects (can create many)");
        System.out.println("Each object has its OWN copy of instance variables.");

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Create a BankAccount class with:");
        System.out.println("  - Fields: owner (String), balance (double)");
        System.out.println("  - Methods: deposit(double amount), withdraw(double amount), getBalance()");
        System.out.println("  - Create 2 BankAccount objects, deposit and withdraw money,");
        System.out.println("    and print balances to verify each account is independent.");

        // TODO: EXERCISE — define BankAccount as a static inner class and test it here
    }
}
