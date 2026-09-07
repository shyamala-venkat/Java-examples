/**
 * CONCEPT: ArrayList vs LinkedList vs ArrayDeque
 *
 * Choosing the wrong data structure is a common senior-level mistake.
 * Each structure has different time complexity for different operations.
 *
 * ArrayList:   O(1) get, O(n) insert-at-middle, O(1) amortized add-at-end
 * LinkedList:  O(n) get, O(1) insert-at-position-if-iterator, O(1) add/remove ends
 * ArrayDeque:  O(1) add/remove at BOTH ends, no nulls allowed
 *
 * Rule of thumb:
 *   - Default to ArrayList (cache-friendly, less memory overhead)
 *   - Use ArrayDeque for stack or queue behavior
 *   - Use LinkedList only when you frequently remove from the middle with a ListIterator
 *
 * Why this matters:
 *   - ArrayList.get(i) is O(1); LinkedList.get(i) is O(n) — huge for large lists
 *   - ArrayDeque beats Stack (legacy, synchronized) and LinkedList as a queue
 */
import java.util.*;

class ListsAndDeques {

    public static void main(String[] args) {

        // --- ArrayList: random access O(1), insertion at middle O(n) ---
        System.out.println("=== ArrayList ===");
        List<String> arrayList = new ArrayList<>(8); // hint initial capacity
        arrayList.addAll(Arrays.asList("Apple", "Banana", "Cherry", "Date"));
        System.out.println("Before: " + arrayList);
        arrayList.add(1, "Avocado");       // O(n) — shifts everything right
        arrayList.remove("Cherry");        // O(n) — scan + shift
        System.out.println("After:  " + arrayList);
        System.out.println("Get(2): " + arrayList.get(2)); // O(1)

        // subList is a VIEW — mutations affect original
        List<String> view = arrayList.subList(1, 3);
        System.out.println("SubList view: " + view);
        view.clear(); // clears elements from original list!
        System.out.println("After subList clear: " + arrayList);

        // --- ArrayDeque: O(1) at both ends — use as stack AND queue ---
        System.out.println("\n=== ArrayDeque as Stack (LIFO) ===");
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(10); stack.push(20); stack.push(30);
        System.out.println("Stack: " + stack);         // [30, 20, 10]
        System.out.println("peek: " + stack.peek());   // 30, no remove
        System.out.println("pop:  " + stack.pop());    // 30, removed
        System.out.println("After pop: " + stack);

        System.out.println("\n=== ArrayDeque as Queue (FIFO) ===");
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("first"); queue.offer("second"); queue.offer("third");
        System.out.println("Queue: " + queue);           // [first, second, third]
        System.out.println("peek: " + queue.peek());     // first, no remove
        System.out.println("poll: " + queue.poll());     // first, removed
        System.out.println("After poll: " + queue);

        // --- PriorityQueue: min-heap by default ---
        System.out.println("\n=== PriorityQueue (min-heap) ===");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        int[] nums = {5, 1, 3, 9, 2, 7};
        for (int n : nums) minHeap.offer(n);
        System.out.print("Sorted order (polling): ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " ");
        System.out.println();

        // Max-heap via reversed comparator
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        for (int n : nums) maxHeap.offer(n);
        System.out.print("Reverse order (max-heap): ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " ");
        System.out.println();

        // --- Iterator safety: ConcurrentModificationException ---
        System.out.println("\n=== Safe removal with removeIf ===");
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        // WRONG: for (int n : numbers) if (n % 2 == 0) numbers.remove(n); // CME!
        numbers.removeIf(n -> n % 2 == 0); // CORRECT: bulk removal
        System.out.println("Odd only: " + numbers);

        // TRY THIS:
        // 1. Time ArrayList.get(n-1) vs LinkedList.get(n-1) for n=100_000.
        //    Observe O(1) vs O(n) difference.
        // 2. Use ArrayDeque to implement a browser history:
        //    visit(url) → addFirst; back() → pollFirst; forward() → ???
        //    How do you handle forward after a new visit?
        // 3. Use PriorityQueue<int[]> with custom Comparator to implement a
        //    "k-closest points to origin" algorithm (LeetCode #973).
    }
}
