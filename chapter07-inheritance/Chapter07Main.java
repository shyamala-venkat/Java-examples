/**
 * Chapter 7: Better Living in Objectville — Inheritance
 *
 * Key Concepts:
 *  - Inheritance: `extends` keyword
 *  - IS-A relationship (subclass IS-A superclass)
 *  - HAS-A relationship (composition — holds a reference to another object)
 *  - Method overriding: subclass provides its own version of a parent method
 *  - The `super` keyword: calling parent's methods/fields
 *  - Overriding vs overloading — important distinction
 *  - Polymorphism preview: parent reference, child object
 *  - Object class — the root of all Java classes
 *
 * How to run:
 *   javac Chapter07Main.java
 *   java Chapter07Main
 */
public class Chapter07Main {

    // -------------------------------------------------------
    // CONCEPT 1: Superclass (parent class)
    //
    // Animal is the general blueprint. It has things all animals share.
    // -------------------------------------------------------
    static class Animal {
        private String name;
        private int age;

        public Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge()     { return age; }

        // This method can be OVERRIDDEN by subclasses
        public void makeSound() {
            System.out.println(name + " makes a generic animal sound.");
        }

        public void eat() {
            System.out.println(name + " is eating.");
        }

        public void sleep() {
            System.out.println(name + " is sleeping. Zzz...");
        }

        // toString() is inherited from Object — overriding it gives a nice representation
        @Override
        public String toString() {
            return "Animal[name=" + name + ", age=" + age + "]";
        }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Subclass using `extends`
    //
    // Dog IS-A Animal. Dog INHERITS all public/protected fields and methods.
    // Dog can ADD new fields and methods, and OVERRIDE existing ones.
    // -------------------------------------------------------
    static class Dog extends Animal {

        // Dog-specific field — not in Animal
        private String breed;

        // -------------------------------------------------------
        // CONCEPT 3: super() — calling the parent constructor
        //
        // The FIRST thing a subclass constructor must do (explicitly or
        // implicitly) is call the parent's constructor.
        // -------------------------------------------------------
        public Dog(String name, int age, String breed) {
            super(name, age); // calls Animal(name, age) constructor
            this.breed = breed;
        }

        public String getBreed() { return breed; }

        // -------------------------------------------------------
        // CONCEPT 4: Method Overriding
        //
        // Same method signature (name + parameters) as the parent.
        // @Override annotation tells the compiler "I intend to override
        // a parent method" — the compiler will error if you mistype the name.
        // -------------------------------------------------------
        @Override
        public void makeSound() {
            System.out.println(getName() + " says: Woof! Woof!");
        }

        // Dog-specific method — not in Animal
        public void fetch(String item) {
            System.out.println(getName() + " fetches the " + item + "!");
        }

        // Calling super's method from within the overriding method
        public void makeComplexSound() {
            super.makeSound();      // calls Animal's version first
            makeSound();            // then calls Dog's version
        }

        @Override
        public String toString() {
            return "Dog[name=" + getName() + ", age=" + getAge() + ", breed=" + breed + "]";
        }
    }

    // -------------------------------------------------------
    // Cat also extends Animal
    // -------------------------------------------------------
    static class Cat extends Animal {
        private boolean isIndoor;

        public Cat(String name, int age, boolean isIndoor) {
            super(name, age);
            this.isIndoor = isIndoor;
        }

        @Override
        public void makeSound() {
            System.out.println(getName() + " says: Meow~");
        }

        public void purr() {
            System.out.println(getName() + " purrs contentedly.");
        }

        @Override
        public String toString() {
            return "Cat[name=" + getName() + ", indoor=" + isIndoor + "]";
        }
    }

    // -------------------------------------------------------
    // CONCEPT 5: HAS-A relationship (Composition)
    //
    // Person HAS-A Dog (holds a reference to a Dog object).
    // This is composition, NOT inheritance.
    // Rule of thumb: Prefer composition over inheritance when possible.
    //
    // IS-A test: "A Dog IS-A Person" — makes no sense, so don't extend Person.
    // HAS-A test: "A Person HAS-A Dog" — makes sense, so use composition.
    // -------------------------------------------------------
    static class Person {
        private String name;
        private Dog pet; // HAS-A Dog — composition

        public Person(String name, Dog pet) {
            this.name = name;
            this.pet = pet;
        }

