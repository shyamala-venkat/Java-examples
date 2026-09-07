/**
 * CONCEPT: Arrays — fixed-size, contiguous memory
 *
 * Arrays are the foundation of almost every data structure question.
 * Interviewers often start here: two-pointer, sliding window, prefix sums.
 *
 * Why this matters:
 *   - Arrays have O(1) random access but O(n) insert/delete
 *   - Two-pointer technique avoids nested loops (O(n²) → O(n))
 *   - Prefix sum turns repeated range-sum queries from O(n) to O(1)
 */
import java.util.Arrays;

class ArrayFundamentals {

    public static void main(String[] args) {

        // --- Declaration forms ---
        int[] arr1 = new int[5];                      // all zeros
        int[] arr2 = {3, 1, 4, 1, 5, 9, 2, 6};       // literal
        int[] arr3 = new int[]{10, 20, 30};           // explicit new

        // --- Common pitfall: array reference vs copy ---
        int[] original = {1, 2, 3};
        int[] aliased  = original;        // same array, NOT a copy
        int[] copied   = Arrays.copyOf(original, original.length); // real copy

        aliased[0] = 99;
        System.out.println("original[0] via aliased: " + original[0]); // 99 — aliased changed it!
        System.out.println("copied[0]:               " + copied[0]);    // 1  — independent

        // --- Sorting and searching ---
        int[] nums = {5, 2, 8, 1, 9, 3};
        Arrays.sort(nums);
        System.out.println("Sorted: " + Arrays.toString(nums));

        int idx = Arrays.binarySearch(nums, 8); // array MUST be sorted first
        System.out.println("8 is at index: " + idx);

        // --- Two-pointer technique: check if array has a pair summing to target ---
        // Brute force: O(n²) — two nested loops
        // Two-pointer: O(n) after O(n log n) sort — total O(n log n)
        int target = 11;
        System.out.println("Pair summing to " + target + ": " + hasPairWithSum(nums, target));

        // --- Prefix sum: range sum queries in O(1) after O(n) build ---
        int[] data = {2, 4, 6, 8, 10};
        int[] prefix = buildPrefixSum(data);
        System.out.println("Prefix: " + Arrays.toString(prefix)); // [0, 2, 6, 12, 20, 30]
        System.out.println("Sum [1..3]: " + rangeSum(prefix, 1, 3)); // 4+6+8 = 18

        // --- 2D arrays ---
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.print("Diagonal: ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(matrix[i][i] + " "); // 1 5 9
        }
        System.out.println();

        // TRY THIS:
        // 1. Modify hasPairWithSum to also print which two numbers form the pair.
        // 2. Use two-pointer to find a triplet summing to a target (3Sum — harder).
        // 3. Build the prefix sum for a 2D matrix (useful for image processing / DP).
        // 4. What happens if you call Arrays.binarySearch on an unsorted array?
    }

    // Two-pointer: sorted array required, O(n)
    static boolean hasPairWithSum(int[] sorted, int target) {
        int left = 0, right = sorted.length - 1;
        while (left < right) {
            int sum = sorted[left] + sorted[right];
            if      (sum == target) return true;
            else if (sum < target)  left++;
            else                    right--;
        }
        return false;
    }

    // prefix[i] = sum of data[0..i-1], so prefix[0]=0
    static int[] buildPrefixSum(int[] data) {
        int[] prefix = new int[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            prefix[i + 1] = prefix[i] + data[i];
        }
        return prefix;
    }

    // O(1) range sum query [l, r] inclusive
    static int rangeSum(int[] prefix, int l, int r) {
        return prefix[r + 1] - prefix[l];
    }
}
