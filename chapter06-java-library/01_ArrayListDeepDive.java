/**
 * CONCEPT: ArrayList Internals and Performance
 *
 * ArrayList is backed by a resizable array. When it's full, it allocates
 * a new array ~1.5x larger and copies all elements. This amortizes to O(1) add.
 *
 * Why this matters:
 *   - Choosing the right initial capacity avoids costly resizing in tight loops
 *   - remove(int index) is O(n) — avoid in hot paths
 *   - List vs ArrayList typing: code to the interface
 *   - Knowing when NOT to use ArrayList (LinkedList for queue, Set for uniqueness)
 */
import java.util.*;

class ArrayListDeepDive {

    public static void main(String[] args) {

        // --- Initial capacity: avoids resize when size is known ---
        List<String> names = new ArrayList<>(1000); // no resize for first 1000 adds
        List<String> small = new ArrayList<>();      // default capacity: 10

        // --- Core operations ---
        List<String> fruits = new ArrayList<>(Arrays.asList("apple", "banana", "cherry"));
        fruits.add("date");                       // append: O(1) amortized
        fruits.add(1, "avocado");                 // insert at index: O(n) — shifts elements
        fruits.set(0, "apricot");                 // replace: O(1)
        fruits.remove("banana");                  // remove by value: O(n) scan + shift
        fruits.remove(0);                         // remove by index: O(n) shift

        System.out.println("After ops: " + fruits);
        System.out.println("Size: " + fruits.size());
        System.out.println("Contains cherry: " + fruits.contains("cherry")); // O(n) scan

        // --- Sorting and searching ---
        List<Integer> nums = new ArrayList<>(Arrays.asList(5, 3, 8, 1, 9, 2));
        Collections.sort(nums);                   // O(n log n)
        System.out.println("Sorted: " + nums);
        int idx = Collections.binarySearch(nums, 8); // O(log n) — must be sorted first
        System.out.println("8 at index: " + idx);

        // --- Sublist: a VIEW, not a copy ---
        List<Integer> sub = nums.subList(1, 4);   // [3, 5, 8]
        System.out.println("Sublist: " + sub);
        sub.set(0, 99);
        System.out.println("Original after sublist mutation: " + nums); // changed!

        // --- Iteration: removeIf is the safe way to remove while iterating ---
        List<Integer> mutable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        mutable.removeIf(n -> n % 2 == 0);       // safe bulk removal
        System.out.println("Odds only: " + mutable);

        // ConcurrentModificationException: modifying list while iterating with for-each
        List<String> words = new ArrayList<>(Arrays.asList("hello", "hi", "hey", "bye"));
        // for (String w : words) { if (w.startsWith("h")) words.remove(w); } // THROWS!
        // Fix: use removeIf or an Iterator
        words.removeIf(w -> w.startsWith("h"));
        System.out.println("After removeIf: " + words); // [bye]

        // --- List.of() vs new ArrayList() ---
        List<String> immutable = List.of("a", "b", "c");  // immutable, no nulls
        List<String> mutableCopy = new ArrayList<>(immutable); // mutable copy
        // immutable.add("d"); // UnsupportedOperationException

        System.out.println("Immutable: " + immutable);

        // TRY THIS:
        // 1. Time the difference between inserting 100,000 elements at index 0
        //    vs appending. Why such a huge difference?
        // 2. LinkedList also implements List. Write a benchmark comparing
        //    ArrayList vs LinkedList for: append 10000 elements, then iterate all.
        // 3. What does Collections.unmodifiableList() return? Is it a copy?
        //    Try modifying the original list and observe the "unmodifiable" view.
    }
}
