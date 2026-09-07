/**
 * Chapter 16: Data Structures — Collections and Generics
 *
 * Key Concepts:
 *  - The Collections Framework hierarchy
 *  - List: ArrayList vs LinkedList
 *  - Set: HashSet, LinkedHashSet, TreeSet
 *  - Map: HashMap, LinkedHashMap, TreeMap
 *  - Queue and Deque: ArrayDeque, PriorityQueue
 *  - Generics: type parameters, bounded wildcards
 *  - Comparable vs Comparator — custom sorting
 *  - Collections utility class methods
 *  - Iterators
 *
 * How to run:
 *   javac Chapter16Main.java
 *   java Chapter16Main
 */
import java.util.*;

public class Chapter16Main {

    // -------------------------------------------------------
    // CONCEPT: Comparable — natural ordering
    //
    // A class that implements Comparable defines ITS OWN ordering.
    // The compareTo() method returns:
    //   negative: this < other
    //   zero:     this == other
    //   positive: this > other
    // -------------------------------------------------------
    static class Student implements Comparable<Student> {
        private String name;
        private double gpa;
        private int age;

        public Student(String name, double gpa, int age) {
            this.name = name;
            this.gpa = gpa;
            this.age = age;
        }

        // Natural ordering: by GPA descending (highest first)
        @Override
        public int compareTo(Student other) {
            return Double.compare(other.gpa, this.gpa); // reverse for descending
        }

        @Override
        public String toString() {
            return String.format("%-8s gpa=%.1f age=%d", name, gpa, age);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 16: Collections and Generics ===\n");

        // -------------------------------------------------------
        // CONCEPT 1: List — ordered, allows duplicates, indexed access
        //
        // ArrayList:    fast random access (get/set by index), slow insert/delete in middle
        // LinkedList:   fast insert/delete at ends, slow random access, implements Deque
        // -------------------------------------------------------
        System.out.println("--- List: ArrayList vs LinkedList ---");

        // ArrayList — backed by an array, best for most cases
        List<String> arrayList = new ArrayList<>(Arrays.asList("banana", "apple", "cherry", "apple"));
        System.out.println("ArrayList: " + arrayList);
        System.out.println("  get(1):        " + arrayList.get(1));
        System.out.println("  contains:      " + arrayList.contains("apple"));
        System.out.println("  duplicates:    " + Collections.frequency(arrayList, "apple")); // 2

        // LinkedList — backed by doubly-linked list, good for stack/queue operations
        LinkedList<Integer> linkedList = new LinkedList<>(Arrays.asList(10, 20, 30));
        linkedList.addFirst(5);   // O(1) — fast at head
        linkedList.addLast(40);   // O(1) — fast at tail
        linkedList.removeFirst(); // O(1)
        System.out.println("LinkedList: " + linkedList);

        // -------------------------------------------------------
        // CONCEPT 2: Set — no duplicates
        //
        // HashSet:       O(1) add/remove/contains, no ordering
        // LinkedHashSet: O(1), INSERTION order preserved
        // TreeSet:       O(log n), SORTED order (natural or custom Comparator)
        // -------------------------------------------------------
        System.out.println("\n--- Set: HashSet, LinkedHashSet, TreeSet ---");

        Set<String> hashSet = new HashSet<>(Arrays.asList("banana", "apple", "cherry", "apple", "banana"));
        System.out.println("HashSet (no duplicates, no order): " + hashSet);

        Set<String> linkedHashSet = new LinkedHashSet<>(Arrays.asList("banana", "apple", "cherry", "apple"));
        System.out.println("LinkedHashSet (insertion order):   " + linkedHashSet);

        Set<String> treeSet = new TreeSet<>(Arrays.asList("banana", "apple", "cherry", "apple"));
        System.out.println("TreeSet (sorted):                  " + treeSet);

        // Set operations
        Set<Integer> setA = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> setB = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));

        Set<Integer> union = new HashSet<>(setA);
        union.addAll(setB);
        System.out.println("Union:        " + new TreeSet<>(union));

        Set<Integer> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        System.out.println("Intersection: " + new TreeSet<>(intersection));

        Set<Integer> difference = new HashSet<>(setA);
        difference.removeAll(setB);
        System.out.println("Difference:   " + new TreeSet<>(difference));

