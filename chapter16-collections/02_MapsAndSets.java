/**
 * CONCEPT: HashMap, LinkedHashMap, TreeMap, HashSet, TreeSet
 *
 * Maps and Sets are interview staples — most coding problems use them.
 *
 * HashMap:       O(1) average get/put; unordered; allows null key
 * LinkedHashMap: O(1) get/put; insertion order or access order (for LRU cache!)
 * TreeMap:       O(log n) get/put; sorted by key; no null keys
 * HashSet:       O(1) contains; unordered; backed by HashMap
 * TreeSet:       O(log n) contains; sorted; backed by TreeMap
 *
 * Collision: when two keys hash to the same bucket.
 *   Java 8+: bucket degrades to a red-black tree when chain length > 8 (O(log n) worst case)
 *   Before Java 8: linked list chain (O(n) worst case)
 *
 * Why this matters:
 *   - Most sliding window / frequency counting problems need a Map or Set
 *   - LinkedHashMap with accessOrder=true is the standard LRU cache backbone
 */
import java.util.*;

class MapsAndSets {

    public static void main(String[] args) {

        // --- HashMap: frequency counting pattern ---
        System.out.println("=== HashMap: frequency count ===");
        String sentence = "the quick brown fox jumps over the lazy dog the fox";
        Map<String, Integer> freq = new HashMap<>();
        for (String word : sentence.split(" ")) {
            freq.merge(word, 1, Integer::sum); // merge: insert 1 or add 1 to existing
        }
        System.out.println("Frequencies: " + freq);
        freq.forEach((word, count) -> {
            if (count > 1) System.out.println("  \"" + word + "\" appears " + count + " times");
        });

        // getOrDefault — safe lookup without null check
        System.out.println("Count 'fox': " + freq.getOrDefault("fox", 0));
        System.out.println("Count 'xyz': " + freq.getOrDefault("xyz", 0));

        // computeIfAbsent — group values
        System.out.println("\n=== computeIfAbsent: grouping ===");
        String[] words = {"apple", "ant", "banana", "avocado", "blueberry", "apricot"};
        Map<Character, List<String>> byLetter = new HashMap<>();
        for (String w : words) {
            byLetter.computeIfAbsent(w.charAt(0), k -> new ArrayList<>()).add(w);
        }
        byLetter.forEach((letter, list) ->
            System.out.println("  " + letter + ": " + list));

        // --- LinkedHashMap: insertion order, access order for LRU ---
        System.out.println("\n=== LinkedHashMap ===");
        Map<String, Integer> ordered = new LinkedHashMap<>();
        ordered.put("c", 3); ordered.put("a", 1); ordered.put("b", 2);
        System.out.println("Insertion order: " + ordered); // {c=3, a=1, b=2}

        // accessOrder=true: recently accessed moves to end — LRU cache backbone
        Map<String, String> lruLike = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > 3; // cap at 3 entries
            }
        };
        lruLike.put("A", "1"); lruLike.put("B", "2"); lruLike.put("C", "3");
        lruLike.get("A"); // access A — moves to end
        lruLike.put("D", "4"); // capacity exceeded — removes eldest (B)
        System.out.println("LRU-like map: " + lruLike); // {C, A, D}

        // --- TreeMap: sorted, range queries ---
        System.out.println("\n=== TreeMap: sorted + range queries ===");
        TreeMap<Integer, String> events = new TreeMap<>();
        events.put(1000, "Server start");
        events.put(1050, "Request 1");
        events.put(1200, "Request 2");
        events.put(1500, "Shutdown");

        System.out.println("First event: " + events.firstEntry());
        System.out.println("Events after 1100: " + events.tailMap(1100));
        System.out.println("Closest key to 1100: " + events.floorKey(1100)); // 1050
        System.out.println("Ceiling key of 1100: " + events.ceilingKey(1100)); // 1200

        // --- HashSet: O(1) membership + deduplication ---
        System.out.println("\n=== HashSet ===");
        Set<String> visited = new HashSet<>();
        String[] urls = {"/home", "/about", "/home", "/contact", "/about"};
        for (String url : urls) {
            if (!visited.add(url)) { // add returns false if already present
                System.out.println("Duplicate visit: " + url);
            }
        }
        System.out.println("Unique pages: " + visited);

        // Set intersection, union, difference
        Set<Integer> a = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> b = new HashSet<>(Arrays.asList(3, 4, 5, 6, 7));
        Set<Integer> intersection = new HashSet<>(a); intersection.retainAll(b);
        Set<Integer> union        = new HashSet<>(a); union.addAll(b);
        Set<Integer> difference   = new HashSet<>(a); difference.removeAll(b);
        System.out.println("A ∩ B = " + intersection);
        System.out.println("A ∪ B = " + union);
        System.out.println("A − B = " + difference);

        // TRY THIS:
        // 1. Implement "Two Sum" (LeetCode #1) using a HashMap:
        //    For each nums[i], check if (target - nums[i]) is already in the map.
        //    O(n) time, O(n) space.
        // 2. Use TreeMap to implement a "time-based key-value store" (LeetCode #981):
        //    set(key, value, timestamp) and get(key, timestamp) → latest value ≤ timestamp.
        // 3. Use LinkedHashMap (accessOrder=true) to implement a proper LRU cache.
        //    Capacity 3, override removeEldestEntry. Test: put 1,2,3; get 1; put 4 → evicts 2.
    }
}
