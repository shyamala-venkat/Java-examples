/**
 * Chapter 6: Using the Java Library
 *
 * Key Concepts:
 *  - ArrayList — dynamic array from java.util
 *  - Wrapper classes (Integer, Double, etc.) and autoboxing/unboxing
 *  - The Java API — reading docs, understanding packages, imports
 *  - Commonly used library classes: Math, String, Collections
 *  - Arrays utility class
 *
 * How to run:
 *   javac Chapter06Main.java
 *   java Chapter06Main
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Chapter06Main {

    public static void main(String[] args) {

        System.out.println("=== Chapter 6: Using the Java Library ===\n");

        // -------------------------------------------------------
        // CONCEPT 1: ArrayList — the dynamic list
        //
        // Unlike arrays, ArrayList can GROW and SHRINK at runtime.
        // You don't need to know the size upfront.
        // ArrayList<Type> — uses generics to specify element type.
        // -------------------------------------------------------
        System.out.println("--- ArrayList Basics ---");

        ArrayList<String> shoppingList = new ArrayList<>(); // <> is diamond operator

        // Adding elements
        shoppingList.add("Milk");
        shoppingList.add("Eggs");
        shoppingList.add("Bread");
        shoppingList.add("Coffee");
        System.out.println("List: " + shoppingList);
        System.out.println("Size: " + shoppingList.size()); // size() not length!

        // Getting elements (0-indexed, like arrays)
        System.out.println("First item: " + shoppingList.get(0));
        System.out.println("Last item:  " + shoppingList.get(shoppingList.size() - 1));

        // Check if element exists
        System.out.println("Has Milk:  " + shoppingList.contains("Milk"));
        System.out.println("Has Sugar: " + shoppingList.contains("Sugar"));

        // Find index of element
        System.out.println("Index of Eggs: " + shoppingList.indexOf("Eggs"));

        // Update (replace) element at index
        shoppingList.set(1, "Butter"); // replaces "Eggs" with "Butter"
        System.out.println("After set(1, Butter): " + shoppingList);

        // Remove by index
        shoppingList.remove(0); // removes "Milk"
        System.out.println("After remove(0): " + shoppingList);

        // Remove by value (first occurrence)
        shoppingList.remove("Bread");
        System.out.println("After remove(Bread): " + shoppingList);

        // Iterating with for-each
        System.out.print("Items: ");
        for (String item : shoppingList) {
            System.out.print(item + " | ");
        }
        System.out.println();

        // -------------------------------------------------------
        // CONCEPT 2: ArrayList vs Array — when to use which?
        //
        // Use Array when:  size is fixed, performance-critical, primitives
        // Use ArrayList when: size changes, need add/remove, convenience methods
        // -------------------------------------------------------
        System.out.println("\n--- ArrayList<Integer> ---");
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(42);
        numbers.add(17);
        numbers.add(99);
        numbers.add(5);
        numbers.add(63);
        System.out.println("Numbers: " + numbers);
        System.out.println("Min: " + Collections.min(numbers));
        System.out.println("Max: " + Collections.max(numbers));
        Collections.sort(numbers);
        System.out.println("Sorted: " + numbers);
        Collections.reverse(numbers);
        System.out.println("Reversed: " + numbers);
        Collections.shuffle(numbers);
        System.out.println("Shuffled: " + numbers);

        // -------------------------------------------------------
        // CONCEPT 3: Autoboxing and Unboxing
        //
        // Primitives (int, double, etc.) CANNOT go into ArrayList directly.
        // Java auto-converts between primitives and their wrapper classes:
        //   int    <-> Integer
        //   double <-> Double
        //   boolean <-> Boolean
        //   char   <-> Character
        //   etc.
        //
        // Autoboxing:   int -> Integer (automatic, when needed)
        // Unboxing:     Integer -> int (automatic, when needed)
        // -------------------------------------------------------
        System.out.println("\n--- Autoboxing & Unboxing ---");

        // Autoboxing: Java wraps int in Integer automatically
        ArrayList<Integer> scores = new ArrayList<>();
        scores.add(95);  // Java does: scores.add(Integer.valueOf(95))
        scores.add(80);
        scores.add(75);

        // Unboxing: Java extracts int from Integer automatically
        int firstScore = scores.get(0); // Java does: scores.get(0).intValue()
        System.out.println("First score: " + firstScore);

        // Beware: null Integer can cause NullPointerException when unboxed!
        Integer nullableScore = null;
        // int x = nullableScore; // NullPointerException! -- don't do this

        // Wrapper classes have useful static methods
        System.out.println("Integer.MAX_VALUE: " + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE: " + Integer.MIN_VALUE);
        System.out.println("Parse string:      " + Integer.parseInt("42"));
        System.out.println("Integer to binary: " + Integer.toBinaryString(255));
        System.out.println("Integer to hex:    " + Integer.toHexString(255));
        System.out.println("Double.parseDouble: " + Double.parseDouble("3.14"));

        // -------------------------------------------------------
        // CONCEPT 4: Math class — useful static methods
        //
        // Math is a utility class — all methods are static, no object needed.
        // -------------------------------------------------------
        System.out.println("\n--- Math class ---");
        System.out.println("Math.abs(-5):        " + Math.abs(-5));
        System.out.println("Math.max(10, 20):    " + Math.max(10, 20));
        System.out.println("Math.min(10, 20):    " + Math.min(10, 20));
        System.out.println("Math.pow(2, 10):     " + Math.pow(2, 10));   // 1024.0
        System.out.println("Math.sqrt(144):      " + Math.sqrt(144));    // 12.0
        System.out.println("Math.round(3.7):     " + Math.round(3.7));   // 4
        System.out.println("Math.floor(3.9):     " + Math.floor(3.9));   // 3.0
        System.out.println("Math.ceil(3.1):      " + Math.ceil(3.1));    // 4.0
        System.out.println("Math.PI:             " + Math.PI);

        // Random numbers
        double random = Math.random();         // 0.0 <= random < 1.0
        int die = (int)(Math.random() * 6) + 1; // random int 1-6
        System.out.println("Math.random():       " + random);
        System.out.println("Simulated die roll:  " + die);

        // -------------------------------------------------------
        // CONCEPT 5: Arrays utility class
        // -------------------------------------------------------
        System.out.println("\n--- Arrays utility ---");
        int[] arr = {5, 3, 8, 1, 9, 2};
        System.out.println("Before sort: " + Arrays.toString(arr));
        Arrays.sort(arr);
        System.out.println("After sort:  " + Arrays.toString(arr));

        // Binary search (array must be sorted first!)
        int idx = Arrays.binarySearch(arr, 8);
        System.out.println("Binary search for 8 at index: " + idx);

        // Copy
        int[] copy = Arrays.copyOf(arr, 4); // copy first 4 elements
        System.out.println("First 4 elements: " + Arrays.toString(copy));

        int[] rangeCopy = Arrays.copyOfRange(arr, 2, 5); // index 2 to 4
        System.out.println("Range copy [2,5): " + Arrays.toString(rangeCopy));

        // Fill
        int[] filled = new int[5];
        Arrays.fill(filled, 7);
        System.out.println("Filled with 7: " + Arrays.toString(filled));

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a simple To-Do List application using ArrayList:");
        System.out.println("  - Start with an empty ArrayList<String>");
        System.out.println("  - Add 5 tasks");
        System.out.println("  - Print all tasks with their number (1-based, not 0-based)");
        System.out.println("  - Mark task #2 as done by removing it");
        System.out.println("  - Add a new task");
        System.out.println("  - Sort the remaining tasks alphabetically using Collections.sort()");
        System.out.println("  - Print the final list");

        // TODO: EXERCISE — implement To-Do List here
    }
}
