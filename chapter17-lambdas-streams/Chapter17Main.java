/**
 * Chapter 17: Lambda Expressions and Streams
 *
 * Key Concepts:
 *  - Lambda expressions: concise anonymous function syntax
 *  - Functional interfaces: the foundation of lambdas
 *  - Built-in functional interfaces: Predicate, Function, Consumer, Supplier, BiFunction
 *  - Method references: :: operator (4 forms)
 *  - Streams API: pipeline of operations on collections
 *  - Intermediate operations: filter, map, sorted, distinct, limit, flatMap
 *  - Terminal operations: collect, forEach, reduce, count, min, max, anyMatch, allMatch
 *  - Optional<T> — handling potentially absent values
 *  - Collectors: toList, groupingBy, counting, joining, summarizing
 *
 * How to run:
 *   javac Chapter17Main.java
 *   java Chapter17Main
 */
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Chapter17Main {

    record Employee(String name, String department, double salary, int age) {
        @Override public String toString() {
            return String.format("%-10s %-12s $%7.0f age=%d", name, department, salary, age);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== Chapter 17: Lambda Expressions and Streams ===\n");

        // -------------------------------------------------------
        // CONCEPT 1: Lambda Expressions
        //
        // Lambda = anonymous function (no name, no class needed)
        // Syntax: (parameters) -> expression
        //      or (parameters) -> { statements; }
        //
        // They replace anonymous classes for functional interfaces.
        // -------------------------------------------------------
        System.out.println("--- Lambda Expressions ---");

        // Old way — anonymous class
        Runnable oldWay = new Runnable() {
            @Override public void run() {
                System.out.println("Anonymous class Runnable");
            }
        };

        // Lambda way
        Runnable lambdaWay = () -> System.out.println("Lambda Runnable");

        oldWay.run();
        lambdaWay.run();

        // Lambdas with parameters
        Comparator<String> byLength = (s1, s2) -> s1.length() - s2.length();
        List<String> words = new ArrayList<>(Arrays.asList("banana", "fig", "apple", "kiwi"));
        words.sort(byLength);
        System.out.println("Sorted by length: " + words);

        // -------------------------------------------------------
        // CONCEPT 2: Built-in Functional Interfaces (java.util.function)
        // -------------------------------------------------------
        System.out.println("\n--- Functional Interfaces ---");

        // Predicate<T>: takes T, returns boolean — used for filtering
        Predicate<String> isLong      = s -> s.length() > 5;
        Predicate<Integer> isEven     = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;

        System.out.println("isLong(\"banana\"): " + isLong.test("banana")); // true
        System.out.println("isLong(\"fig\"):    " + isLong.test("fig"));    // false

        // Predicates can be combined with and(), or(), negate()
        Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
        System.out.println("isEvenAndPositive(4):  " + isEvenAndPositive.test(4));  // true
        System.out.println("isEvenAndPositive(-4): " + isEvenAndPositive.test(-4)); // false

        // Function<T, R>: takes T, returns R — used for transformation/mapping
        Function<String, Integer> strLen = String::length; // method reference!
        Function<Integer, String> intToHex = Integer::toHexString;
        System.out.println("\"hello\" length: " + strLen.apply("hello")); // 5
        System.out.println("255 in hex:     " + intToHex.apply(255));   // ff

        // Compose functions: f.andThen(g) means g(f(x))
        Function<String, String> lengthAsHex = strLen.andThen(intToHex);
        System.out.println("\"hello\" length as hex: " + lengthAsHex.apply("hello")); // "5"

        // Consumer<T>: takes T, returns nothing — used for side effects
        Consumer<String> printer = System.out::println;
        Consumer<String> shout   = s -> System.out.println(s.toUpperCase());
        Consumer<String> printThenShout = printer.andThen(shout);
        printThenShout.accept("hello");

        // Supplier<T>: takes nothing, returns T — used for lazy creation
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        System.out.println("New list from Supplier: " + newList);

        // BiFunction<T, U, R>: takes T and U, returns R
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println("repeat(\"ab\", 3): " + repeat.apply("ab", 3)); // ababab

        // -------------------------------------------------------
        // CONCEPT 3: Method References — shorthand for lambdas that call a method
        //
        // Four forms:
        //   1. ClassName::staticMethod   -> (args) -> ClassName.staticMethod(args)
        //   2. instance::instanceMethod  -> (args) -> instance.instanceMethod(args)
        //   3. ClassName::instanceMethod -> (obj, args) -> obj.method(args)
        //   4. ClassName::new            -> (args) -> new ClassName(args)
        // -------------------------------------------------------
        System.out.println("\n--- Method References ---");

        List<String> fruits = Arrays.asList("apple", "BANANA", "Cherry");

        // 1. Static method reference
        fruits.stream().map(String::valueOf).forEach(System.out::println); // same as s -> String.valueOf(s)

        // 3. Instance method of arbitrary object of a type (most common)
        fruits.stream().map(String::toUpperCase).forEach(System.out::println);

        // 4. Constructor reference
        Supplier<ArrayList<String>> listSupplier = ArrayList::new;
        ArrayList<String> freshList = listSupplier.get();

        System.out.println("Method references work as lambda shortcuts.");

        // -------------------------------------------------------
        // CONCEPT 4: Streams API
        //
        // A Stream is a sequence of elements from a source.
        // It processes data through a PIPELINE:
        //   Source -> Intermediate ops (lazy) -> Terminal op (eager, triggers processing)
        //
        // Streams are NOT data structures — they don't store data.
        // They process data from collections, arrays, or I/O.
        // -------------------------------------------------------
        System.out.println("\n--- Streams API ---");

        List<Employee> employees = Arrays.asList(
            new Employee("Alice",   "Engineering", 95000, 30),
            new Employee("Bob",     "Marketing",   65000, 25),
            new Employee("Charlie", "Engineering", 85000, 35),
            new Employee("Dana",    "HR",          55000, 28),
            new Employee("Eve",     "Engineering", 110000, 32),
            new Employee("Frank",   "Marketing",   72000, 29),
            new Employee("Grace",   "HR",          60000, 26)
        );

        // filter + forEach
        System.out.println("Engineers:");
        employees.stream()
            .filter(e -> e.department().equals("Engineering"))
            .forEach(e -> System.out.println("  " + e));

        // filter + map + collect
        List<String> highEarnerNames = employees.stream()
            .filter(e -> e.salary() > 80000)
            .map(Employee::name)
            .sorted()
            .collect(Collectors.toList());
        System.out.println("High earners (>$80k): " + highEarnerNames);

        // reduce — folding all elements into one result
        double totalSalary = employees.stream()
            .mapToDouble(Employee::salary)
            .sum(); // shortcut for reduce
        System.out.printf("Total salary: $%.0f%n", totalSalary);

        double avgSalary = employees.stream()
            .mapToDouble(Employee::salary)
            .average()
            .orElse(0); // orElse handles the Optional
        System.out.printf("Avg salary:   $%.0f%n", avgSalary);

        // count, min, max
        long engineerCount = employees.stream()
            .filter(e -> e.department().equals("Engineering"))
            .count();
        System.out.println("Engineer count: " + engineerCount);

        Optional<Employee> highestPaid = employees.stream()
            .max(Comparator.comparingDouble(Employee::salary));
        highestPaid.ifPresent(e -> System.out.println("Highest paid: " + e.name() + " $" + e.salary()));

        // anyMatch, allMatch, noneMatch
        boolean anyUnderPaid = employees.stream().anyMatch(e -> e.salary() < 50000);
        boolean allAboveMinWage = employees.stream().allMatch(e -> e.salary() > 30000);
        System.out.println("Any under $50k: " + anyUnderPaid);
        System.out.println("All above $30k: " + allAboveMinWage);

        // -------------------------------------------------------
        // CONCEPT 5: Collectors — collecting stream results
        // -------------------------------------------------------
        System.out.println("\n--- Collectors ---");

        // groupingBy — group into a Map<key, List<value>>
        Map<String, List<Employee>> byDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::department));
        byDept.forEach((dept, emps) -> {
            System.out.println(dept + ": " + emps.stream().map(Employee::name).collect(Collectors.joining(", ")));
        });

        // groupingBy + counting
        Map<String, Long> countByDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        System.out.println("Count by dept: " + new TreeMap<>(countByDept));

        // groupingBy + averagingDouble
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::department,
                     Collectors.averagingDouble(Employee::salary)));
        avgSalaryByDept.forEach((dept, avg) ->
            System.out.printf("  %-12s avg salary: $%.0f%n", dept, avg));

        // joining — concatenate strings
        String allNames = employees.stream()
            .map(Employee::name)
            .sorted()
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("All names: " + allNames);

        // -------------------------------------------------------
        // CONCEPT 6: Optional<T> — avoid null checks
        //
        // Optional wraps a value that might be absent.
        // Forces you to handle the "not present" case explicitly.
        // -------------------------------------------------------
        System.out.println("\n--- Optional<T> ---");

        Optional<String> present = Optional.of("Hello");
        Optional<String> absent  = Optional.empty();

        System.out.println("isPresent: " + present.isPresent()); // true
        System.out.println("get:       " + present.get());        // Hello
        System.out.println("orElse:    " + absent.orElse("default")); // default
        System.out.println("orElseGet: " + absent.orElseGet(() -> "computed default"));

        present.ifPresent(s -> System.out.println("Value: " + s));
        Optional<String> upper = present.map(String::toUpperCase);
        System.out.println("Mapped:    " + upper.orElse("?"));

        // -------------------------------------------------------
        // CONCEPT 7: flatMap — flatten nested structures
        // -------------------------------------------------------
        System.out.println("\n--- flatMap ---");
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8, 9)
        );
        List<Integer> flat = nested.stream()
            .flatMap(Collection::stream) // flatten List<List<T>> to Stream<T>
            .collect(Collectors.toList());
        System.out.println("Flattened: " + flat);

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Using the employees list above, use the Streams API to:");
        System.out.println("  1. Find employees aged 28-32 in Engineering, print their names");
        System.out.println("  2. Find the department with the highest average salary");
        System.out.println("  3. Create a Map<String, Double> of employee name -> salary for HR dept");
        System.out.println("  4. Print all unique departments in alphabetical order");
        System.out.println("  5. Find the youngest employee in Marketing");
        System.out.println("     (use min + Comparator.comparingInt + Optional.ifPresent)");

        // TODO: EXERCISE — implement all 5 stream queries here
    }
}
