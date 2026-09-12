/**
 * CONCEPT: Abstract Classes — partial implementation + enforcement
 *
 * An abstract class is a template: it provides shared code but forces
 * subclasses to fill in the missing pieces.
 *
 * Template Method Pattern: defines the algorithm skeleton in the base class,
 * lets subclasses override specific steps without changing the structure.
 *
 * Why this matters:
 *   - Abstract classes enforce contracts at compile time — can't instantiate them
 *   - Template Method is one of the most commonly used design patterns in Java
 *   - Knowing when abstract class > interface (need state? need constructors?) is a key signal
 */
abstract class AbstractClasses {

    // Template Method: defines the workflow, subclasses fill in steps
    static abstract class DataProcessor {
        private String sourceName;

        DataProcessor(String sourceName) { this.sourceName = sourceName; }

        // Template method — final so nobody can change the order of steps
        final void process() {
            System.out.println("=== Processing from: " + sourceName + " ===");
            String raw      = readData();
            String validated = validate(raw);
            String result   = transform(validated);
            save(result);
            System.out.println("=== Done ===");
        }

        // Steps subclasses MUST implement
        protected abstract String readData();
        protected abstract String transform(String data);

        // Steps with DEFAULT implementations (subclasses can override)
        protected String validate(String data) {
            if (data == null || data.isBlank())
                throw new IllegalStateException("Data is empty from " + sourceName);
            System.out.println("  [validate] OK — " + data.length() + " chars");
            return data.trim();
        }

        protected void save(String result) {
            System.out.println("  [save] Stored: " + result.substring(0, Math.min(50, result.length())));
        }
    }

    static class CsvProcessor extends DataProcessor {
        CsvProcessor() { super("CSV File"); }

        @Override protected String readData() {
            System.out.println("  [read] Reading CSV...");
            return "  Alice,30,Engineer  \n  Bob,25,Designer  ";
        }
        @Override protected String transform(String data) {
            System.out.println("  [transform] Parsing CSV rows...");
            String[] rows = data.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String row : rows) {
                String[] cols = row.trim().split(",");
                sb.append("Name:").append(cols[0].trim())
                  .append(" Age:").append(cols[1].trim()).append(" | ");
            }
            return sb.toString();
        }
    }

    static class JsonProcessor extends DataProcessor {
        JsonProcessor() { super("JSON API"); }

        @Override protected String readData() {
            System.out.println("  [read] Fetching JSON...");
            return "{\"users\":[{\"name\":\"Carol\"},{\"name\":\"Dave\"}]}";
        }
        @Override protected String transform(String data) {
            System.out.println("  [transform] Extracting names...");
            // simplified — real code would use a JSON library
            return data.replaceAll("[{}\"]", "").replaceAll("users:", "Users: ");
        }
        // Override save for JSON-specific behavior
        @Override protected void save(String result) {
            System.out.println("  [save-json] Uploading to data warehouse: " + result);
        }
    }

    public static void main(String[] args) {
        // DataProcessor dp = new DataProcessor("x"); // COMPILE ERROR — can't instantiate abstract

        DataProcessor[] pipeline = { new CsvProcessor(), new JsonProcessor() };
        for (DataProcessor p : pipeline) {
            p.process(); // same call, completely different behavior — polymorphism
            System.out.println();
        }

        // TRY THIS:
        // 1. Add a DatabaseProcessor that reads from a "fake DB" string.
        //    Override validate() to also check for SQL injection patterns.
        // 2. Make process() NOT final and override it in a subclass. What risks emerge?
        // 3. Add timing to the template method: record start time, end time, print duration.
        //    Subclasses get timing for free — the power of the template method.
    }
}
