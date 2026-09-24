/**
 * EXERCISE — Chapter 16: Data Structures
 *
 * Problem 1: LRU Cache (LeetCode #146)
 * Difficulty: Medium (FAANG staple)
 * -------------------------------------------------------
 *
 * Design a data structure that follows the Least Recently Used (LRU) cache eviction policy.
 *
 * Requirements:
 *   - LRUCache(int capacity): initialize with positive capacity
 *   - int get(int key): return value if key exists, else return -1
 *   - void put(int key, int value): insert or update key.
 *     If capacity is exceeded, evict the LEAST RECENTLY USED key.
 *
 * Both operations must be O(1).
 *
 * Approach:
 *   - LinkedHashMap(capacity, loadFactor, accessOrder=true)
 *     + override removeEldestEntry to enforce capacity
 *
 * OR the "interview-impressive" approach:
 *   - HashMap<key, Node> for O(1) lookup
 *   - Doubly linked list for O(1) move-to-front / remove
 *   - head (MRU) ← [nodes] → tail (LRU)
 *   - HashMap maps key → node pointer
 *
 * Bonus: Implement both approaches and compare code complexity.
 *
 * Test cases:
 *   cache = LRUCache(2)
 *   put(1,1), put(2,2)
 *   get(1)      → 1
 *   put(3,3)    → evicts key 2
 *   get(2)      → -1 (evicted)
 *   put(4,4)    → evicts key 1
 *   get(1)      → -1 (evicted)
 *   get(3)      → 3
 *   get(4)      → 4
 *
 * Problem 2: equals()/hashCode() Contract — Find the Bug, Then Fix It
 * -----------------------------------------------------------------------
 * BrokenPoint below compiles and "looks" fine, but violates the equals/hashCode
 * contract: it overrides equals() (field-based comparison) but does NOT
 * override hashCode() — so it inherits Object's default IDENTITY-based
 * hashCode. This is the single most common version of this bug.
 *
 * 1. BrokenPoint(int x, int y): equals() compares x and y; hashCode() is NOT
 *    overridden (given — don't fix this class, it's the baseline).
 *
 * 2. Demonstrate the bug: create p1 = new BrokenPoint(1,2) and an equal-by-value
 *    p2 = new BrokenPoint(1,2). Confirm p1.equals(p2) is true. Then put p1 into
 *    a HashSet<BrokenPoint> and call set.contains(p2) — it returns FALSE, even
 *    though equals() says they're equal, because HashSet looks in the bucket
 *    for p2's (different, identity-based) hash code first, and never even
 *    calls equals() on p1. This is why the contract says: if a.equals(b), then
 *    a.hashCode() MUST equal b.hashCode().
 *
 * 3. Fix it: implement Point (immutable) with a CORRECT equals() (check class,
 *    then all fields) and hashCode() (Objects.hash(x, y)) — any field used in
 *    one MUST be used in the other. Repeat the HashSet test; contains() must
 *    now return true.
 *
 * 4. Comparator chaining: given a List<Employee> (name, department, salary),
 *    sort using
 *      Comparator.comparing(Employee::department)
 *        .thenComparing(Employee::salary, Comparator.reverseOrder())
 *        .thenComparing(Employee::name)
 *    Print the sorted list and confirm: grouped by department, salary
 *    descending within each department, name as the final tiebreaker.
 */
import java.util.*;

public class Chapter16Exercise {

    // ========= Approach 1: LinkedHashMap (concise) =========
    static class LRUCacheSimple {
        private final int capacity;
        private final LinkedHashMap<Integer, Integer> cache;

        LRUCacheSimple(int capacity) {
            this.capacity = capacity;
            // TODO: initialize LinkedHashMap with accessOrder=true
            // override removeEldestEntry to evict when size > capacity
            this.cache = null; // replace with proper initialization
        }

        public int get(int key) {
            // TODO: return cache value or -1
            return -1;
        }

        public void put(int key, int value) {
            // TODO: put key-value; LinkedHashMap handles eviction via removeEldestEntry
        }
    }

    // ========= Approach 2: HashMap + Doubly Linked List (interview-impressive) =========
    static class LRUCache {
        private static class Node {
            int key, val;
            Node prev, next;
            Node(int k, int v) { key = k; val = v; }
        }

        private final int capacity;
        private final Map<Integer, Node> map;
        private final Node head, tail; // sentinels: head=MRU end, tail=LRU end

