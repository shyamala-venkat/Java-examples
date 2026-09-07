/**
 * CONCEPT: Inheritance — extends, method overriding, IS-A
 *
 * Inheritance models an IS-A relationship. Use it when a subclass truly IS
 * a specialized kind of the parent, not just when you want to reuse code.
 * Misusing inheritance leads to brittle hierarchies (the Fragile Base Class problem).
 *
 * Why this matters:
 *   - Overriding vs overloading: compile-time vs runtime dispatch
 *   - The @Override annotation makes the compiler catch typos
 *   - Liskov Substitution Principle: a subclass must be usable wherever the parent is
 *   - Interview question: "when would you prefer composition over inheritance?"
 */
class InheritanceChain {

    // --- Abstract base: Employee hierarchy ---
    static abstract class Employee {
        private final String id;
        private final String name;
        protected double baseSalary;

        Employee(String id, String name, double baseSalary) {
            this.id         = id;
            this.name       = name;
            this.baseSalary = baseSalary;
        }

        String getId()   { return id; }
        String getName() { return name; }

        // Template Method pattern: subclasses customize bonus calculation
        abstract double calculateBonus();

        double totalCompensation() { return baseSalary + calculateBonus(); }

        // Can be overridden but has a sensible default
        String role() { return "Employee"; }

        @Override public String toString() {
            return String.format("%-10s %-12s %-12s base=$%-8.0f bonus=$%-8.0f total=$%.0f",
                role(), id, name, baseSalary, calculateBonus(), totalCompensation());
        }
    }

    static class Engineer extends Employee {
        private int performanceRating; // 1-5

        Engineer(String id, String name, double baseSalary, int rating) {
            super(id, name, baseSalary);
            this.performanceRating = rating;
        }

        @Override public double calculateBonus() {
            return baseSalary * (performanceRating * 0.05); // 5-25% of base
        }
        @Override public String role() { return "Engineer"; }
    }

    static class Manager extends Engineer {
        private int teamSize;

        Manager(String id, String name, double baseSalary, int rating, int teamSize) {
            super(id, name, baseSalary, rating);
            this.teamSize = teamSize;
        }

        @Override public double calculateBonus() {
            return super.calculateBonus() + teamSize * 1000; // extra per direct report
        }
        @Override public String role() { return "Manager"; }
    }

    static class Intern extends Employee {
        Intern(String id, String name, double baseSalary) {
            super(id, name, baseSalary);
        }
        @Override public double calculateBonus() { return 0; } // no bonus
        @Override public String role() { return "Intern"; }
    }

    public static void main(String[] args) {

        Employee[] team = {
            new Engineer("E001", "Alice",   120_000, 5),
            new Engineer("E002", "Bob",      95_000, 3),
            new Manager( "M001", "Carol",   150_000, 4, 8),
            new Intern(  "I001", "Dave",     40_000),
        };

        System.out.println("--- Team Compensation ---");
        System.out.printf("%-10s %-12s %-12s %-14s %-14s %s%n",
            "Role", "ID", "Name", "Base", "Bonus", "Total");
        System.out.println("-".repeat(80));

        double totalPayroll = 0;
        for (Employee e : team) {
            System.out.println(e);             // polymorphic call to toString()
            totalPayroll += e.totalCompensation();
        }
        System.out.println("-".repeat(80));
        System.out.printf("Total payroll: $%.0f%n", totalPayroll);

        // --- instanceof and pattern matching ---
        System.out.println("\n--- instanceof ---");
        for (Employee e : team) {
            if (e instanceof Manager m) {
                System.out.println(m.getName() + " manages " + m.teamSize + " people");
            }
        }

        // --- Liskov Substitution: Manager can be used wherever Employee is ---
        Employee manager = new Manager("M999", "Eve", 200_000, 5, 12);
        System.out.println("\nLSP demo: " + manager.totalCompensation()); // correct Manager comp

        // TRY THIS:
        // 1. Add a SalesRep extends Employee that gets a 10% commission on a sales amount.
        //    Add salesAmount as a constructor parameter.
        // 2. What happens if you create a Manager with teamSize=0?
        //    Is that a valid state? Add validation.
        // 3. Violate LSP: override totalCompensation() in Intern to always return 0.
        //    Now iterate the team array calling totalCompensation() — is the behavior
        //    predictable? What went wrong?
    }
}
