/**
 * CONCEPT: Exception Hierarchy and Checked vs Unchecked
 *
 * Throwable
 *   ├── Error          (JVM problems — never catch: OOM, StackOverflow)
 *   └── Exception
 *        ├── Checked   (must declare or catch: IOException, SQLException)
 *        └── RuntimeException (unchecked: NPE, IllegalArgument, IndexOutOfBounds)
 *
 * Why this matters:
 *   - Checked exceptions = recoverable scenarios callers should handle
 *   - Unchecked = programmer errors or unrecoverable runtime failures
 *   - Catching Exception broadly is a code smell — you hide bugs
 *   - Exception chaining (cause) preserves the root cause in production logs
 */
class ExceptionHierarchy {

    // --- Custom checked exception: represents a RECOVERABLE business error ---
    static class PaymentDeclinedException extends Exception {
        private final String declineCode;
        private final double amount;

        PaymentDeclinedException(String declineCode, double amount) {
            super("Payment declined (code=" + declineCode + ") for $" + String.format("%.2f", amount));
            this.declineCode = declineCode;
            this.amount      = amount;
        }

        // Extra context — callers can use this to decide what to do
        String getDeclineCode() { return declineCode; }
        double getAmount()      { return amount; }
    }

    // --- Custom unchecked exception: represents a PROGRAMMER ERROR ---
    static class InvalidConfigurationException extends RuntimeException {
        InvalidConfigurationException(String key, String value) {
            super("Invalid config: " + key + "=" + value);
        }
        InvalidConfigurationException(String message, Throwable cause) {
            super(message, cause); // exception chaining — preserves root cause
        }
    }

    // --- Method that throws checked exception ---
    static void processPayment(double amount, String cardType) throws PaymentDeclinedException {
        if (amount <= 0)          throw new IllegalArgumentException("Amount must be positive");
        if ("EXPIRED".equals(cardType)) throw new PaymentDeclinedException("CARD_EXPIRED", amount);
        if (amount > 10_000)      throw new PaymentDeclinedException("LIMIT_EXCEEDED", amount);
        System.out.println("Payment of $" + amount + " processed successfully.");
    }

    // --- Exception chaining: wrap low-level exceptions in domain exceptions ---
    static void loadConfig(String filename) {
        try {
            // Simulating a low-level I/O or parse error
            if (filename.endsWith(".bad")) {
                throw new java.io.IOException("File not found: " + filename);
            }
            System.out.println("Config loaded: " + filename);
        } catch (java.io.IOException e) {
            // Wrap with context — caller sees the domain error, cause still accessible
            throw new InvalidConfigurationException(
                "Failed to load config from " + filename, e // `e` is the cause
            );
        }
    }

    public static void main(String[] args) {

        // --- Handling checked exceptions ---
        System.out.println("--- Checked Exceptions ---");
        try {
            processPayment(99.99, "VISA");       // success
            processPayment(50.00, "EXPIRED");    // throws PaymentDeclinedException
        } catch (PaymentDeclinedException e) {
            System.out.println("Declined: " + e.getMessage());
            System.out.println("Code: " + e.getDeclineCode());
            if ("CARD_EXPIRED".equals(e.getDeclineCode())) {
                System.out.println("Action: ask customer to update card");
            }
        }

        // --- Multi-catch ---
        System.out.println("\n--- Multi-catch ---");
        try {
            processPayment(-10, "VISA");   // throws IllegalArgumentException (unchecked)
        } catch (PaymentDeclinedException | IllegalArgumentException e) {
            System.out.println("Payment error: " + e.getMessage());
        }

        // --- Exception chaining ---
        System.out.println("\n--- Exception Chaining ---");
        try {
            loadConfig("settings.bad");
        } catch (InvalidConfigurationException e) {
            System.out.println("Domain error: " + e.getMessage());
            System.out.println("Root cause:   " + e.getCause().getMessage());
        }

        // --- finally: always executes ---
        System.out.println("\n--- finally ---");
        try {
            processPayment(20_000, "VISA"); // throws
        } catch (PaymentDeclinedException e) {
            System.out.println("Caught: " + e.getDeclineCode());
        } finally {
            System.out.println("Payment attempt logged (finally always runs)");
        }

        // --- Don't swallow exceptions ---
        System.out.println("\n--- Swallowing exceptions is WRONG ---");
        try {
            processPayment(100, "EXPIRED");
        } catch (PaymentDeclinedException e) {
            // BAD: catch { } or catch { e.printStackTrace() } hides problems in production
            // GOOD: at minimum, log the full exception with a logging framework
            System.out.println("// empty catch is a bug — don't do this");
        }

        // TRY THIS:
        // 1. Create a checked InsufficientFundsException and an unchecked
        //    AccountFrozenException. Write a withdraw() method that throws each appropriately.
        // 2. Catch Exception broadly — then check: does catching Exception also catch Error?
        //    What about catching Throwable?
        // 3. Add a suppressed exception using addSuppressed() — see how it appears in the trace.
    }
}