        public void interactWithPet() {
            System.out.println(name + " plays with their dog " + pet.getName());
            pet.fetch("ball");
            pet.makeSound();
        }
    }

    // -------------------------------------------------------
    // CONCEPT 6: Multi-level inheritance
    //
    // GuideDog extends Dog which extends Animal.
    // Java allows multi-level inheritance but NOT multiple inheritance
    // of classes (you can't extend two classes).
    // -------------------------------------------------------
    static class GuideDog extends Dog {
        private String owner;

        public GuideDog(String name, int age, String breed, String owner) {
            super(name, age, breed); // calls Dog's constructor
            this.owner = owner;
        }

        public void guide() {
            System.out.println(getName() + " is guiding " + owner + " safely.");
        }

        @Override
        public void makeSound() {
            System.out.println(getName() + " barks softly to alert " + owner);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 7: Inheritance ===\n");

        // -------------------------------------------------------
        // Creating and using objects
        // -------------------------------------------------------
        System.out.println("--- Basic Inheritance ---");
        Dog rex = new Dog("Rex", 3, "Labrador");
        Cat whiskers = new Cat("Whiskers", 5, true);

        rex.makeSound();       // Dog's version
        rex.eat();             // inherited from Animal — no override needed
        rex.sleep();           // inherited from Animal
        rex.fetch("stick");    // Dog-specific method

        whiskers.makeSound();  // Cat's version
        whiskers.purr();       // Cat-specific

        System.out.println(rex);      // calls Dog's toString
        System.out.println(whiskers); // calls Cat's toString

        // -------------------------------------------------------
        // Polymorphism preview — parent type, child object
        //
        // An Animal reference can point to a Dog OR Cat object.
        // At runtime, Java calls the ACTUAL object's method (not Animal's).
        // This is called runtime polymorphism (dynamic dispatch).
        // -------------------------------------------------------
        System.out.println("\n--- Polymorphism Preview ---");

        Animal[] animals = {
            new Dog("Buddy", 2, "Beagle"),
            new Cat("Luna", 4, false),
            new Dog("Max", 6, "German Shepherd"),
            new Cat("Felix", 1, true)
        };

        // Each animal calls ITS OWN makeSound(), even though the reference type is Animal
        for (Animal a : animals) {
            a.makeSound(); // polymorphism in action!
        }

        // -------------------------------------------------------
        // super method call
        // -------------------------------------------------------
        System.out.println("\n--- super call ---");
        Dog buddy = new Dog("Buddy", 2, "Beagle");
        buddy.makeComplexSound(); // calls both Animal's and Dog's version

        // -------------------------------------------------------
        // HAS-A (composition)
        // -------------------------------------------------------
        System.out.println("\n--- HAS-A (Composition) ---");
        Dog myDog = new Dog("Charlie", 4, "Border Collie");
        Person owner = new Person("Sarah", myDog);
        owner.interactWithPet();

        // -------------------------------------------------------
        // Multi-level inheritance
        // -------------------------------------------------------
        System.out.println("\n--- Multi-level Inheritance ---");
        GuideDog guide = new GuideDog("Zeus", 4, "Labrador", "John");
        guide.guide();
        guide.makeSound();
        guide.eat();       // inherited all the way from Animal!

        // -------------------------------------------------------
        // instanceof — checking object type at runtime
        // -------------------------------------------------------
        System.out.println("\n--- instanceof check ---");
        Animal a = new Dog("Spot", 1, "Dalmatian");
        System.out.println("a instanceof Dog:    " + (a instanceof Dog));    // true
        System.out.println("a instanceof Animal: " + (a instanceof Animal)); // true
        System.out.println("a instanceof Cat:    " + (a instanceof Cat));    // false

        // Pattern matching instanceof (Java 16+)
        if (a instanceof Dog d) {
            d.fetch("frisbee"); // d is already cast to Dog — no explicit cast needed
        }

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Create a vehicle hierarchy:");
        System.out.println("  - Superclass: Vehicle (fields: make, model, year; method: describe())");
        System.out.println("  - Subclass: Car extends Vehicle (field: numDoors; override: describe())");
        System.out.println("  - Subclass: Motorcycle extends Vehicle (field: hasSidecar; override: describe())");
        System.out.println("  - Create an array of Vehicle objects with mixed Car/Motorcycle");
        System.out.println("  - Loop through and call describe() on each — observe polymorphism");

        // TODO: EXERCISE — implement Vehicle hierarchy here
    }
}
