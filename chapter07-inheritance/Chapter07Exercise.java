/**
 * EXERCISE — Chapter 7: Inheritance
 *
 * Problem: Employee Payroll System
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
 */
import java.util.*;

public class Chapter07Exercise {

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
    }
}
