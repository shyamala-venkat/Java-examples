/**
 * EXERCISE — Chapter 14: Saving Objects
 *
 * Problem: CSV log parser — read, analyze, write summary
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
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Exercise {

    record LogEntry(String timestamp, String level, String service, String message) {
        static LogEntry parse(String csvLine) {
            // TODO: split by comma (max 4 parts), return LogEntry
            // Handle message which may contain commas: use split(",", 4)
            String[] parts = csvLine.split(",", 4);
            return new LogEntry(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts.length > 3 ? parts[3].trim() : "");
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

    public static void main(String[] args) throws IOException {
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
    }
}
