/**
 * Chapter 11: Exception Handling (Risky Behavior)
 *
 * Key Concepts:
 *  - What is an exception and why it exists
 *  - try-catch-finally blocks
 *  - Checked vs Unchecked exceptions
 *  - The exception hierarchy (Throwable -> Exception -> RuntimeException)
 *  - throws declaration on method signature
 *  - Custom exceptions
 *  - try-with-resources (Java 7+)
 *  - Multi-catch (Java 7+)
 *  - Best practices: don't swallow exceptions, don't catch Exception broadly
 *
 * How to run:
 *   javac Chapter11Main.java
 *   java Chapter11Main
 */
public class Chapter11Main {

    // -------------------------------------------------------
    // CONCEPT 1: Custom Checked Exception
    //
    // Extend Exception (not RuntimeException) for CHECKED exceptions.
    // Callers MUST handle or declare them with `throws`.
    // Use for recoverable errors the caller should expect.
    // -------------------------------------------------------
    static class InsufficientFundsException extends Exception {
        private double amount; // extra context — how much was missing?

        public InsufficientFundsException(double amount) {
            super("Insufficient funds. Need $" + String.format("%.2f", amount) + " more.");
            this.amount = amount;
        }

        public double getAmountNeeded() { return amount; }
    }

    // -------------------------------------------------------
    // CONCEPT 2: Custom Unchecked Exception
    //
    // Extend RuntimeException for UNCHECKED exceptions.
    // Callers do NOT need to handle or declare them.
    // Use for programmer errors (invalid input, illegal state).
    // -------------------------------------------------------
    static class InvalidAccountException extends RuntimeException {
        public InvalidAccountException(String message) {
            super(message);
        }
    }

    // -------------------------------------------------------
    // BankAccount — demonstrates throwing exceptions
    // -------------------------------------------------------
    static class BankAccount {
        private String owner;
        private double balance;

        public BankAccount(String owner, double initialBalance) {
            if (owner == null || owner.isBlank()) {
                throw new InvalidAccountException("Owner name cannot be empty"); // unchecked — no `throws`
            }
            if (initialBalance < 0) {
                throw new InvalidAccountException("Initial balance cannot be negative");
            }
            this.owner = owner;
            this.balance = initialBalance;
        }

