/**
 * EXERCISE — Chapter 6: Using the Java Library
 *
 * Problem: Find All Anagrams in a String (LeetCode #438)
 * -------------------------------------------------------
 * Given strings s and p, return all start indices where p's anagram
 * appears in s. An anagram has the same characters, just reordered.
 *
 * Examples:
 *   s = "cbaebabacd", p = "abc"  → [0, 6]
 *   s = "abab",       p = "ab"   → [0, 1, 2]
 *
 * Approach: Sliding window + frequency array
 *   - Keep a window of size p.length() sliding across s
 *   - Track character frequencies: one array for p, one for the window
 *   - Use Arrays.equals to compare in O(26) = O(1)
 *   - Slide the window: add right char, remove left char
 *   - Total time: O(n), space O(1) — because only 26 lowercase letters
 *
 * What this tests:
 *   - char arithmetic (ch - 'a' to index into 26-element array)
 *   - Sliding window pattern (appears in dozens of interview problems)
 *   - Arrays utility class
 *   - List<Integer> as return type
 *
 * Bonus: Group Anagrams (LeetCode #49)
 *   Given ["eat","tea","tan","ate","nat","bat"]
 *   → [["bat"],["nat","tan"],["ate","eat","tea"]]
 *   Hint: sort each word as a key in a Map<String, List<String>>
 */
import java.util.*;

public class Chapter06Exercise {

    static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;

        // TODO: implement sliding window
        // Hint:
        //   int[] pCount = new int[26];   // frequency of p
        //   int[] wCount = new int[26];   // frequency of current window
        //   Fill pCount by iterating p
        //   Fill wCount with the first window (first p.length() chars of s)
        //   If Arrays.equals(pCount, wCount) -> add 0 to result
        //   Slide: for i from p.length() to s.length()-1:
        //     add s.charAt(i) - 'a' to wCount
        //     remove s.charAt(i - p.length()) - 'a' from wCount
        //     check equality again

        return result;
    }

    static Map<String, List<String>> groupAnagrams(String[] words) {
        Map<String, List<String>> groups = new HashMap<>();
        // TODO: for each word, sort its chars as a key, group into map
        return groups;
    }

    public static void main(String[] args) {

        System.out.println("=== Find All Anagrams ===");
        System.out.println(findAnagrams("cbaebabacd", "abc") + " (expected [0, 6])");
        System.out.println(findAnagrams("abab", "ab")        + " (expected [0, 1, 2])");
        System.out.println(findAnagrams("aa", "bb")          + " (expected [])");
        System.out.println(findAnagrams("baa", "aa")         + " (expected [1])");

        System.out.println("\n=== Group Anagrams (Bonus) ===");
        Map<String, List<String>> groups = groupAnagrams(
            new String[]{"eat","tea","tan","ate","nat","bat"}
        );
        // Print sorted for deterministic output
        groups.values().forEach(g -> { Collections.sort(g); System.out.println(g); });
        // Expected groups: [ate, eat, tea], [bat], [nat, tan]
    }
}
