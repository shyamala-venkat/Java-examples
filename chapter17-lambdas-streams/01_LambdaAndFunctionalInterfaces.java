/**
 * CONCEPT: Lambdas and Functional Interfaces
 *
 * A lambda is an anonymous function: (params) -> body
 * A functional interface has exactly one abstract method (@FunctionalInterface).
 *
 * Built-in functional interfaces in java.util.function:
 *   Predicate<T>        — T → boolean          (filter, test)
 *   Function<T,R>       — T → R                (map, transform)
 *   Consumer<T>         — T → void             (forEach, side effects)
 *   Supplier<T>         — () → T               (lazy evaluation, factory)
 *   BiFunction<T,U,R>   — (T,U) → R            (combine two inputs)
 *   UnaryOperator<T>    — T → T                (replaceAll)
 *   BinaryOperator<T>   — (T,T) → T            (reduce)
 *
 * Why this matters:
 *   - All Stream operations take functional interfaces
 *   - Lambdas enable Strategy pattern without boilerplate classes
 *   - Composing functions (andThen, compose, and, or, negate) is a senior skill
 */
import java.util.*;
import java.util.function.*;

class LambdaAndFunctionalInterfaces {

    @FunctionalInterface
    interface Transformer<T, R> {
        R transform(T input);
        // only ONE abstract method — default/static methods allowed
        default Transformer<T, String> andStringify() {
            return input -> String.valueOf(this.transform(input));
        }
    }

    public static void main(String[] args) {

        // --- Lambda syntax forms ---
        System.out.println("=== Lambda syntax ===");
        Runnable r1 = () -> System.out.println("No params");
        Runnable r2 = () -> { System.out.println("Block body"); System.out.println("multi-line"); };
        Comparator<String> byLength = (a, b) -> a.length() - b.length();
        Function<String, Integer> parse = Integer::parseInt; // method reference

        r1.run(); r2.run();
        System.out.println(byLength.compare("hi", "hello")); // negative
        System.out.println(parse.apply("42"));

        // --- Predicate: composing filters ---
        System.out.println("\n=== Predicate composition ===");
        Predicate<String> notEmpty  = s -> !s.isEmpty();
        Predicate<String> longEnough = s -> s.length() > 3;
        Predicate<String> valid = notEmpty.and(longEnough);

        List<String> words = Arrays.asList("", "hi", "java", "streams", "lambda");
        words.stream()
             .filter(valid)
             .forEach(w -> System.out.println("  valid: " + w));

        System.out.println("Negate: " + valid.negate().test("hi")); // true (failed valid)

        // --- Function: compose and andThen ---
        System.out.println("\n=== Function composition ===");
        Function<String, String> trim   = String::trim;
        Function<String, String> upper  = String::toUpperCase;
        Function<String, Integer> length = String::length;

        // andThen: f → g (left to right)
        Function<String, Integer> trimUpperLength = trim.andThen(upper).andThen(length);
        System.out.println("\"  hello  \" → " + trimUpperLength.apply("  hello  ")); // 5

        // compose: g(f(x)) — right to left
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> addThree = x -> x + 3;
        System.out.println("double then add3: " + doubleIt.andThen(addThree).apply(5));  // 13
        System.out.println("add3 then double: " + doubleIt.compose(addThree).apply(5));   // 16

        // --- Consumer: chaining side effects ---
        System.out.println("\n=== Consumer chaining ===");
        Consumer<String> print  = System.out::println;
        Consumer<String> log    = s -> System.out.println("[LOG] " + s);
        Consumer<String> both   = print.andThen(log);
        both.accept("event");

        // --- Supplier: lazy evaluation ---
        System.out.println("\n=== Supplier (lazy) ===");
        Supplier<List<String>> listFactory = ArrayList::new; // no list created yet
        List<String> a = listFactory.get(); // created here
        List<String> b = listFactory.get(); // new list, not same as a
        a.add("hello");
        System.out.println("a: " + a + ", b: " + b + " (independent)");

        // --- BiFunction and BinaryOperator ---
        System.out.println("\n=== BiFunction / BinaryOperator ===");
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println(repeat.apply("ab", 3)); // "ababab"

        BinaryOperator<Integer> max = (x, y) -> x > y ? x : y;
        System.out.println("max(7,3): " + max.apply(7, 3));

        // --- Custom functional interface ---
        System.out.println("\n=== Custom @FunctionalInterface ===");
        Transformer<String, Integer> wordCount = s -> s.split("\\s+").length;
        System.out.println("Words: " + wordCount.transform("hello world foo"));
        System.out.println("Stringified: " + wordCount.andStringify().transform("one two"));

        // TRY THIS:
        // 1. Write a generic pipeline method:
        //    <T,R> R pipeline(T input, List<Function<Object,Object>> steps)
        //    Apply each function in sequence. Hint: use Function.andThen in a loop.
        // 2. Use Predicate.not() (Java 11+) to filter non-blank strings.
        //    List.of("a","","b"," ").stream().filter(Predicate.not(String::isBlank))
        // 3. Create a memoize(Function<T,R>) that caches results in a HashMap.
        //    Use computeIfAbsent inside. Test with an expensive Fibonacci function.
    }
}