        // CHECKED exception — caller must handle or declare `throws`
        public void withdraw(double amount) throws InsufficientFundsException {
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive"); // unchecked
            }
            if (amount > balance) {
                throw new InsufficientFundsException(amount - balance); // checked
            }
            balance -= amount;
            System.out.println(owner + " withdrew $" + amount + ". Balance: $" + balance);
        }

        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive");
            }
            balance += amount;
            System.out.println(owner + " deposited $" + amount + ". Balance: $" + balance);
        }

        public double getBalance() { return balance; }
        public String getOwner()   { return owner; }
    }

    // -------------------------------------------------------
    // CONCEPT 3: throws on method signature
    //
    // A method that throws a checked exception must declare it.
    // It either handles it internally or propagates it to the caller.
    // -------------------------------------------------------
    static void performTransaction(BankAccount account, double amount) throws InsufficientFundsException {
        System.out.println("Attempting to withdraw $" + amount + " from " + account.getOwner() + "...");
        account.withdraw(amount); // may throw InsufficientFundsException — we propagate it
    }

    // -------------------------------------------------------
    // Simulating checked I/O-like exception for try-with-resources demo
    // -------------------------------------------------------
    static class FakeFileReader implements AutoCloseable {
        private String filename;
        private boolean closed = false;

        public FakeFileReader(String filename) {
            this.filename = filename;
            System.out.println("  Opening file: " + filename);
        }

        public String readLine() {
            if (closed) throw new IllegalStateException("File already closed!");
            return "Content of " + filename;
        }

        @Override
        public void close() {
            closed = true;
            System.out.println("  Closing file: " + filename + " (automatically by try-with-resources)");
        }
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 11: Exception Handling ===\n");

        // -------------------------------------------------------
        // CONCEPT 4: try-catch-finally
        //
        // try: code that might throw an exception
        // catch: handles specific exception type
        // finally: ALWAYS runs (cleanup code) — even if exception thrown or return called
        // -------------------------------------------------------
        System.out.println("--- try-catch-finally ---");

        BankAccount account = new BankAccount("Alice", 500.0);

        try {
            account.deposit(200.0);        // ok
            account.withdraw(300.0);       // ok
            account.withdraw(1000.0);      // throws InsufficientFundsException!
            System.out.println("This line never runs!");

        } catch (InsufficientFundsException e) {
            // e contains the exception object — use it!
            System.out.println("CAUGHT: " + e.getMessage());
            System.out.println("Amount needed: $" + e.getAmountNeeded());

        } finally {
            // This ALWAYS executes — even if no exception, even if return statement
            System.out.println("FINALLY: Transaction completed. Final balance: $" + account.getBalance());
        }

        // -------------------------------------------------------
        // CONCEPT 5: Multiple catch blocks
        // Catch from most specific to most general.
        // -------------------------------------------------------
        System.out.println("\n--- Multiple catch blocks ---");
        try {
            String str = null;
            // str.length();                   // NullPointerException
            int[] arr = new int[5];
            arr[10] = 1;                       // ArrayIndexOutOfBoundsException

        } catch (NullPointerException e) {
            System.out.println("Null pointer: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array out of bounds: " + e.getMessage());
        } catch (Exception e) {
            // Catch-all for any other Exception — only as a last resort
            System.out.println("Some other exception: " + e.getMessage());
        }

        // -------------------------------------------------------
        // CONCEPT 6: Multi-catch (Java 7+) — catch multiple types in one block
        // -------------------------------------------------------
        System.out.println("\n--- Multi-catch (Java 7+) ---");
        // Multi-catch works when exception types are NOT related by inheritance.
        // NumberFormatException IS-A IllegalArgumentException, so they can't share a multi-catch.
        // Use unrelated types: e.g., ArithmeticException and ArrayIndexOutOfBoundsException.
        try {
            if (Math.random() < 0.5) {
                throw new ArithmeticException("bad math");
            } else {
                throw new ArrayIndexOutOfBoundsException("bad index");
            }
        } catch (ArithmeticException | ArrayIndexOutOfBoundsException e) {
            // Handles either exception type with one catch block
            System.out.println("Caught either type: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // -------------------------------------------------------
        // CONCEPT 7: Unchecked exception — no `throws` needed
        // -------------------------------------------------------
        System.out.println("\n--- Unchecked Exceptions ---");
        try {
            BankAccount bad = new BankAccount("", 100); // throws InvalidAccountException (unchecked)
        } catch (InvalidAccountException e) {
            System.out.println("Caught unchecked: " + e.getMessage());
        }

        try {
            int result = Integer.parseInt("not-a-number"); // NumberFormatException (unchecked)
        } catch (NumberFormatException e) {
            System.out.println("Cannot parse: " + e.getMessage());
        }

        // -------------------------------------------------------
        // CONCEPT 8: Exception propagation
        // -------------------------------------------------------
        System.out.println("\n--- Exception Propagation ---");
        BankAccount bob = new BankAccount("Bob", 50.0);
        try {
            performTransaction(bob, 100.0); // propagates the exception here
        } catch (InsufficientFundsException e) {
            System.out.println("Transaction failed for Bob: " + e.getMessage());
            System.out.println("Auto-adding funds and retrying...");
            bob.deposit(100.0);
            try {
                performTransaction(bob, 100.0); // succeeds now
            } catch (InsufficientFundsException ex) {
                System.out.println("Still failed: " + ex.getMessage());
            }
        }

        // -------------------------------------------------------
        // CONCEPT 9: try-with-resources (Java 7+)
        //
        // For objects that implement AutoCloseable (like files, DB connections).
        // Automatically calls close() when the try block exits — even on exception.
        // Eliminates the need for finally { resource.close(); }
        // -------------------------------------------------------
        System.out.println("\n--- try-with-resources ---");
        try (FakeFileReader reader = new FakeFileReader("data.txt")) {
            String line = reader.readLine();
            System.out.println("  Read: " + line);
        } // close() called automatically here!
        System.out.println("  (File was auto-closed by try-with-resources)");

        // Multiple resources — closed in reverse order
        try (FakeFileReader r1 = new FakeFileReader("input.txt");
             FakeFileReader r2 = new FakeFileReader("output.txt")) {
            System.out.println("  " + r1.readLine());
            System.out.println("  " + r2.readLine());
        } // r2.close() then r1.close() — reverse order

        // -------------------------------------------------------
        // Exception hierarchy summary
        // -------------------------------------------------------
        System.out.println("\n--- Exception Hierarchy ---");
        System.out.println("Throwable");
        System.out.println("  ├── Error            (JVM problems — don't catch: OutOfMemoryError, StackOverflowError)");
        System.out.println("  └── Exception        (things that can go wrong)");
        System.out.println("       ├── Checked      (must declare or catch: IOException, SQLException)");
        System.out.println("       └── RuntimeException (unchecked: NPE, ArrayIndexOutOfBounds, IllegalArgument)");

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a safe division calculator:");
        System.out.println("  - Custom checked exception: DivisionByZeroException");
        System.out.println("  - Method: double safeDivide(double a, double b) throws DivisionByZeroException");
        System.out.println("  - Method: double safeParseAndDivide(String a, String b)");
        System.out.println("    -> catches NumberFormatException internally, throws DivisionByZeroException");
        System.out.println("  - Test: valid division, division by zero, invalid string input");
        System.out.println("  - Use finally to print 'calculation attempted' every time");

        // TODO: EXERCISE — implement safe division calculator here
    }
}
