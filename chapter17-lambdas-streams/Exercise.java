/**
 * EXERCISE — Chapter 17: Lambda and Streams
 *
 * Problem: Word Frequency Analytics Pipeline
 * (Real-world ETL pattern, common in data engineering interviews)
 * -------------------------------------------------------
 *
 * Given a list of log lines (strings), build a stream pipeline that:
 *
 * 1. PARSE: split each line by whitespace, flatten to individual words
 * 2. CLEAN: lowercase, remove non-alphabetic characters, filter blank/short words
 * 3. STOP WORDS: filter out common words: {"the","a","an","is","it","in","on","at","to"}
 * 4. FREQUENCY: count occurrences of each word
 * 5. TOP-N: return the N most frequent words as a List<Map.Entry<String,Long>>
 *
 * Requirements:
 *   - One stream pipeline from source to List (no intermediate variables except the result)
 *   - Use: flatMap, map, filter, collect(Collectors.groupingBy or toMap), sorted, limit
 *   - Method: List<Map.Entry<String,Long>> topWords(List<String> lines, int n)
 *
 * Bonus 1: Words with same frequency should be sorted alphabetically (secondary sort).
 * Bonus 2: Write topWordsByDept(List<LogEntry> logs, int n) where LogEntry has dept and message.
 *          Return Map<String, List<Map.Entry<String,Long>>> — top words per department.
 * Bonus 3: Do it with a parallel stream. What's different? Are results deterministic?
 *
 * Test data provided — expected output shown in comments.
 */
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class Exercise {

    static final Set<String> STOP_WORDS = Set.of(
        "the","a","an","is","it","in","on","at","to","and","of","for","with","be","as","by"
    );

    record LogEntry(String dept, String message) {}

    /**
     * TODO: implement this method using a single stream pipeline.
     *
     * Steps:
     *   1. stream() the lines
     *   2. flatMap(line -> Arrays.stream(line.split("\\s+")))
     *   3. map(word -> word.toLowerCase().replaceAll("[^a-z]",""))
     *   4. filter(word -> word.length() > 2 && !STOP_WORDS.contains(word))
     *   5. collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
     *   6. entrySet().stream()
     *   7. sorted(by count desc, then alpha asc)
     *   8. limit(n)
     *   9. collect(Collectors.toList())
     */
    static List<Map.Entry<String, Long>> topWords(List<String> lines, int n) {
        // TODO: implement
        return Collections.emptyList();
    }

    /**
     * Bonus: top words per department.
     * Hint: Collectors.groupingBy(LogEntry::dept, downstream)
     * The downstream collector should produce List<Map.Entry<String,Long>>
     * — use Collectors.collectingAndThen(groupingBy+counting, map -> topN from map)
     */
    static Map<String, List<Map.Entry<String, Long>>> topWordsByDept(List<LogEntry> logs, int n) {
        // TODO: implement
        return Collections.emptyMap();
    }

    public static void main(String[] args) {

        List<String> lines = Arrays.asList(
            "ERROR database connection failed after timeout",
            "WARN slow query detected in database module timeout exceeded",
            "INFO user login successful authentication complete",
            "ERROR database timeout retry logic triggered connection reset",
            "INFO cache miss for user data loading from database",
            "WARN authentication failed invalid token user blocked",
            "INFO query optimization improved performance database queries",
            "ERROR connection pool exhausted all connections busy database",
            "INFO user session expired token refresh required authentication",
            "WARN slow response from external service timeout threshold"
        );

        System.out.println("=== Top 10 words ===");
        List<Map.Entry<String, Long>> top10 = topWords(lines, 10);
        top10.forEach(e -> System.out.printf("  %-20s %d%n", e.getKey(), e.getValue()));

        // Expected (approximate — stop words filtered):
        // database     6+
        // timeout      3+
        // connection   3+
        // user         3+
        // authentication 2+
        // ...

        System.out.println("\n=== Verify top word ===");
        if (!top10.isEmpty()) {
            System.out.println("Most frequent: " + top10.get(0).getKey()
                + " (" + top10.get(0).getValue() + " times)");
        }

        // --- Bonus: top words by department ---
        System.out.println("\n=== Top words by dept ===");
        List<LogEntry> logs = Arrays.asList(
            new LogEntry("backend",  "database connection timeout retry"),
            new LogEntry("backend",  "database query slow performance"),
            new LogEntry("frontend", "user click event handler triggered"),
            new LogEntry("frontend", "user session expired reload required"),
            new LogEntry("backend",  "cache invalidated database sync"),
            new LogEntry("frontend", "button click navigation user")
        );

        Map<String, List<Map.Entry<String, Long>>> byDept = topWordsByDept(logs, 3);
        byDept.forEach((dept, words) -> {
            System.out.println(dept + ":");
            words.forEach(e -> System.out.printf("  %-15s %d%n", e.getKey(), e.getValue()));
        });
    }
}
