/**
 * CONCEPT: Recursion — thinking in base cases and sub-problems
 *
 * Recursion is a method calling itself with a smaller version of the problem.
 * Every recursive solution has:
 *   1. Base case(s) — the stopping condition
 *   2. Recursive case — the problem reduced toward the base case
 *
 * Why this matters:
 *   - Tree traversal, graph DFS, divide-and-conquer all use recursion
 *   - Stack overflow from infinite recursion is a production risk
 *   - Tail recursion optimization (Java doesn't do it — know why)
 *   - Converting recursion to iteration (with explicit stack) avoids stack overflow
 */
class Recursion {

    // --- Classic: Fibonacci (naive O(2^n) vs memoized O(n)) ---
    static int fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2); // exponential — fib(40) is slow
    }

    // Memoized: each subproblem solved once — O(n)
    static long[] memo = new long[100];
    static long fibMemo(int n) {
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n];
        memo[n] = fibMemo(n - 1) + fibMemo(n - 2);
        return memo[n];
    }

    // --- Binary search (recursive) ---
    static int binarySearch(int[] arr, int target, int lo, int hi) {
        if (lo > hi) return -1; // base case: not found
        int mid = lo + (hi - lo) / 2; // avoids overflow vs (lo+hi)/2
        if (arr[mid] == target) return mid;
        if (arr[mid] < target)  return binarySearch(arr, target, mid + 1, hi);
        else                    return binarySearch(arr, target, lo, mid - 1);
    }

    // --- Power: x^n using fast exponentiation O(log n) ---
    static long power(long x, int n) {
        if (n == 0) return 1;
        if (n % 2 == 0) {
            long half = power(x, n / 2);
            return half * half;                // x^n = (x^(n/2))^2
        }
        return x * power(x, n - 1);
    }

    // --- Permutations: generates all orderings of a string ---
    static void permutations(String prefix, String remaining) {
        if (remaining.isEmpty()) {
            System.out.print(prefix + " ");
            return;
        }
        for (int i = 0; i < remaining.length(); i++) {
            permutations(
                prefix + remaining.charAt(i),
                remaining.substring(0, i) + remaining.substring(i + 1)
            );
        }
    }

    // --- Converting recursion to iteration to avoid StackOverflow ---
    static long fibIterative(int n) {
        if (n <= 1) return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public static void main(String[] args) {

        System.out.println("--- Fibonacci ---");
        System.out.println("fib(10) naive:    " + fibNaive(10));
        System.out.println("fib(10) memoized: " + fibMemo(10));
        System.out.println("fib(50) memoized: " + fibMemo(50));
        System.out.println("fib(50) iterative:" + fibIterative(50));
        // fibNaive(50) would take minutes — don't run it!

        System.out.println("\n--- Binary Search ---");
        int[] sorted = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91};
        System.out.println("Find 23: index " + binarySearch(sorted, 23, 0, sorted.length - 1)); // 5
        System.out.println("Find 99: index " + binarySearch(sorted, 99, 0, sorted.length - 1)); // -1

        System.out.println("\n--- Fast Power ---");
        System.out.println("2^10 = " + power(2, 10));   // 1024
        System.out.println("3^5  = " + power(3, 5));    // 243
        System.out.println("2^30 = " + power(2, 30));   // 1073741824

        System.out.println("\n--- Permutations of 'abc' ---");
        permutations("", "abc");
        System.out.println();

        // TRY THIS:
        // 1. Implement factorial(int n) recursively. What's the largest n before overflow?
        //    Use BigInteger to compute factorial(100).
        // 2. Implement countDown(int n) that prints n, n-1, ..., 1, "Go!"
        //    Convert it to an iterative version. Which is clearer?
        // 3. What is the maximum recursion depth before StackOverflowError on your JVM?
        //    Write a method that counts how deep it goes before crashing.
    }
}