        // -------------------------------------------------------
        // CONCEPT 3: Map — key-value pairs, no duplicate keys
        //
        // HashMap:       O(1) average get/put, no ordering
        // LinkedHashMap: O(1), insertion order preserved
        // TreeMap:       O(log n), keys sorted
        // -------------------------------------------------------
        System.out.println("\n--- Map: HashMap, LinkedHashMap, TreeMap ---");

        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 92);
        scores.put("Bob", 87);
        scores.put("Charlie", 95);
        scores.put("Alice", 98);  // overwrites previous value for "Alice"

        System.out.println("HashMap: " + scores);
        System.out.println("  get Alice:  " + scores.get("Alice"));        // 98
        System.out.println("  getOrDefault Dave: " + scores.getOrDefault("Dave", 0)); // 0
        System.out.println("  containsKey Bob: " + scores.containsKey("Bob"));

        // Iterating a Map — always use entrySet() for both key and value
        System.out.println("  Iterating entrySet:");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println("    " + entry.getKey() + " -> " + entry.getValue());
        }

        // TreeMap — sorted by key
        Map<String, Integer> sortedScores = new TreeMap<>(scores);
        System.out.println("TreeMap (sorted): " + sortedScores);

        // Map.of() — immutable map (Java 9+)
        Map<String, String> capitals = Map.of(
            "France", "Paris",
            "Germany", "Berlin",
            "Japan", "Tokyo"
        );
        System.out.println("Immutable map: " + new TreeMap<>(capitals));

        // computeIfAbsent — powerful for building maps of lists
        Map<String, List<String>> groupedByInitial = new HashMap<>();
        String[] names = {"Alice", "Bob", "Anna", "Charlie", "Beth"};
        for (String name : names) {
            String key = String.valueOf(name.charAt(0));
            groupedByInitial.computeIfAbsent(key, k -> new ArrayList<>()).add(name);
        }
        System.out.println("Grouped by initial: " + new TreeMap<>(groupedByInitial));

        // -------------------------------------------------------
        // CONCEPT 4: Queue and Deque
        //
        // Queue: FIFO (First In, First Out) — like a line of people
        // Deque: Double-ended queue — can add/remove from both ends (stack + queue)
        // -------------------------------------------------------
        System.out.println("\n--- Queue and Deque ---");

        Queue<String> queue = new LinkedList<>();
        queue.offer("first");  // prefer offer() over add() — returns false on failure vs throws
        queue.offer("second");
        queue.offer("third");
        System.out.println("Queue: " + queue);
        System.out.println("  peek (look without remove): " + queue.peek()); // "first"
        System.out.println("  poll (remove and return):   " + queue.poll()); // "first"
        System.out.println("After poll: " + queue);

        // PriorityQueue — elements processed in natural order (or custom Comparator)
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(30); pq.offer(10); pq.offer(20);
        System.out.print("PriorityQueue poll order: ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " "); // 10, 20, 30
        System.out.println();

        // ArrayDeque as a Stack (LIFO) — preferred over java.util.Stack
        Deque<String> stack = new ArrayDeque<>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");
        System.out.print("Stack pop order: ");
        while (!stack.isEmpty()) System.out.print(stack.pop() + " "); // top, middle, bottom
        System.out.println();

        // -------------------------------------------------------
        // CONCEPT 5: Sorting with Comparable and Comparator
        // -------------------------------------------------------
        System.out.println("\n--- Sorting: Comparable vs Comparator ---");

        List<Student> students = Arrays.asList(
            new Student("Alice",   3.8, 22),
            new Student("Bob",     3.5, 20),
            new Student("Charlie", 3.9, 25),
            new Student("Dana",    3.5, 21)
        );

        // Natural order (Comparable.compareTo) — GPA descending
        Collections.sort(students);
        System.out.println("Natural order (GPA desc):");
        students.forEach(s -> System.out.println("  " + s));

        // Custom Comparator — sort by name alphabetically
        students.sort(Comparator.comparing(s -> s.name));
        System.out.println("By name:");
        students.forEach(s -> System.out.println("  " + s));

        // Chained Comparator — by GPA desc, then by name
        students.sort(Comparator.<Student, Double>comparing(s -> s.gpa)
            .reversed()
            .thenComparing(s -> s.name));
        System.out.println("By GPA desc, then name:");
        students.forEach(s -> System.out.println("  " + s));

        // -------------------------------------------------------
        // CONCEPT 6: Generics basics
        // -------------------------------------------------------
        System.out.println("\n--- Generics ---");

        // Generic method
        System.out.println("Max of 3, 7, 1: " + findMax(Arrays.asList(3, 7, 1)));
        System.out.println("Max of apple, banana, cherry: " + findMax(Arrays.asList("apple", "banana", "cherry")));

        // Wildcard ? — accept any type
        List<Integer> ints = Arrays.asList(1, 2, 3);
        List<Double> doubles = Arrays.asList(1.5, 2.5, 3.5);
        System.out.println("Sum of ints:    " + sumNumbers(ints));
        System.out.println("Sum of doubles: " + sumNumbers(doubles));

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Implement a word frequency counter:");
        System.out.println("  - Input: \"the quick brown fox jumps over the lazy dog the fox\"");
        System.out.println("  - Use a Map<String, Integer> to count occurrences of each word");
        System.out.println("  - Print words sorted by frequency (descending), then alphabetically");
        System.out.println("  - Expected top result: the=3, fox=2, ...");
        System.out.println("  Hint: Use Map.getOrDefault() or computeIfAbsent()");
        System.out.println("  Bonus: Use a TreeMap<Integer, List<String>> to group words by count");

        // TODO: EXERCISE — implement word frequency counter here
    }

    // Generic method: finds max in any Comparable list
    static <T extends Comparable<T>> T findMax(List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("Empty list");
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) max = item;
        }
        return max;
    }

    // Bounded wildcard: accepts List<Integer>, List<Double>, List<Long>, etc.
    static double sumNumbers(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) sum += n.doubleValue();
        return sum;
    }
}
