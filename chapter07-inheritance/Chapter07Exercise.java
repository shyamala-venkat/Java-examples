/**
 * EXERCISE — Chapter 7: Inheritance
 *
 * Problem 1: Employee Payroll System
 * ---------------------------------
 * Design a payroll system for a tech company with these employee types:
 *
 *   FullTimeEmployee(id, name, annualSalary)
 *     - monthly pay = annualSalary / 12
 *     - bonus = 10% of annualSalary if performance >= 4
 *
 *   PartTimeEmployee(id, name, hourlyRate, hoursWorked)
 *     - monthly pay = hourlyRate * hoursWorked
 *     - no bonus
 *
 *   Contractor(id, name, dailyRate, daysWorked)
 *     - monthly pay = dailyRate * daysWorked
 *     - no bonus, but 15% agency fee deducted from company's perspective
 *       (add a agencyCost() method — agency cost = monthly pay * 0.15)
 *
 * Requirements:
 *   1. Abstract base class Employee with: id, name, abstract monthlyPay(),
 *      abstract bonus(), and a final totalCost() = monthlyPay() + bonus()
 *   2. Generate a payroll report for a mixed team (see main)
 *   3. Sort employees by totalCost() descending
 *   4. Print highest and lowest paid employee
 *   5. Compute total company payroll cost
 *
 * Note: For Contractor, totalCost() should include the agencyCost().
 *       Override totalCost() in Contractor — think carefully about LSP here.
 *
 * Problem 2: SOLID Refactor — Break the God Class
 * -------------------------------------------------
 * OrderProcessorBad below "works" but violates SRP (it validates, prices,
 * charges payment, and emails — four unrelated reasons to change) and OCP
 * (adding a new payment provider means editing this class directly).
 *
 * Your task: refactor it into collaborators WITHOUT changing observable output.
 *   - OrderValidator                    — SRP: validation has its own reason to change
 *   - TaxCalculator                     — OCP: swap tax rules without touching orchestration
 *   - PaymentCharger interface +
 *     CreditCardCharger implementation  — DIP: orchestrator depends on an abstraction,
 *                                          not a concrete gateway (mirrors real payment
 *                                          integration work)
 *   - EmailNotifier                     — SRP again: notification is not order logic
 *   - OrderProcessor                    — slim orchestrator: wires collaborators via its
 *                                          constructor (manual dependency injection) and
 *                                          contains NO business logic itself
 *
 * For each class you write, add a one-line comment naming which SOLID principle
 * the split satisfies.
 *
 * Verify: OrderProcessor.process(order) on the same test orders prints IDENTICAL
 * output to OrderProcessorBad.process(order).
 */
import java.util.*;

public class Chapter07Exercise {

    // ========= Problem 2 baseline: the God class (given — do not refactor this one) =========
    static class OrderProcessorBad {
        record Order(String id, double amount, String customerEmail, boolean expedited) {}

        void process(Order order) {
            // validate
            if (order.amount() <= 0) throw new IllegalArgumentException("Invalid amount");
            if (order.customerEmail() == null || !order.customerEmail().contains("@"))
                throw new IllegalArgumentException("Invalid email");

            // calculate tax
            double taxRate = order.expedited() ? 0.10 : 0.08;
            double total = order.amount() * (1 + taxRate);

            // charge payment (pretend gateway call)
            System.out.printf("  [charge] $%.2f to card for order %s%n", total, order.id());

            // send email
            System.out.println("  [email] Sent confirmation to " + order.customerEmail());

            System.out.printf("Order %s processed: total=$%.2f%n", order.id(), total);
        }
    }

    // ========= Problem 2: TODO — refactor into collaborators (see problem statement) =========
    interface PaymentCharger {
        void charge(double amount, String orderId);
    }

    static class OrderValidator {
        // TODO: void validate(OrderProcessorBad.Order order) — same rules as OrderProcessorBad
    }

    static class TaxCalculator {
        // TODO: double totalWithTax(OrderProcessorBad.Order order) — same rates as OrderProcessorBad
    }

    static class CreditCardCharger implements PaymentCharger {
        @Override public void charge(double amount, String orderId) {
            // TODO: same console line as OrderProcessorBad's charge step
        }
    }

    static class EmailNotifier {
        // TODO: void sendConfirmation(String email) — same console line as OrderProcessorBad
    }

    static class OrderProcessor {
        // TODO: constructor takes OrderValidator, TaxCalculator, PaymentCharger, EmailNotifier
        // TODO: void process(OrderProcessorBad.Order order) — delegates to each collaborator,
        //       no business logic here, only orchestration
    }

    static abstract class Employee {
        final String id, name;
        Employee(String id, String name) { this.id = id; this.name = name; }

        abstract double monthlyPay();
        abstract double bonus();

        double totalCost() { return monthlyPay() + bonus(); } // override in Contractor

        @Override public String toString() {
            return String.format("%-10s %-12s pay=$%-9.2f bonus=$%-8.2f total=$%.2f",
                getClass().getSimpleName(), name, monthlyPay(), bonus(), totalCost());
        }
    }

    // TODO: implement FullTimeEmployee, PartTimeEmployee, Contractor

    public static void main(String[] args) {
        List<Employee> team = new ArrayList<>();
        // TODO: add employees:
        // FullTime:   "E001" Alice   120000/yr perf=5
        //             "E002" Bob      85000/yr perf=3
        // PartTime:   "P001" Carol   $45/hr 120hrs
        // Contractor: "C001" Dave    $800/day 20days

        System.out.println("=== Payroll Report ===");
        // TODO: print all employees

        System.out.println("\n=== Sorted by Cost (desc) ===");
        // TODO: sort and print

        System.out.println("\n=== Summary ===");
        // TODO: highest paid, lowest paid, total payroll

        System.out.println("\n=== Problem 2: SOLID Refactor ===");
        OrderProcessorBad.Order order = new OrderProcessorBad.Order(
            "O-100", 199.99, "shopper@example.com", true);

        System.out.println("-- OrderProcessorBad (baseline) --");
        new OrderProcessorBad().process(order);

        System.out.println("-- OrderProcessor (refactored) --");
        // TODO: wire up OrderProcessor with its collaborators and call process(order);
        //       output above should match the baseline exactly
    }
}
