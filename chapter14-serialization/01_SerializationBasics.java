/**
 * CONCEPT: Java Serialization and NIO File I/O
 *
 * Serialization: convert an object graph to bytes (and back).
 * Marked with implements Serializable (marker interface — no methods).
 *
 * Pitfalls:
 *   - transient fields are NOT serialized (passwords, computed caches)
 *   - serialVersionUID mismatch → InvalidClassException on deserialization
 *   - Java serialization is not security-safe — RCE via deserialization is a known attack
 *     Production code uses JSON (Jackson/Gson) or Protobuf instead
 *
 * NIO.2 (java.nio.file): modern file I/O, introduced Java 7
 *   Files.readString/writeString    — simple text I/O (Java 11+)
 *   Files.readAllBytes/write        — binary I/O
 *   Files.lines(path).stream()...  — lazy line streaming
 *   Files.walk(path)               — recursive directory traversal (stream)
 *   Path/Paths                     — immutable path representation
 *
 * Why this matters:
 *   - File I/O is in every backend service
 *   - NIO.2 is the modern replacement for java.io.File
 *   - Serialization pitfalls are common interview topics
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

class SerializationBasics {

    // Serializable class with transient field
    static class User implements Serializable {
        private static final long serialVersionUID = 1L; // always declare this!

        private String name;
        private int    age;
        private transient String password; // NOT serialized

        User(String name, int age, String password) {
            this.name = name; this.age = age; this.password = password;
        }

        @Override public String toString() {
            return "User{name='" + name + "', age=" + age + ", password=" + password + "}";
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // --- Java Serialization ---
        System.out.println("=== Java Serialization ===");
        User original = new User("Alice", 30, "secret123");
        System.out.println("Before: " + original);

        // Serialize to byte array (in-memory for demo; normally use FileOutputStream)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }
        byte[] bytes = baos.toByteArray();
        System.out.println("Serialized to " + bytes.length + " bytes");

        // Deserialize
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            User restored = (User) ois.readObject();
            System.out.println("After:  " + restored); // password is null — transient!
        }

        // --- NIO.2 File I/O ---
        System.out.println("\n=== NIO.2 File I/O ===");
        Path tempDir = Files.createTempDirectory("java_examples_");
        Path textFile = tempDir.resolve("sample.txt");

        // Write text file
        List<String> logLines = Arrays.asList(
            "2026-09-01 INFO Server started",
            "2026-09-01 WARN High memory usage",
            "2026-09-01 ERROR Database timeout",
            "2026-09-02 INFO Server restarted",
            "2026-09-02 INFO Cache warmed up"
        );
        Files.write(textFile, logLines); // writes with system line separator
        System.out.println("Written " + Files.size(textFile) + " bytes to " + textFile.getFileName());

        // Read all at once
        String content = Files.readString(textFile);
        System.out.println("Read back: " + content.length() + " chars");

        // Stream lines — lazy, no full file loaded to memory
        System.out.println("\nERROR lines:");
        try (Stream<String> lines = Files.lines(textFile)) {
            lines.filter(l -> l.contains("ERROR"))
                 .forEach(l -> System.out.println("  " + l));
        }

        // Files.walk: recursive traversal
        System.out.println("\nFiles under temp dir:");
        try (Stream<Path> walk = Files.walk(tempDir)) {
            walk.filter(Files::isRegularFile)
                .forEach(p -> System.out.println("  " + p.getFileName()));
        }

        // Copy, move, delete
        Path copy = tempDir.resolve("sample_copy.txt");
        Files.copy(textFile, copy, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied: " + Files.exists(copy));
        Files.delete(copy);
        System.out.println("Deleted copy: " + !Files.exists(copy));

        // Cleanup
        Files.delete(textFile);
        Files.delete(tempDir);

        // TRY THIS:
        // 1. Add a List<String> field to User and serialize/deserialize it.
        //    What happens to element order?
        // 2. Remove serialVersionUID and change User.age field name, then
        //    try to deserialize an old byte array. What exception do you get?
        // 3. Use Files.lines() + streams to parse the log file above and
        //    count log levels (INFO, WARN, ERROR) using Collectors.groupingBy.
    }
}
