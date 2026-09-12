/**
 * CONCEPT: Variable Scope and Lifetime
 *
 * Scope determines WHERE a variable can be accessed.
 * Lifetime determines HOW LONG it exists in memory.
 *
 * Why this matters:
 *   - Shadowing (local variable hiding an instance variable) is a subtle bug source
 *   - Loop variables leaking into outer scope is a classic Java gotcha
 *   - Understanding stack vs heap lifetime is critical for memory leak analysis
 *   - `var` (local type inference, Java 10+) reduces boilerplate but has limits
 */
import java.util.ArrayList;
import java.util.List;

class ScopeAndLifetime {

    // Class (static) scope — shared across all instances, lives for JVM lifetime
    static int instanceCount = 0;

    // Instance scope — one copy per object, lives while object is reachable
    private String name;

    ScopeAndLifetime(String name) {
        this.name = name; // `this.name` = instance var; `name` = parameter — same name, different scope
        instanceCount++;
    }

    // --- Shadowing: dangerous when unintentional ---
    void shadowingDemo() {
        String name = "local";          // shadows instance variable this.name
        System.out.println("local name: " + name);        // "local"
        System.out.println("instance name: " + this.name); // object's name
    }

    static void blockScopeDemo() {
        int outer = 10;

        {
            int inner = 20;    // inner is scoped to this block only
            System.out.println("inner: " + inner);
            System.out.println("outer inside block: " + outer);
        }
        // System.out.println(inner); // COMPILE ERROR — inner out of scope

        for (int i = 0; i < 3; i++) {
            int loopLocal = i * 2;  // new loopLocal each iteration
            System.out.println("loop: i=" + i + ", local=" + loopLocal);
        }
        // i and loopLocal are out of scope here

        System.out.println("outer after block: " + outer); // still accessible
    }

    static void varDemo() {
        // `var` — local type inference (Java 10+)
        // The TYPE is still fixed at compile time — Java is still statically typed
        var list  = new ArrayList<String>(); // type is ArrayList<String>
        var count = 0;                        // type is int

        list.add("hello");
        list.add("world");
        count = list.size();

        // var list2 = null; // COMPILE ERROR — can't infer type from null
        // var is only for LOCAL variables — not fields, parameters, or return types

        System.out.println("var list: " + list);
        System.out.println("var count: " + count);

        // var is especially useful with long generic types:
        var entries = new java.util.HashMap<String, List<Integer>>();
        // vs: HashMap<String, List<Integer>> entries = new HashMap<String, List<Integer>>();
    }

    public static void main(String[] args) {

        System.out.println("--- Instance scope ---");
        ScopeAndLifetime obj1 = new ScopeAndLifetime("Alice");
        ScopeAndLifetime obj2 = new ScopeAndLifetime("Bob");
        System.out.println("instanceCount: " + ScopeAndLifetime.instanceCount); // 2

        System.out.println("\n--- Shadowing ---");
        obj1.shadowingDemo();

        System.out.println("\n--- Block scope ---");
        blockScopeDemo();

        System.out.println("\n--- var (local type inference) ---");
        varDemo();

        System.out.println("\n--- Memory: when does an object become unreachable? ---");
        ScopeAndLifetime temp = new ScopeAndLifetime("Temp");
        System.out.println("temp created: " + temp.name);
        temp = null;  // no more references → eligible for garbage collection
        System.out.println("temp reference cleared — object may be GC'd");
        // GC runs on its own schedule; you cannot force it reliably

        // TRY THIS:
        // 1. In shadowingDemo(), remove `this.` from `this.name = name` in the constructor.
        //    What happens? Why doesn't the object's name get set?
        // 2. Try using `var` for a method parameter — does it compile?
        // 3. Create a 10MB byte array, print memory usage, then null it and call
        //    System.gc() (hint only — not guaranteed). Observe with Runtime.getRuntime().
    }
}
