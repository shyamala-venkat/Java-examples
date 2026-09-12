/**
 * CONCEPT: Strings — immutability, StringBuilder, common operations
 *
 * String is the most commonly asked topic in Java interviews.
 * Knowing the difference between String, StringBuilder, and StringBuffer
 * and when to use each is a must for senior roles.
 *
 * Why this matters:
 *   - String is immutable — string concatenation in a loop is O(n²) without StringBuilder
 *   - String pool and == vs .equals() is a classic interview trap
 *   - String manipulation is everywhere: parsing, serialization, API responses
 */
class StringFundamentals {

    public static void main(String[] args) {

        // --- Immutability: every "modification" creates a new object ---
        String s = "hello";
        s.toUpperCase();                          // original s unchanged — returns new String
        System.out.println(s);                    // still "hello"
        s = s.toUpperCase();                      // must reassign to use the result
        System.out.println(s);                    // "HELLO"

        // --- String pool vs heap ---
        String a = "java";          // stored in the string pool (interned)
        String b = "java";          // same pool object — a == b is true
        String c = new String("java"); // forced onto heap — different object

        System.out.println("a == b (pool):    " + (a == b));      // true
        System.out.println("a == c (heap):    " + (a == c));      // false
        System.out.println("a.equals(c):      " + a.equals(c));   // true — always use equals

        // --- Concatenation in a loop: the O(n²) trap ---
        // BAD: creates a new String object on every iteration
        String bad = "";
        for (int i = 0; i < 5; i++) bad += i; // 5 intermediate objects discarded

        // GOOD: StringBuilder is mutable — no intermediate objects
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i);
        String good = sb.toString();
        System.out.println("Result: " + good); // "01234"

        // --- Common String operations ---
        String text = "  Hello, World!  ";
        System.out.println(text.trim());                    // strip leading/trailing spaces
        System.out.println(text.strip());                   // Unicode-aware trim (Java 11+)
        System.out.println("hello world".contains("world")); // true
        System.out.println("hello".startsWith("he"));       // true
        System.out.println("hello".charAt(1));              // 'e'
        System.out.println("hello".indexOf('l'));            // 2
        System.out.println("hello".lastIndexOf('l'));        // 3
        System.out.println("hello world".replace("world", "java")); // hello java
        System.out.println(String.join("-", "a", "b", "c")); // a-b-c

        // --- Splitting and joining ---
        String csv = "Alice,30,Engineer";
        String[] parts = csv.split(",");
        System.out.println("Name: " + parts[0] + ", Age: " + parts[1]);

        // --- Useful for parsing ---
        System.out.println("Is digit: " + Character.isDigit('5'));
        System.out.println("Is letter: " + Character.isLetter('A'));
        System.out.println("To lower: " + Character.toLowerCase('Z'));

        // --- String as char array (useful in many interview problems) ---
        char[] chars = "abcde".toCharArray();
        chars[0] = 'z';
        System.out.println(new String(chars)); // "zbcde"

        // --- StringBuilder for palindrome check (in-place reversal) ---
        String word = "racecar";
        String reversed = new StringBuilder(word).reverse().toString();
        System.out.println(word + " is palindrome: " + word.equals(reversed));

        // TRY THIS:
        // 1. Write a method that counts occurrences of a char in a String without
        //    using any built-in count methods — just charAt() and a loop.
        // 2. Reverse words in a sentence: "Hello World" -> "World Hello"
        //    Use split() and StringBuilder.
        // 3. Check if two strings are anagrams using only sorting (Arrays.sort on char[]).
        // 4. What happens when you compare null with .equals()? How do you safely compare?
    }
}
