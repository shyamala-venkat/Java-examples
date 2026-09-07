/**
 * Chapter 1: Breaking the Surface
 *
 * Key Concepts:
 *  - What Java code looks like (class, main method)
 *  - Printing output with System.out.println
 *  - Variables: declaring and assigning
 *  - Basic types: int, String, boolean
 *  - Loops: while and for
 *  - Conditionals: if / else if / else
 *  - How Java compiles (.java -> .class -> JVM runs it)
 *
 * How to run:
 *   javac Chapter01Main.java
 *   java Chapter01Main
 */
public class Chapter01Main {

    public static void main(String[] args) {

        // -------------------------------------------------------
        // CONCEPT 1: Printing to the console
        // System.out.println prints text and moves to the next line.
        // System.out.print prints text WITHOUT a newline.
        // -------------------------------------------------------
        System.out.println("=== Chapter 1: Breaking the Surface ===");
        System.out.println("Hello, World!");
        System.out.print("No newline here... ");
        System.out.println("and this continues on the same line.");

        // -------------------------------------------------------
        // CONCEPT 2: Variables — declaring and assigning values
        // Format:  type variableName = value;
        // Java is statically typed: you must declare the type.
        // -------------------------------------------------------
        int age = 25;             // whole numbers
        double salary = 75000.50; // decimal numbers
        boolean isEmployed = true;
        String name = "Alex";     // String is a class, not a primitive

        System.out.println("\n--- Variables ---");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Salary: " + salary);
        System.out.println("Employed: " + isEmployed);

        // The + operator concatenates (joins) Strings.
        // When you add a String to a number, Java converts the number to String first.
        System.out.println("Age next year: " + (age + 1)); // parentheses matter!

        // -------------------------------------------------------
        // CONCEPT 3: Conditionals — if / else if / else
        // Java evaluates the condition (true or false) and runs
        // the matching block.
        // -------------------------------------------------------
        System.out.println("\n--- Conditionals ---");
        int score = 82;

        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else if (score >= 70) {
            System.out.println("Grade: C");
        } else {
            System.out.println("Grade: F");
        }

        // -------------------------------------------------------
        // CONCEPT 4: while loop
        // Keeps running as long as the condition is true.
        // Risk: if the condition never becomes false, you get an
        // infinite loop!
        // -------------------------------------------------------
        System.out.println("\n--- While Loop (countdown) ---");
        int countdown = 5;
        while (countdown > 0) {
            System.out.println("T-minus " + countdown);
            countdown--; // same as countdown = countdown - 1
        }
        System.out.println("Liftoff!");

        // -------------------------------------------------------
        // CONCEPT 5: for loop
        // Use for when you know exactly how many times to loop.
        // Format: for (init; condition; update)
        // -------------------------------------------------------
        System.out.println("\n--- For Loop (squares 1-5) ---");
        for (int i = 1; i <= 5; i++) {
            System.out.println(i + " squared = " + (i * i));
        }

        // -------------------------------------------------------
        // CONCEPT 6: Nested loops
        // A loop inside a loop. The inner loop completes fully
        // for every single iteration of the outer loop.
        // -------------------------------------------------------
        System.out.println("\n--- Nested Loops (3x3 grid) ---");
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                System.out.print("[" + row + "," + col + "] ");
            }
            System.out.println(); // newline after each row
        }

        // -------------------------------------------------------
        // CONCEPT 7: Arithmetic operators
        // + addition, - subtraction, * multiply, / divide, % modulo
        // Integer division truncates (drops the decimal).
        // -------------------------------------------------------
        System.out.println("\n--- Arithmetic ---");
        int a = 10, b = 3;
        System.out.println("10 + 3 = " + (a + b));
        System.out.println("10 - 3 = " + (a - b));
        System.out.println("10 * 3 = " + (a * b));
        System.out.println("10 / 3 = " + (a / b));      // 3 (integer division!)
        System.out.println("10.0 / 3 = " + (10.0 / b)); // 3.333... (double division)
        System.out.println("10 % 3 = " + (a % b));      // 1 (remainder)

        // -------------------------------------------------------
        // EXERCISE (solve this yourself!)
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Print a multiplication table for numbers 1 through 5.");
        System.out.println("Expected output format:");
        System.out.println("1 x 1 = 1");
        System.out.println("1 x 2 = 2");
        System.out.println("... up to ...");
        System.out.println("5 x 5 = 25");
        System.out.println("Hint: use nested for loops.");

        // TODO: EXERCISE — write your solution below this line
        // Remove the example output above and print the actual table.

    }
}
