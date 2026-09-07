/**
 * CONCEPT: Type Casting — widening, narrowing, and the hidden traps
 *
 * Casting converts a value from one type to another. Getting it wrong
 * silently corrupts data — one of the hardest bugs to trace.
 *
 * Why this matters:
 *   - Integer division in calculations is a classic silent bug
 *   - Long arithmetic overflow in financial/scientific code causes data loss
 *   - Casting between numeric types is common in stream processing and DB results
 */
class TypeCasting {

    public static void main(String[] args) {

        // --- Widening: automatic, no data loss ---
        int i = 100_000;
        long  l = i;         // int → long, automatic
        float f = i;         // int → float, automatic (but may lose precision!)
        double d = i;        // int → double, automatic

        System.out.println("int    -> long:   " + l);  // 100000
        System.out.println("int    -> float:  " + f);  // 100000.0
        System.out.println("int    -> double: " + d);  // 100000.0

        // float → double is safe, but int → float can lose precision for large values
        int bigInt = 123_456_789;
        float imprecise = bigInt;
        System.out.println("Large int as float: " + imprecise); // 1.23456792E8 — different!
        System.out.println("Difference: " + (bigInt - (int) imprecise)); // not zero

        // --- Narrowing: explicit required, truncates ---
        double price = 9.99;
        int dollars = (int) price;   // truncates toward zero — NOT rounded
        System.out.println("\n9.99 → int:  " + dollars);              // 9
        System.out.println("Rounded:     " + Math.round(price));      // 10 (long)
        System.out.println("Math.floor:  " + (int) Math.floor(price)); // 9

        // --- The classic integer division mistake ---
        int a = 5, b = 2;
        double wrong  = a / b;       // integer division first → 2, then widened to 2.0
        double correct = (double) a / b; // cast BEFORE division
        System.out.println("\nInteger division: " + wrong);   // 2.0 — WRONG
        System.out.println("Correct division: " + correct);  // 2.5

        // --- Long overflow trap in calculations ---
        int seconds = 60;
        int minutesInYear = 60 * 24 * 365;
        long secondsInYear = (long) seconds * minutesInYear; // cast BEFORE multiply
        long overflow = seconds * minutesInYear;             // int multiply overflows!
        System.out.println("\nSeconds in a year (correct): " + secondsInYear);
        System.out.println("Overflow version:             " + overflow); // wrong or same by luck

        // --- char ↔ int ---
        char ch = 'A';
        int code = ch;              // widening: char → int
        System.out.println("\n'A' as int: " + code);        // 65
        char back = (char)(code + 1);
        System.out.println("65+1 as char: " + back);       // 'B'

        // --- instanceof before casting (avoid ClassCastException) ---
        Object obj = "Hello";
        if (obj instanceof String s) {       // pattern matching instanceof (Java 16+)
            System.out.println("\nLength: " + s.length()); // safe — no cast needed
        }

        // TRY THIS:
        // 1. What is (int)(2.5 + 2.5)? What about (int)2.5 + (int)2.5? Why different?
        // 2. Compute percentage: 1 item out of 3 items. Why does (1/3)*100 give 0?
        //    Fix it with proper casting.
        // 3. What is the result of (byte)(127 + 1)? Run it. Why?
    }
}
