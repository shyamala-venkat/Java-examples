/**
 * EXERCISE — Chapter 1: Breaking the Surface
 *
 * Problem 1: FizzBuzz (Classic but with a twist)
 * -----------------------------------------------
 * Print numbers 1 to 100 with these rules:
 *   - Divisible by 3 and 5 → "FizzBuzz"
 *   - Divisible by 3       → "Fizz"
 *   - Divisible by 5       → "Buzz"
 *   - Divisible by 7       → "Bazz"  ← twist: also handle 7
 *   - Multiple rules apply → concatenate (e.g., 21 → "FizzBazz", 35 → "BuzzBazz")
 *
 * Senior tip: write it in a way that's easy to ADD a new rule (e.g., divisible by 11 → "Jazz")
 * without changing existing if-else chains. Think extensibility.
 *
 * Problem 2: Count Primes (Sieve of Eratosthenes)
 * -------------------------------------------------
 * Count the number of prime numbers less than n = 30.
 * Brute force is O(n√n). The Sieve is O(n log log n) — know the difference.
 *
 * Expected output:
 *   Primes < 30: [2, 3, 5, 7, 11, 13, 17, 19, 23, 29] — count: 10
 *
 * Problem 3: Integer Reversal with Overflow Check
 * -------------------------------------------------
 * Reverse the digits of an integer. If the reversed integer overflows
 * 32-bit signed range, return 0.
 * Input: 123  → Output: 321
 * Input: -456 → Output: -654
 * Input: 1534236469 → Output: 0 (overflow)
 *
 * Implement all three below.
 */
public class Chapter01Exercise {

    public static void main(String[] args) {

        System.out.println("=== Problem 1: Extended FizzBuzz ===");
        fizzBuzz(20); // print first 20 so output is manageable

        System.out.println("\n=== Problem 2: Count Primes (Sieve) ===");
        int[] primes = sieve(30);
        System.out.print("Primes < 30: [");
        for (int i = 0; i < primes.length; i++) {
            System.out.print(primes[i]);
            if (i < primes.length - 1) System.out.print(", ");
        }
        System.out.println("] — count: " + primes.length);

        System.out.println("\n=== Problem 3: Integer Reversal ===");
        System.out.println(reverseInt(123));         // 321
        System.out.println(reverseInt(-456));        // -654
        System.out.println(reverseInt(1534236469));  // 0 (overflow)
    }

    // --- Problem 1 ---
    // TODO: implement extensible FizzBuzz
    // Hint: use a String[] rules = {"Fizz", "Buzz", "Bazz"} and int[] divisors = {3, 5, 7}
    // Build the result by iterating rules instead of chaining if-else.
    static void fizzBuzz(int n) {
        // YOUR CODE HERE
    }

    // --- Problem 2 ---
    // Sieve of Eratosthenes — mark all multiples of each prime as composite
    // Returns int[] of all primes less than n
    static int[] sieve(int n) {
        // YOUR CODE HERE
        return new int[0];
    }

    // --- Problem 3 ---
    // Reverse digits. Return 0 on overflow.
    // Hint: use long to detect overflow before casting back to int.
    static int reverseInt(int x) {
        // YOUR CODE HERE
        return 0;
    }
}
