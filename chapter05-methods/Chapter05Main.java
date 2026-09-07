/**
 * Chapter 5: Extra-Strength Methods
 *
 * Key Concepts:
 *  - Method signatures: return types, parameters, overloading
 *  - The return statement
 *  - switch expressions (modern Java 14+) vs switch statements
 *  - Enhanced for loop (for-each)
 *  - break and continue in loops
 *  - Varargs (variable-length arguments)
 *  - Arrays: declaration, access, length, iteration
 *
 * How to run:
 *   javac Chapter05Main.java
 *   java Chapter05Main
 */
public class Chapter05Main {

    // -------------------------------------------------------
    // CONCEPT 1: Method Overloading
    //
    // Same method NAME, different parameter types or count.
    // Java picks the right one based on what arguments you pass.
    // Return type alone does NOT distinguish overloaded methods.
    // -------------------------------------------------------
    static int add(int a, int b) {
        return a + b;
    }

    static double add(double a, double b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    // -------------------------------------------------------
    // CONCEPT 2: Return types
    //
    // void = returns nothing
    // Every non-void method MUST return a value of the declared type.
    // The compiler will catch missing return paths.
    // -------------------------------------------------------
    static String classify(int n) {
        if (n > 0) {
            return "positive";
        } else if (n < 0) {
            return "negative";
        } else {
            return "zero"; // every code path must return!
        }
    }

    // -------------------------------------------------------
    // CONCEPT 3: Varargs — variable number of arguments
    //
    // Use type... name — the values arrive as an array.
    // Must be the LAST parameter if mixed with others.
    // -------------------------------------------------------
    static double average(double... numbers) {
        if (numbers.length == 0) return 0;
        double sum = 0;
        for (double n : numbers) { // for-each works on varargs
            sum += n;
        }
        return sum / numbers.length;
    }

    static void printAll(String label, int... values) {
        System.out.print(label + ": ");
        for (int v : values) {
            System.out.print(v + " ");
        }
        System.out.println();
    }

    // -------------------------------------------------------
    // CONCEPT 4: Arrays
    // Fixed size. Type-specific. Index starts at 0.
    // -------------------------------------------------------
    static void demonstrateArrays() {
        // Declaring and initializing
        int[] scores = new int[5];           // array of 5 ints, all 0
        String[] names = {"Alice", "Bob", "Charlie"}; // array literal
        double[] grades = new double[]{90.5, 85.0, 78.3}; // another form

        // Accessing elements
        scores[0] = 95;
        scores[1] = 87;
        scores[2] = 92;
        scores[3] = 78;
        scores[4] = 88;

        System.out.println("First name: " + names[0]);
        System.out.println("Array length: " + scores.length); // .length is a field, not method

        // Traditional for loop — useful when you need the index
        System.out.print("Scores (with index): ");
        for (int i = 0; i < scores.length; i++) {
            System.out.print("[" + i + "]=" + scores[i] + " ");
        }
        System.out.println();

        // Enhanced for loop (for-each) — cleaner when you don't need index
        System.out.print("Names (for-each): ");
        for (String name : names) {
            System.out.print(name + " ");
        }
        System.out.println();

        // 2D array — array of arrays
        int[][] grid = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("Center of grid: " + grid[1][1]); // 5
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 5: Extra-Strength Methods ===\n");

        // -------------------------------------------------------
        // Method overloading demo
        // -------------------------------------------------------
        System.out.println("--- Method Overloading ---");
        System.out.println("add(3, 4)       = " + add(3, 4));       // int version
        System.out.println("add(3.0, 4.5)   = " + add(3.0, 4.5));   // double version
        System.out.println("add(1, 2, 3)    = " + add(1, 2, 3));     // 3-arg version

        // -------------------------------------------------------
        // Return types
        // -------------------------------------------------------
        System.out.println("\n--- Return Types ---");
        System.out.println("classify(5):  " + classify(5));
        System.out.println("classify(-3): " + classify(-3));
        System.out.println("classify(0):  " + classify(0));

        // -------------------------------------------------------
        // CONCEPT 5: switch — choosing between many cases
        //
        // Classic switch statement (Java 1 - 13 style):
        //   - needs `break` to prevent fall-through
        //   - fall-through is a common bug
        //
        // Modern switch EXPRESSION (Java 14+):
        //   - uses -> arrow syntax
        //   - no fall-through
        //   - returns a value directly
        //   - compiler ensures all cases are covered (with default)
        // -------------------------------------------------------
        System.out.println("\n--- Switch: Classic vs Modern ---");

        int dayNumber = 3;

        // Classic switch (avoid unless targeting older Java)
        System.out.print("Classic switch day " + dayNumber + ": ");
        switch (dayNumber) {
            case 1: System.out.println("Monday"); break;
            case 2: System.out.println("Tuesday"); break;
            case 3: System.out.println("Wednesday"); break;
            // Forgetting `break` causes fall-through — runs next case too!
            default: System.out.println("Other");
        }

        // Modern switch expression (preferred in Java 14+)
        String dayName = switch (dayNumber) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            case 7 -> "Sunday";
            default -> "Invalid day";
        };
        System.out.println("Modern switch day " + dayNumber + ": " + dayName);

        // Switch on String (supported since Java 7)
        String season = "WINTER";
        String activity = switch (season) {
            case "SPRING" -> "Gardening";
            case "SUMMER" -> "Swimming";
            case "FALL"   -> "Hiking";
            case "WINTER" -> "Skiing";
            default       -> "Staying home";
        };
        System.out.println("Season activity: " + activity);

        // -------------------------------------------------------
        // CONCEPT 6: break and continue in loops
        //
        // break    = exit the loop immediately
        // continue = skip to the next iteration
        // -------------------------------------------------------
        System.out.println("\n--- break and continue ---");

        System.out.print("break at 5: ");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) break; // stops loop entirely
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("skip evens (continue): ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue; // skip even numbers
            System.out.print(i + " ");
        }
        System.out.println();

        // -------------------------------------------------------
        // Varargs demo
        // -------------------------------------------------------
        System.out.println("\n--- Varargs ---");
        System.out.println("average(5, 10)            = " + average(5, 10));
        System.out.println("average(1, 2, 3, 4, 5)    = " + average(1, 2, 3, 4, 5));
        System.out.println("average(90.5, 85.0, 78.3) = " + average(90.5, 85.0, 78.3));
        printAll("Scores", 95, 87, 92, 78, 88);

        // -------------------------------------------------------
        // Arrays demo
        // -------------------------------------------------------
        System.out.println("\n--- Arrays ---");
        demonstrateArrays();

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a Calculator with these overloaded methods:");
        System.out.println("  - calculate(int a, int b, char op)");
        System.out.println("    where op is '+', '-', '*', '/'");
        System.out.println("  - Use a switch expression to select the operation");
        System.out.println("  - Handle division by zero: return 0 and print a warning");
        System.out.println("  - Call it with several pairs and verify results");
        System.out.println("  Bonus: add a sum(int... numbers) varargs method");

        // TODO: EXERCISE — implement Calculator here
    }
}
