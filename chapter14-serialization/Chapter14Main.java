/**
 * Chapter 14: Saving Objects (and Text) — Serialization and File I/O
 *
 * Key Concepts:
 *  - Serialization: converting objects to bytes for storage/transmission
 *  - Serializable interface — marks a class as serializable
 *  - ObjectOutputStream / ObjectInputStream — write/read objects
 *  - transient fields — excluded from serialization
 *  - serialVersionUID — version control for serialized classes
 *  - Text file I/O: FileWriter, BufferedWriter, FileReader, BufferedReader
 *  - Modern file I/O: Files class (Java NIO — java.nio.file)
 *  - Path and Paths
 *  - try-with-resources for safe resource management
 *
 * How to run:
 *   javac Chapter14Main.java
 *   java Chapter14Main
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Chapter14Main {

    // -------------------------------------------------------
    // CONCEPT 1: Serializable interface
    //
    // Implementing Serializable is a marker interface — no methods to implement.
    // It tells Java "this class is OK to serialize (convert to bytes)."
    //
    // ALL instance variables must also be Serializable (primitives are fine).
    // -------------------------------------------------------
    static class GameCharacter implements Serializable {

        // serialVersionUID: Java uses this to verify the class version when deserializing.
        // If you modify the class and the UID doesn't match, you get InvalidClassException.
        // Best practice: always declare it explicitly.
        private static final long serialVersionUID = 1L;

        private String name;
        private int level;
        private int health;
        private List<String> inventory;

        // transient = this field is NOT saved during serialization
        // Use for: passwords, cached data, non-serializable dependencies
        private transient String sessionToken; // excluded from serialization
        private transient long lastLoginTime;  // excluded from serialization

        public GameCharacter(String name, int level, int health) {
            this.name = name;
            this.level = level;
            this.health = health;
            this.inventory = new ArrayList<>();
            this.sessionToken = "tok_" + System.currentTimeMillis();
            this.lastLoginTime = System.currentTimeMillis();
        }

        public void addItem(String item) { inventory.add(item); }

        @Override
        public String toString() {
            return "GameCharacter{name=" + name + ", level=" + level +
                   ", health=" + health + ", inventory=" + inventory +
                   ", sessionToken=" + sessionToken + "}";
        }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Object Serialization (write) and Deserialization (read)
    // -------------------------------------------------------
    static void serializeCharacter(GameCharacter character, String filename) {
        // try-with-resources ensures streams are closed even if exception occurs
        try (ObjectOutputStream oos = new ObjectOutputStream(
                                      new FileOutputStream(filename))) {
            oos.writeObject(character);
            System.out.println("Saved: " + character.name + " to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    static GameCharacter deserializeCharacter(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
                                     new FileInputStream(filename))) {
            GameCharacter character = (GameCharacter) ois.readObject();
            System.out.println("Loaded: " + character.name + " from " + filename);
            return character;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading: " + e.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------
    // CONCEPT 3: Writing text files (old-style IO)
    // -------------------------------------------------------
    static void writeTextFile(String filename, List<String> lines) {
        // BufferedWriter wraps FileWriter for efficient writing (reduces disk I/O)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // cross-platform newline
            }
            System.out.println("Written text file: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // CONCEPT 4: Reading text files (old-style IO)
    // -------------------------------------------------------
    static List<String> readTextFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading: " + e.getMessage());
        }
        return lines;
    }

    // -------------------------------------------------------
    // CONCEPT 5: Modern NIO File I/O (Java 7+ — preferred approach)
    //
    // java.nio.file.Files provides simple, powerful static methods.
    // Much cleaner than the old java.io approach.
    // -------------------------------------------------------
    static void modernFileIO(String filename) throws IOException {
        Path path = Path.of(filename); // or Paths.get(filename)

        // Write all lines at once (simpler than BufferedWriter)
        List<String> content = Arrays.asList(
            "Line 1: Modern Java NIO is much cleaner",
            "Line 2: Files.writeString() or Files.write() are preferred",
            "Line 3: Path objects are more powerful than File objects"
        );
        Files.write(path, content); // creates or overwrites file
        System.out.println("NIO wrote: " + path);

        // Read all lines at once
        List<String> readLines = Files.readAllLines(path);
        System.out.println("NIO read " + readLines.size() + " lines:");
        readLines.forEach(line -> System.out.println("  " + line));

        // Write a single string
        Files.writeString(path, "Replaced with a single string.\n"); // Java 11+
        System.out.println("NIO overwrote with single string.");

        // Read the whole file as a String
        String allContent = Files.readString(path); // Java 11+
        System.out.println("File content: " + allContent.trim());

        // Append to file
        Files.writeString(path, "\nAppended line.", StandardOpenOption.APPEND);

        // File metadata
        System.out.println("File exists: " + Files.exists(path));
        System.out.println("File size:   " + Files.size(path) + " bytes");
    }

    public static void main(String[] args) throws IOException {

        System.out.println("=== Chapter 14: Saving Objects and Text ===\n");

        // Use temp directory to avoid cluttering project
        String tmpDir = System.getProperty("java.io.tmpdir") + File.separator;

        // -------------------------------------------------------
        // Object Serialization Demo
        // -------------------------------------------------------
        System.out.println("--- Object Serialization ---");

        GameCharacter hero = new GameCharacter("Aria", 25, 100);
        hero.addItem("Sword of Light");
        hero.addItem("Health Potion");
        hero.addItem("Magic Shield");

        System.out.println("Before serialization: " + hero);

        String saveFile = tmpDir + "character.ser";
        serializeCharacter(hero, saveFile);

        // Simulate application restart — load from file
        GameCharacter loadedHero = deserializeCharacter(saveFile);
        System.out.println("After deserialization: " + loadedHero);
        System.out.println("NOTE: sessionToken is null (transient field not saved)");

        // -------------------------------------------------------
        // Text File I/O (classic)
        // -------------------------------------------------------
        System.out.println("\n--- Text File I/O (Classic BufferedReader/Writer) ---");

        List<String> diary = Arrays.asList(
            "Day 1: Started learning Java",
            "Day 2: Understood OOP concepts",
            "Day 3: Mastered exceptions",
            "Day 4: Now doing file I/O"
        );

        String textFile = tmpDir + "diary.txt";
        writeTextFile(textFile, diary);

        List<String> readDiary = readTextFile(textFile);
        System.out.println("Read back " + readDiary.size() + " entries:");
        for (int i = 0; i < readDiary.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + readDiary.get(i));
        }

        // -------------------------------------------------------
        // Modern NIO File I/O
        // -------------------------------------------------------
        System.out.println("\n--- Modern NIO (java.nio.file) ---");
        String nioFile = tmpDir + "nio_demo.txt";
        modernFileIO(nioFile);

        // -------------------------------------------------------
        // Working with directories
        // -------------------------------------------------------
        System.out.println("\n--- Directory Operations ---");
        Path tempSubDir = Path.of(tmpDir + "java_demo_dir");
        Files.createDirectories(tempSubDir); // creates all missing parent dirs
        System.out.println("Created dir: " + tempSubDir);

        // List files in a directory
        Path homeDir = Path.of(System.getProperty("java.io.tmpdir"));
        System.out.println("Listing .ser and .txt files in temp dir:");
        try (var stream = Files.list(homeDir)) {
            stream.filter(p -> p.toString().endsWith(".ser") || p.toString().endsWith(".txt"))
                  .limit(5)
                  .forEach(p -> System.out.println("  " + p.getFileName()));
        }

        // Cleanup temp files
        Files.deleteIfExists(Path.of(saveFile));
        Files.deleteIfExists(Path.of(textFile));
        Files.deleteIfExists(Path.of(nioFile));
        Files.deleteIfExists(tempSubDir);
        System.out.println("\nCleaned up temp files.");

        // -------------------------------------------------------
        // Summary: old vs new approach
        // -------------------------------------------------------
        System.out.println("\n--- Old IO vs NIO Summary ---");
        System.out.println("Old (java.io):  FileWriter, BufferedReader — verbose, error-prone");
        System.out.println("New (java.nio): Files.write(), Files.readAllLines() — concise, preferred");
        System.out.println("Use new NIO for most file tasks. Use java.io when working with streams.");

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a simple contact book that saves to disk:");
        System.out.println("  - Class: Contact implements Serializable { name, phone, email }");
        System.out.println("  - Save a List<Contact> to a file using ObjectOutputStream");
        System.out.println("  - Load it back and print all contacts");
        System.out.println("  - Also write the contact list as a CSV text file");
        System.out.println("    format: name,phone,email (one per line)");
        System.out.println("  - Verify by reading the CSV back and parsing with split(',')");

        // TODO: EXERCISE — implement contact book here
    }
}
