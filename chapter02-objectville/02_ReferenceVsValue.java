/**
 * CONCEPT: Reference vs Value — how Java passes arguments
 *
 * Java is ALWAYS pass-by-value. For objects, the value passed is the reference
 * (memory address). This is one of the most misunderstood Java concepts.
 *
 * Rule:
 *   - Primitives: a COPY of the value is passed — caller's variable unchanged
 *   - Objects:    a COPY of the REFERENCE is passed — caller's object CAN be mutated,
 *                 but the caller's VARIABLE cannot be made to point elsewhere
 *
 * Why this matters:
 *   - Writing a swap(a, b) method that works requires returning values or using wrappers
 *   - Defensive copying prevents unintended mutation of objects from outside
 *   - This affects how you write unit-testable, safe APIs
 */
class ReferenceVsValue {

    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override public String toString() { return "(" + x + ", " + y + ")"; }
    }

    // Primitives: change inside method does NOT affect caller
    static void tryDouble(int n) {
        n = n * 2;
        System.out.println("  inside tryDouble: " + n);
    }

    // Object: the object's state CAN be changed (same object, new field values)
    static void shift(Point p, int dx, int dy) {
        p.x += dx;  // mutates the actual Point object
        p.y += dy;
    }

    // Object: reassigning the parameter does NOT affect caller's variable
    static void tryReplace(Point p) {
        p = new Point(999, 999);  // p now points to new object — caller unaffected
        System.out.println("  inside tryReplace: " + p);
    }

    // Proper swap using an array wrapper (since Java has no out/ref params)
    static void swapInArray(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    public static void main(String[] args) {

        // --- Primitive pass-by-value ---
        int num = 10;
        System.out.println("Before tryDouble: " + num);
        tryDouble(num);
        System.out.println("After tryDouble:  " + num); // still 10

        // --- Object pass-by-value-of-reference ---
        Point p1 = new Point(1, 2);
        System.out.println("\nBefore shift: " + p1);
        shift(p1, 5, 3);
        System.out.println("After shift:  " + p1); // (6, 5) — object was mutated

        System.out.println("\nBefore tryReplace: " + p1);
        tryReplace(p1);
        System.out.println("After tryReplace:  " + p1); // still (6, 5) — p1 unchanged

        // --- Swapping via array ---
        int[] pair = {3, 7};
        System.out.println("\nBefore swap: " + pair[0] + ", " + pair[1]);
        swapInArray(pair, 0, 1);
        System.out.println("After swap:  " + pair[0] + ", " + pair[1]);

        // --- Defensive copy: protecting internal state ---
        System.out.println("\n--- Defensive copy ---");
        int[] original = {1, 2, 3};
        int[] alias = original;            // NOT a copy — same array
        int[] copy  = original.clone();    // real defensive copy

        alias[0] = 99;
        System.out.println("original[0] via alias: " + original[0]); // 99 — changed!
        System.out.println("copy[0] (safe):        " + copy[0]);     // 1  — protected

        // TRY THIS:
        // 1. Write a method resetPoint(Point p) that tries to set p to (0,0).
        //    Does the caller's Point change? Why?
        // 2. Create a SafePoint class that returns a copy from getCoords()
        //    so callers can't mutate internal state.
        // 3. Try writing a swap(int a, int b) that actually swaps two int variables.
        //    Why can't you do it? What's the workaround?
    }
}
