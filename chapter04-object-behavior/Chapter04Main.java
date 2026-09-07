/**
 * Chapter 4: How Objects Behave
 *
 * Key Concepts:
 *  - State (instance variables) vs Behavior (methods)
 *  - Encapsulation — hiding data with private + public getters/setters
 *  - The `this` keyword — refers to the current object
 *  - Why encapsulation matters: controlled access to state
 *  - Method parameters and how they pass values
 *  - Pass by value for primitives vs pass by value of reference
 *
 * How to run:
 *   javac Chapter04Main.java
 *   java Chapter04Main
 */
public class Chapter04Main {

    // -------------------------------------------------------
    // CONCEPT 1: BAD design — no encapsulation
    // Fields are public: anyone can set any value, including invalid ones.
    // -------------------------------------------------------
    static class BadPlayer {
        public int health;  // public = anyone can access/modify directly
        public int speed;
    }

    // -------------------------------------------------------
    // CONCEPT 2: GOOD design — Encapsulation
    //
    // Mark instance variables PRIVATE so they can only be
    // changed through methods YOU control.
    //
    // Private = locked box. You provide the key (getter/setter).
    //
    // Benefits:
    //  - You can validate input before changing state
    //  - You can change internal implementation without breaking callers
    //  - You prevent invalid state (negative health, speed > max, etc.)
    // -------------------------------------------------------
    static class Player {

        // private fields — cannot be accessed directly from outside this class
        private String name;
        private int health;
        private int speed;

        private static final int MAX_HEALTH = 100;
        private static final int MAX_SPEED = 10;

        // -------------------------------------------------------
        // CONCEPT 3: The `this` keyword
        //
        // `this` refers to the current object instance.
        // Use it when a parameter name shadows an instance variable.
        // -------------------------------------------------------
        public void setName(String name) {
            // Without `this.name`, both sides would refer to the parameter.
            this.name = name; // this.name = instance variable, name = parameter
        }

        // Getter — read-only access to private field
        public String getName() {
            return this.name; // `this.` is optional when unambiguous, but good habit
        }

        // Setter WITH validation — this is the power of encapsulation
        public void setHealth(int health) {
            if (health < 0) {
                this.health = 0;           // can't be negative
            } else if (health > MAX_HEALTH) {
                this.health = MAX_HEALTH;  // can't exceed max
            } else {
                this.health = health;
            }
        }

        public int getHealth() {
            return health;
        }

        public void setSpeed(int speed) {
            if (speed >= 0 && speed <= MAX_SPEED) {
                this.speed = speed;
            } else {
                System.out.println("Invalid speed: " + speed + ". Must be 0-" + MAX_SPEED);
            }
        }

        public int getSpeed() {
            return speed;
        }

        // -------------------------------------------------------
        // CONCEPT 4: Methods that change state and return info
        // -------------------------------------------------------
        public void takeDamage(int damage) {
            int newHealth = this.health - damage;
            setHealth(newHealth); // reuses setter validation!
            System.out.println(name + " took " + damage + " damage. Health: " + health);
        }

        public void heal(int amount) {
            setHealth(this.health + amount);
            System.out.println(name + " healed " + amount + ". Health: " + health);
        }

        public boolean isAlive() {
            return health > 0;
        }

        public String status() {
            return "[" + name + "] HP:" + health + " Speed:" + speed;
        }
    }

    // -------------------------------------------------------
    // CONCEPT 5: Demonstrating pass-by-value
    //
    // Java is ALWAYS pass-by-value.
    // For primitives: the VALUE is copied — original is unchanged.
    // For objects: the REFERENCE (address) is copied — the original
    //   object CAN be modified through the reference, but you can't
    //   make the caller's variable point to a different object.
    // -------------------------------------------------------
    static void tryToDoubleInt(int x) {
        x = x * 2; // only changes the LOCAL copy
        System.out.println("Inside method, x = " + x);
    }

    static void tryToModifyPlayer(Player p) {
        p.setHealth(1); // modifies the ACTUAL object via the reference copy
        System.out.println("Inside method, player health = " + p.getHealth());
    }

    static void tryToReplacePlayer(Player p) {
        p = new Player(); // only changes the LOCAL reference copy
        p.setName("Ghost");
        // the caller's variable still points to the original object!
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 4: How Objects Behave ===\n");

        // -------------------------------------------------------
        // Demo: Bad design (no encapsulation)
        // -------------------------------------------------------
        System.out.println("--- Bad Design (no encapsulation) ---");
        BadPlayer badPlayer = new BadPlayer();
        badPlayer.health = -999; // invalid! nothing stops this
        badPlayer.speed = 10000; // also invalid
        System.out.println("BadPlayer health: " + badPlayer.health); // -999
        System.out.println("BadPlayer speed:  " + badPlayer.speed);  // 10000

        // -------------------------------------------------------
        // Demo: Good design (encapsulation)
        // -------------------------------------------------------
        System.out.println("\n--- Good Design (encapsulation) ---");
        Player player = new Player();
        player.setName("Hero");
        player.setHealth(100);
        player.setSpeed(7);
        System.out.println(player.status());

        player.setHealth(-50);  // setter clamps to 0 automatically
        System.out.println("After setHealth(-50): " + player.getHealth()); // 0

        player.setHealth(200);  // setter clamps to MAX_HEALTH
        System.out.println("After setHealth(200): " + player.getHealth()); // 100

        player.setSpeed(15);    // rejected — prints error message
        System.out.println("Speed after invalid set: " + player.getSpeed()); // unchanged

        player.takeDamage(30);
        player.heal(10);
        System.out.println("Alive: " + player.isAlive());

        player.takeDamage(999);
        System.out.println("Alive: " + player.isAlive()); // false

        // -------------------------------------------------------
        // Demo: Pass by value
        // -------------------------------------------------------
        System.out.println("\n--- Pass By Value ---");

        // Primitive pass-by-value
        int myNumber = 5;
        System.out.println("Before method: " + myNumber);
        tryToDoubleInt(myNumber);
        System.out.println("After method:  " + myNumber); // still 5 — original unchanged

        // Object reference pass-by-value
        Player hero = new Player();
        hero.setName("Warrior");
        hero.setHealth(80);
        System.out.println("\nBefore method: " + hero.getHealth());
        tryToModifyPlayer(hero);
        System.out.println("After modifyPlayer: " + hero.getHealth()); // 1 — object was changed!

        // Reassigning reference inside method — caller unaffected
        System.out.println("\nBefore replacePlayer: " + hero.getName());
        tryToReplacePlayer(hero);
        System.out.println("After replacePlayer: " + hero.getName()); // still "Warrior"

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Create an encapsulated 'Person' class with:");
        System.out.println("  - private fields: name (String), age (int)");
        System.out.println("  - setAge() validates: age must be 0-150");
        System.out.println("  - setName() validates: name cannot be null or empty");
        System.out.println("  - greet() prints: \"Hi, I'm [name] and I'm [age] years old.\"");
        System.out.println("Test with valid and invalid values to verify validation works.");

        // TODO: EXERCISE — implement Person class and test it here
    }
}
