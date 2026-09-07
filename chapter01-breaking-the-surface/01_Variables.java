/**
 * CONCEPT: Variables, Primitives, and Type System
 *
 * Java is statically typed — every variable has a type decided at compile time.
 * There are exactly 8 primitive types. Everything else is a reference type (object).
 *
 * Why this matters in interviews:
 *   - Integer overflow causes silent bugs (classic off-by-one in binary search)
 *   - Integer cache (-128 to 127) trips people up with == vs .equals()
 *   - Understanding widening vs narrowing prevents data-loss bugs
 */
class Variables {

    public static void main(String[] args) {

        // --- The 8 primitives ---
        byte   b  = 127;                  // 8-bit  | -128 to 127
        short  s  = 32_767;              // 16-bit | underscores are legal in literals
        int    i  = 2_147_483_647;       // 32-bit | Integer.MAX_VALUE
        long   l  = 9_999_999_999L;      // 64-bit | needs L suffix
        float  f  = 3.14f;              // 32-bit | needs f suffix
        double d  = 3.141592653589793;  // 64-bit | preferred for decimals
        char   c  = 'A';               // 16-bit | Unicode; 'A' == 65
        boolean flag = true;

        // --- Silent overflow: the classic bug ---
        int max = Integer.MAX_VALUE;
        System.out.println("MAX_VALUE + 1 = " + (max + 1)); // wraps to -2147483648 !
        // Fix: use long when values might exceed int range
        long safeMax = (long) max + 1;
        System.out.println("Safe: " + safeMax);

        // --- Widening (automatic, safe) vs Narrowing (explicit, lossy) ---
        int    intVal    = 1_000;
        long   widened   = intVal;        // automatic — no cast needed
        double alsoWide  = intVal;        // int -> double, automatic
        int    narrowed  = (int) 9.99;    // explicit cast — truncates to 9, NOT rounded
        System.out.println("9.99 cast to int: " + narrowed); // 9

        // --- The Integer cache gotcha (critical interview topic) ---
        Integer x = 127;
        Integer y = 127;
        System.out.println("127 == 127 (cached): " + (x == y));   // true  — same cached object

        Integer a = 128;
        Integer b2 = 128;
        System.out.println("128 == 128 (not cached): " + (a == b2));  // false — different objects!
        System.out.println("128.equals(128): " + a.equals(b2));       // true  — always use .equals()

        // --- char arithmetic ---
        char letter = 'A';
        System.out.println((char)(letter + 3));  // D
        System.out.println('Z' - 'A');           // 25 (int result)

        // TRY THIS:
        // 1. What is the result of: byte x = 127; x++; System.out.println(x);
        //    Run it and explain why.
        // 2. Change narrowed = (int) 9.99 to use Math.round() instead.
        //    What changes? Which is correct for rounding up currency?
        // 3. What happens if you add two Integer.MAX_VALUE values as int vs long?
    }
}
