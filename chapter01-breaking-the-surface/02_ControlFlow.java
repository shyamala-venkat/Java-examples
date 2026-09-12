/**
 * CONCEPT: Control Flow — if/else, switch expressions, loops
 *
 * Modern Java (14+) switch expressions replace the error-prone switch statement.
 * Knowing the difference is a senior-level signal in interviews.
 *
 * Why this matters:
 *   - Fall-through bugs in classic switch are a common source of defects
 *   - Switch expressions are exhaustive — the compiler catches missing cases
 *   - Loop choice affects both readability and performance
 */
class ControlFlow {

    public static void main(String[] args) {

        // --- Classic switch STATEMENT (pre-Java 14) — fall-through is a footgun ---
        int day = 3;
        System.out.print("Classic switch: ");
        switch (day) {
            case 1: System.out.print("Mon "); // falls through!
            case 2: System.out.print("Tue "); // falls through!
            case 3: System.out.print("Wed "); break; // stops here
            default: System.out.print("Other");
        }
        System.out.println();

        // --- Modern switch EXPRESSION (Java 14+) — no fall-through, returns value ---
        String dayName = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6, 7 -> "Weekend";   // multiple labels in one arm
            default -> throw new IllegalArgumentException("Invalid day: " + day);
        };
        System.out.println("Modern switch: " + dayName);

        // Switch with a yield (for multi-statement arms)
        int score = 85;
        String grade = switch (score / 10) {
            case 10, 9 -> "A";
            case 8     -> "B";
            case 7     -> "C";
            default    -> {
                String g = score >= 60 ? "D" : "F";
                System.out.println("  (computed grade: " + g + ")");
                yield g;              // yield returns the value from a block arm
            }
        };
        System.out.println("Grade: " + grade);

        // --- Loop comparison ---
        // for-each: use when you don't need the index
        int[] primes = {2, 3, 5, 7, 11};
        int sum = 0;
        for (int p : primes) sum += p;
        System.out.println("Sum of primes: " + sum);

        // classic for: use when you need the index or custom step
        System.out.print("Even indices: ");
        for (int i = 0; i < primes.length; i += 2) {
            System.out.print(primes[i] + " ");
        }
        System.out.println();

        // while: use for unknown number of iterations (e.g., reading until sentinel)
        int n = 1024;
        int halvings = 0;
        while (n > 1) { n /= 2; halvings++; }
        System.out.println("1024 halved to 1 in " + halvings + " steps"); // 10 = log2(1024)

        // --- Ternary: concise but don't nest it ---
        int value = 42;
        String parity = (value % 2 == 0) ? "even" : "odd";
        System.out.println(value + " is " + parity);

        // TRY THIS:
        // 1. Remove the `break` from case 3 in the classic switch — observe fall-through.
        // 2. Add case 8 to the switch expression with yield and a custom log message.
        // 3. Rewrite the while loop as a for loop. Which reads better?
        // 4. Try passing day = 8 to the modern switch — what happens and why?
    }
}