        LRUCache(int capacity) {
            this.capacity = capacity;
            this.map  = new HashMap<>();
            head = new Node(0, 0); // dummy
            tail = new Node(0, 0); // dummy
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            // TODO: if key in map, move node to front (MRU), return value
            //       else return -1
            return -1;
        }

        public void put(int key, int value) {
            // TODO:
            //   if key in map: update value, move to front
            //   else: create new node, add to front, put in map
            //         if map.size() > capacity: remove LRU node (tail.prev),
            //           remove from map
        }

        private void addToFront(Node node) {
            // TODO: insert node right after head
        }

        private void removeNode(Node node) {
            // TODO: unlink node from its neighbors
        }
    }

    // ========= Problem 2: given buggy baseline — do not fix this class =========
    static class BrokenPoint {
        final int x, y;
        BrokenPoint(int x, int y) { this.x = x; this.y = y; }

        @Override public boolean equals(Object o) {
            if (!(o instanceof BrokenPoint p)) return false;
            return x == p.x && y == p.y;
        }
        // hashCode() intentionally NOT overridden — inherits Object's identity hash. This is the bug.
    }

    // ========= Problem 2: TODO — fix with a correct equals()/hashCode() pair =========
    static class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }

        @Override public boolean equals(Object o) {
            // TODO: check class, then compare x and y (same as BrokenPoint)
            return false;
        }

        @Override public int hashCode() {
            // TODO: return Objects.hash(x, y);
            return 0;
        }
    }

    // ========= Problem 2: Comparator chaining =========
    record Employee(String name, String department, double salary) {}

    static List<Employee> sortEmployees(List<Employee> employees) {
        // TODO: sort using Comparator.comparing(Employee::department)
        //         .thenComparing(Employee::salary, Comparator.reverseOrder())
        //         .thenComparing(Employee::name)
        return employees;
    }

    public static void main(String[] args) {
        System.out.println("=== LRU Cache Test ===");
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println("get(1): " + cache.get(1) + " (expected 1)");
        cache.put(3, 3);    // evicts key 2
        System.out.println("get(2): " + cache.get(2) + " (expected -1)");
        cache.put(4, 4);    // evicts key 1
        System.out.println("get(1): " + cache.get(1) + " (expected -1)");
        System.out.println("get(3): " + cache.get(3) + " (expected 3)");
        System.out.println("get(4): " + cache.get(4) + " (expected 4)");

        System.out.println("\n=== Edge Cases ===");
        LRUCache c2 = new LRUCache(1);
        c2.put(1, 1);
        c2.put(2, 2);  // evicts 1
        System.out.println("get(1): " + c2.get(1) + " (expected -1)");
        System.out.println("get(2): " + c2.get(2) + " (expected 2)");

        System.out.println("\n=== Simple (LinkedHashMap) Test ===");
        LRUCacheSimple simple = new LRUCacheSimple(2);
        simple.put(1, 1); simple.put(2, 2);
        System.out.println("get(1): " + simple.get(1) + " (expected 1)");
        simple.put(3, 3);
        System.out.println("get(2): " + simple.get(2) + " (expected -1)");

        System.out.println("\n=== Problem 2: equals()/hashCode() Contract ===");
        BrokenPoint bp1 = new BrokenPoint(1, 2);
        BrokenPoint bp2 = new BrokenPoint(1, 2);
        System.out.println("bp1.equals(bp2): " + bp1.equals(bp2) + " (expected true)");
        Set<BrokenPoint> brokenSet = new HashSet<>();
        brokenSet.add(bp1);
        System.out.println("brokenSet.contains(bp2): " + brokenSet.contains(bp2) +
            " (expected — and buggy — false: equal objects, different hash codes)");

        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        Set<Point> fixedSet = new HashSet<>();
        fixedSet.add(p1);
        System.out.println("fixedSet.contains(p2): " + fixedSet.contains(p2) + " (expected true)");

        System.out.println("\n=== Problem 2: Comparator Chaining ===");
        List<Employee> employees = new ArrayList<>(List.of(
            new Employee("Dave", "Engineering", 95000),
            new Employee("Alice", "Engineering", 110000),
            new Employee("Carol", "Sales", 80000),
            new Employee("Bob", "Sales", 80000)
        ));
        sortEmployees(employees).forEach(System.out::println);
        System.out.println("Expected order: Alice(Eng,110k), Dave(Eng,95k), Bob(Sales,80k), Carol(Sales,80k)");
    }
}
