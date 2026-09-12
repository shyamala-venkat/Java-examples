/**
 * EXERCISE — Chapter 16: Data Structures
 *
 * Problem: LRU Cache (LeetCode #146)
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
    }
}
