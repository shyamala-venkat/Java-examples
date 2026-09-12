/**
 * CONCEPT: Encapsulation — private fields, controlled access
 *
 * Encapsulation = hiding internal state and exposing only a safe interface.
 * It's not just about getters/setters — it's about invariant protection.
 *
 * Why this matters:
 *   - Fields without validation allow objects into invalid states
 *   - Once you expose a field as public, removing it breaks all callers
 *   - Good encapsulation makes refactoring safe: internals can change freely
 *   - Interview signal: "tell me about a time a design decision caused a bug"
 *     — often the answer traces back to missing encapsulation
 */
class Encapsulation {

    // --- BAD: public fields allow any caller to put the object in invalid state ---
    static class BadAccount {
        public double balance;  // anyone can set this to -1_000_000
        public String owner;
    }

    // --- GOOD: private fields, validation in setters, invariants enforced ---
    static class BankAccount {
        private final String accountId;  // immutable after construction
        private final String owner;      // immutable
        private double balance;          // mutable but controlled

        private static final double MAX_WITHDRAWAL = 10_000.0;

        BankAccount(String accountId, String owner, double initialBalance) {
            if (accountId == null || accountId.isBlank())
                throw new IllegalArgumentException("accountId required");
            if (owner == null || owner.isBlank())
                throw new IllegalArgumentException("owner required");
            if (initialBalance < 0)
                throw new IllegalArgumentException("initial balance cannot be negative");

            this.accountId = accountId;
            this.owner     = owner;
            this.balance   = initialBalance;
        }

        void deposit(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
            balance += amount;
        }

        void withdraw(double amount) {
            if (amount <= 0)
                throw new IllegalArgumentException("Withdrawal must be positive");
            if (amount > MAX_WITHDRAWAL)
                throw new IllegalArgumentException("Exceeds single withdrawal limit: " + MAX_WITHDRAWAL);
            if (amount > balance)
                throw new IllegalStateException("Insufficient funds");
            balance -= amount;
        }

        // Read-only access — callers can read but not set
        double  getBalance()   { return balance; }
        String  getAccountId() { return accountId; }
        String  getOwner()     { return owner; }

        @Override
        public String toString() {
            return "[" + accountId + "] " + owner + ": $" + String.format("%.2f", balance);
        }
    }

    public static void main(String[] args) {

        // --- BAD: no protection ---
        System.out.println("--- Bad Design ---");
        BadAccount bad = new BadAccount();
        bad.balance = -999_999;   // silent corruption — no validation possible
        bad.owner   = null;       // NPE waiting to happen downstream
        System.out.println("Bad balance: " + bad.balance);

        // --- GOOD: invariants enforced ---
        System.out.println("\n--- Good Design ---");
        BankAccount acc = new BankAccount("ACC-001", "Alice", 1000.0);
        System.out.println(acc);

        acc.deposit(500.0);
        acc.withdraw(200.0);
        System.out.println("After transactions: " + acc);

        // Try invalid operations — all rejected cleanly
        try { acc.withdraw(15_000); } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        try { acc.withdraw(5_000); } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        try { acc.deposit(-50); } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        // final fields cannot be changed
        // acc.accountId = "NEW-ID"; // COMPILE ERROR

        System.out.println("\nFinal state: " + acc);

        // TRY THIS:
        // 1. Add a transfer(BankAccount target, double amount) method.
        //    Should it be atomic? What if withdraw succeeds but deposit throws?
        // 2. Add a transactionHistory: List<String> field that records every
        //    deposit and withdrawal. Expose it as an UNMODIFIABLE list.
        //    Hint: return Collections.unmodifiableList(transactionHistory)
        // 3. What's wrong with a getter that returns a mutable object like a List?
        //    Demonstrate by returning the raw list and having a caller modify it.
    }
}
