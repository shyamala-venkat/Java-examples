/**
 * CONCEPT: Stream Pipelines — lazy, declarative data processing
 *
 * A stream pipeline has:
 *   Source          → Collection, array, Stream.of(), Stream.generate(), Stream.iterate()
 *   Intermediate ops → lazy (not executed until terminal op): filter, map, flatMap, sorted,
 *                      distinct, limit, skip, peek, mapToInt/Long/Double
 *   Terminal op     → triggers execution: collect, forEach, reduce, count, findFirst,
 *                      anyMatch, allMatch, noneMatch, min, max, toArray
 *
 * Short-circuit: findFirst, anyMatch, limit can stop early.
 * Parallel streams: .parallel() — splits source, processes in ForkJoinPool, merges.
 *
 * Why this matters:
 *   - Replaces imperative loop soup with readable, chainable operations
 *   - flatMap is the most misunderstood — flattens one level of nesting
 *   - Collectors.groupingBy is extremely common in analytics code
 *   - parallel() is a double-edged sword: correct only for stateless ops
 */
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class StreamPipelines {

    record Employee(String name, String dept, double salary) {}

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
            new Employee("Alice",   "Engineering", 95_000),
            new Employee("Bob",     "Engineering", 82_000),
            new Employee("Carol",   "Marketing",   74_000),
            new Employee("David",   "Marketing",   91_000),
            new Employee("Eve",     "Engineering", 110_000),
            new Employee("Frank",   "HR",          65_000),
            new Employee("Grace",   "HR",          70_000)
        );

        // --- filter + map + sorted + collect ---
        System.out.println("=== Engineers earning > 85k (sorted) ===");
        List<String> highEarnEngineers = employees.stream()
            .filter(e -> "Engineering".equals(e.dept()))
            .filter(e -> e.salary() > 85_000)
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .map(Employee::name)
            .collect(Collectors.toList());
        System.out.println(highEarnEngineers);

        // --- reduce: manual sum ---
        System.out.println("\n=== Total payroll (reduce) ===");
        double payroll = employees.stream()
            .mapToDouble(Employee::salary)
            .sum(); // equivalent to reduce(0, Double::sum)
        System.out.printf("Total: $%.0f%n", payroll);

        // --- groupingBy: department → employees ---
        System.out.println("\n=== Group by department ===");
        Map<String, List<Employee>> byDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::dept));
        byDept.forEach((dept, emps) ->
            System.out.println(dept + ": " + emps.stream().map(Employee::name).toList()));

        // --- groupingBy + counting, averaging ---
        System.out.println("\n=== Dept stats ===");
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)));
        avgSalaryByDept.forEach((dept, avg) ->
            System.out.printf("  %-15s avg: $%.0f%n", dept, avg));

        // --- flatMap: flatten nested lists ---
        System.out.println("\n=== flatMap ===");
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8, 9)
        );
        List<Integer> flat = nested.stream()
            .flatMap(Collection::stream)  // one level of nesting removed
            .collect(Collectors.toList());
        System.out.println("Flat: " + flat);

        // flatMap to split words and count unique words
        String text = "the cat sat on the mat the cat";
        long uniqueWords = Arrays.stream(text.split(" "))
            .distinct()
            .count();
        System.out.println("Unique words: " + uniqueWords);

        // --- Short-circuit operations ---
        System.out.println("\n=== Short-circuit ===");
        Optional<Employee> first = employees.stream()
            .filter(e -> e.salary() > 100_000)
            .findFirst(); // stops after first match — doesn't process rest
        first.ifPresent(e -> System.out.println("First >100k: " + e.name()));

        boolean anyHR = employees.stream().anyMatch(e -> "HR".equals(e.dept()));
        System.out.println("Any HR: " + anyHR);

        // --- Collectors.joining ---
        System.out.println("\n=== Collectors.joining ===");
        String nameList = employees.stream()
            .map(Employee::name)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(nameList);

        // --- Stream.generate and Stream.iterate ---
        System.out.println("\n=== Infinite streams (limited) ===");
        List<Integer> fibs = Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
            .limit(10)
            .map(f -> f[0])
            .collect(Collectors.toList());
        System.out.println("Fibonacci: " + fibs);

        // --- Parallel stream: when and why ---
        System.out.println("\n=== Parallel stream ===");
        long start = System.currentTimeMillis();
        long sequentialSum = LongStream.rangeClosed(1, 50_000_000).sum();
        System.out.println("Sequential: " + (System.currentTimeMillis() - start) + "ms sum=" + sequentialSum);

        start = System.currentTimeMillis();
        long parallelSum = LongStream.rangeClosed(1, 50_000_000).parallel().sum();
        System.out.println("Parallel:   " + (System.currentTimeMillis() - start) + "ms sum=" + parallelSum);
        // Parallel wins here because sum is associative + stateless

        // TRY THIS:
        // 1. Use Collectors.toMap() to build a Map<name, salary> from employees.
        //    What happens with duplicate keys? How do you handle with mergeFunction?
        // 2. Use Collectors.partitioningBy() to split employees into two groups:
        //    those earning above average and below average.
        // 3. Chain .peek() between operations to debug which elements pass each stage.
        //    Note: peek is for debugging only — not side-effectful logic.
    }
}
