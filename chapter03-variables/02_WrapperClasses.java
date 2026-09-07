/**
 * CONCEPT: Wrapper Classes — Boxing, Unboxing, and the Integer Cache
 *
 * Wrapper classes (Integer, Long, Double, Boolean, Character, etc.) let
 * primitives participate in collections, generics, and reflection.
 *
 * Why this matters:
 *   - Autoboxing in tight loops creates garbage and hurts performance
 *   - The Integer cache (-128 to 127) causes == comparisons to mislead
 *   - Null unboxing causes NPE — one of the sneakiest runtime bugs
 *   - Knowing valueOf vs parseInt vs new Integer is a signal of experience
 */
class WrapperClasses {

    public static void main(String[] args) {

        // --- Autoboxing: primitive → wrapper (automatic) ---
        // --- Unboxing:   wrapper → primitive (automatic) ---
        Integer boxed = 42;           // autoboxing: Integer.valueOf(42)
        int primitive = boxed;        // unboxing: boxed.intValue()

        // --- Integer cache: JVM caches Integer objects from -128 to 127 ---
        Integer x = 100; Integer y = 100;
        Integer p = 200; Integer q = 200;

        System.out.println("100 == 100: " + (x == y)); // true  — SAME cached object
        System.out.println("200 == 200: " + (p == q)); // false — DIFFERENT objects
        System.out.println("200.equals: " + p.equals(q)); // true — always use equals!

        // --- Null unboxing NPE ---
        Integer maybeNull = null;
        try {
            int val = maybeNull; // unboxing null → NPE!
        } catch (NullPointerException e) {
            System.out.println("Unboxing null → NPE");
        }
        // Safe pattern: check before unboxing
        int safe = (maybeNull != null) ? maybeNull : -1;

        // --- Parsing and conversion ---
        int  fromString = Integer.parseInt("42");
        long fromLong   = Long.parseLong("9999999999");
        double fromDbl  = Double.parseDouble("3.14");
        System.out.println("Parsed: " + fromString + ", " + fromLong + ", " + fromDbl);

        // parseInt vs valueOf: parseInt returns primitive, valueOf returns Integer
        Integer cached   = Integer.valueOf("127"); // may return cached object
        int    notCached = Integer.parseInt("127"); // always returns primitive int

        // --- Number conversions ---
        System.out.println("255 binary: " + Integer.toBinaryString(255));
        System.out.println("255 hex:    " + Integer.toHexString(255));
        System.out.println("255 octal:  " + Integer.toOctalString(255));

        // --- Character wrapper ---
        System.out.println("'5' isDigit:  " + Character.isDigit('5'));
        System.out.println("'a' isLetter: " + Character.isLetter('a'));
        System.out.println("'a' toUpper:  " + Character.toUpperCase('a'));
        System.out.println("'A' numeric:  " + Character.getNumericValue('A')); // 10 (hex)

        // --- Autoboxing performance trap ---
        // BAD: creates ~1000 Integer objects and discards them
        long badSum = 0;
        Long boxedSum = 0L; // Long, not long
        for (int i = 0; i < 1000; i++) boxedSum += i; // unbox, add, rebox each iteration

        // GOOD: use primitive
        long primitiveSum = 0;
        for (int i = 0; i < 1000; i++) primitiveSum += i; // zero boxing overhead

        System.out.println("Sum: " + primitiveSum);

        // TRY THIS:
        // 1. What is Integer.MAX_VALUE + 1? What about Long.MAX_VALUE + 1?
        // 2. Create a Map<String, Integer> and use getOrDefault to avoid NPE on missing keys.
        //    When does unboxing happen in: int count = map.get("missing_key") ?
        // 3. Compare Integer.valueOf(127) == Integer.valueOf(127) with 128.
        //    Write a test proving the cache boundary exactly.
    }
}
