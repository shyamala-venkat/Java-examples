/**
 * EXERCISE — Chapter 14: Saving Objects
 *
 * Problem 1: CSV log parser — read, analyze, write summary
 * -------------------------------------------------------
 *
 * Given a CSV log file with format:
 *   timestamp,level,service,message
 *   2026-09-01T10:00:00,INFO,auth-service,User login successful
 *   2026-09-01T10:01:00,ERROR,db-service,Connection timeout
 *   ...
 *
 * Requirements:
 *   1. Read the file using Files.lines() + stream pipeline
 *   2. Parse each line into a LogEntry record
 *   3. Compute:
 *      - total lines (excluding header)
 *      - count per level (INFO, WARN, ERROR)
 *      - count per service
 *      - most frequent error service
 *   4. Write a summary file: summary.txt
 *   5. Write only ERROR lines to a separate errors.csv file
 *
 * Use only NIO.2 (Files.*, Path) — no java.io.File or old FileReader.
 * Use stream pipelines for all analysis — no imperative loops.
 *
 * Problem 2: Hand-Rolled JSON (De)Serialization via Reflection
 * -------------------------------------------------------------------
 * This chapter already flagged that production code uses Jackson/Gson instead
 * of Java's native serialization. This exercise builds a MINIMAL version of
 * what those libraries do under the hood, using reflection — so you understand
 * what your @JsonProperty annotations are actually doing at runtime.
 *
 * 1. static String toJson(Object obj) — use obj.getClass().getDeclaredFields(),
 *    field.setAccessible(true), field.get(obj) to read each field's name and
 *    value, and emit {"field":"value","field2":123,...}
 *      - String values: wrap in quotes (skip real escaping — note it as a
 *        known limitation in a comment, real libraries handle this)
 *      - numeric/boolean values: no quotes
 *      - null: literal null
 *      - nested objects: recurse
 *
 * 2. static <T> T fromJson(String json, Class<T> type) — minimal parser: split
 *    top-level "key":value pairs on commas (a real parser handles nested
 *    commas/braces; that's out of scope here — flat objects only), then use
 *    type.getDeclaredConstructor().newInstance() + declared fields to populate
 *    the instance via reflection.
 *
 * Note: use a plain class (not a record) for the round-trip test — records
 * have no no-arg constructor, so newInstance() can't build one. That's itself
 * a fact worth knowing: it's WHY Jackson needs either a no-arg constructor +
 * setters, or special record support it had to add later.
 *
 * Test: round-trip a simple flat POJO through toJson() then fromJson(); confirm
 * every field on the rehydrated object matches the original.
 *
 * Problem 3 (Bonus): In-Memory Transactional KeyValueStore
 * -------------------------------------------------------------------
 * A real JDBC Connection needs an actual database/driver, which this repo
 * intentionally avoids (single-file, zero dependencies). But you can learn the
 * CONCEPT — commit/rollback boundaries — with an in-memory stand-in.
 *
 * 1. KeyValueStore backed by a Map<String,String> "committed" state.
 * 2. begin() — snapshot committed state into a separate "pending" map; every
 *    put()/remove() after this modifies ONLY the pending map.
 * 3. commit() — pending map becomes the new committed state atomically.
 * 4. rollback() — discard the pending map; committed state is untouched.
 * 5. get(key) — reads from the pending map if a transaction is open, else
 *    from committed state.
 *
 * Test: begin(), put a few keys, get() should see them; rollback(); get()
 * should NOT see them anymore. Repeat with commit() instead of rollback() —
 * get() SHOULD see them afterward, with no transaction open.
 */
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Chapter14Exercise {

    record LogEntry(String timestamp, String level, String service, String message) {
        static LogEntry parse(String csvLine) {
            // TODO: split by comma (max 4 parts), return LogEntry
            // Handle message which may contain commas: use split(",", 4)
            String[] parts = csvLine.split(",", 4);
            return new LogEntry(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts.length > 3 ? parts[3].trim() : "");
        }
    }

    // ========= Problem 2: Hand-Rolled JSON via Reflection =========
    // Plain class (not a record) — needs a no-arg constructor for fromJson().
    static class UserProfile {
        String username;
        int age;
        boolean active;

        UserProfile() {} // required for reflection-based construction in fromJson()
        UserProfile(String username, int age, boolean active) {
            this.username = username; this.age = age; this.active = active;
        }

        @Override public String toString() {
            return "UserProfile{username=" + username + ", age=" + age + ", active=" + active + "}";
        }
    }

    static String toJson(Object obj) throws IllegalAccessException {
        // TODO: use obj.getClass().getDeclaredFields(), field.setAccessible(true),
        //       field.get(obj) to build {"field":"value",...} per the rules above
        return null;
    }

    static <T> T fromJson(String json, Class<T> type) throws Exception {
        // TODO: type.getDeclaredConstructor().newInstance(), then split top-level
        //       "key":value pairs and set each declared field via reflection
        return null;
    }

    // ========= Problem 3 (Bonus): In-Memory Transactional KeyValueStore =========
    static class KeyValueStore {
        private final Map<String, String> committed = new HashMap<>();
        private Map<String, String> pending; // non-null while a transaction is open

        void begin() {
            // TODO: pending = new HashMap<>(committed);
        }

        void put(String key, String value) {
            // TODO: write into pending if a transaction is open, else directly into committed
        }

        void commit() {
            // TODO: committed.clear(); committed.putAll(pending); pending = null;
        }

        void rollback() {
            // TODO: pending = null;
        }

        String get(String key) {
            // TODO: read from pending if a transaction is open, else from committed
            return null;
        }
    }

    static Path generateSampleLog(Path dir) throws IOException {
        Path logFile = dir.resolve("app.log.csv");
        List<String> lines = Arrays.asList(
            "timestamp,level,service,message",
            "2026-09-01T10:00:00,INFO,auth-service,User alice login successful",
            "2026-09-01T10:01:00,ERROR,db-service,Connection timeout after 30s",
            "2026-09-01T10:02:00,WARN,cache-service,Cache miss rate above threshold",
            "2026-09-01T10:03:00,INFO,auth-service,User bob login successful",
            "2026-09-01T10:04:00,ERROR,db-service,Deadlock detected in transaction",
            "2026-09-01T10:05:00,ERROR,auth-service,Invalid token presented",
            "2026-09-01T10:06:00,INFO,cache-service,Cache warmed up successfully",
            "2026-09-01T10:07:00,WARN,db-service,Slow query detected 2300ms",
            "2026-09-01T10:08:00,ERROR,db-service,Max connections reached",
            "2026-09-01T10:09:00,INFO,auth-service,Session refreshed for user carol"
        );
        Files.write(logFile, lines);
        return logFile;
    }

    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("log_exercise_");
        Path logFile = generateSampleLog(workDir);

        System.out.println("=== Log Analysis ===");
        System.out.println("Input: " + logFile);

        // TODO: Parse all non-header lines into LogEntry objects using Files.lines() stream

        // TODO: Compute and print:
        //   - Total entries
        //   - Count by level (use Collectors.groupingBy + counting)
        //   - Count by service
        //   - Service with most ERROR entries

        // TODO: Write summary.txt using Files.writeString() or Files.write()
        // Format:
        //   Total: N
        //   Level counts: {INFO=X, WARN=Y, ERROR=Z}
        //   Service counts: {...}
        //   Most error-prone service: X

        // TODO: Write errors.csv with header + only ERROR lines
        // Use Files.write() with a collected List<String>

        System.out.println("Expected most error-prone service: db-service (3 errors)");

        // Cleanup
        Files.walk(workDir).sorted(Comparator.reverseOrder()).forEach(p -> {
            try { Files.delete(p); } catch (IOException e) { /* ignore */ }
        });
        System.out.println("Done. Temp dir cleaned up.");

        System.out.println("\n=== Problem 2: Hand-Rolled JSON ===");
        UserProfile original = new UserProfile("alice", 30, true);
        String json = toJson(original);
        System.out.println("toJson: " + json);
        UserProfile roundTripped = fromJson(json, UserProfile.class);
        System.out.println("fromJson: " + roundTripped);
        System.out.println("Round-trip matches: " +
            (roundTripped != null
                && original.username.equals(roundTripped.username)
                && original.age == roundTripped.age
                && original.active == roundTripped.active));

        System.out.println("\n=== Problem 3 (Bonus): Transactional KeyValueStore ===");
        KeyValueStore store = new KeyValueStore();
        store.begin();
        store.put("a", "1");
        System.out.println("Inside txn, get(a): " + store.get("a") + " (expected 1)");
        store.rollback();
        System.out.println("After rollback, get(a): " + store.get("a") + " (expected null)");

        store.begin();
        store.put("b", "2");
        store.commit();
        System.out.println("After commit, get(b): " + store.get("b") + " (expected 2)");
    }
}
