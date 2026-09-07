/**
 * CONCEPT: Optional<T> and Method References
 *
 * Optional<T>: a container that may or may not hold a value.
 * Use it as a RETURN TYPE — never as a parameter or field.
 * It forces callers to handle the absence case instead of risking NPE.
 *
 * Method references are shorthand for lambdas that delegate to a named method:
 *   Class::staticMethod    → Function<Arg, Return>      (e.g., Integer::parseInt)
 *   instance::method       → Supplier or Consumer        (e.g., list::add)
 *   Class::instanceMethod  → BiFunction<Class,Arg,Ret>  (e.g., String::startsWith)
 *   Class::new             → Supplier or Function        (e.g., ArrayList::new)
 *
 * Why this matters:
 *   - NPE is the most common Java exception; Optional is the idiomatic cure
 *   - Never return null from a method that might have no result — return Optional.empty()
 *   - Method references make stream pipelines readable: .map(String::toUpperCase)
 */
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class OptionalAndMethodRefs {

    record User(int id, String name, String email) {}

    // Returns Optional — caller MUST handle absence
    static Optional<User> findById(List<User> users, int id) {
        return users.stream().filter(u -> u.id() == id).findFirst();
    }

    static Optional<String> parsePositiveInt(String s) {
        try {
            int n = Integer.parseInt(s);
            return n > 0 ? Optional.of(n + " is positive") : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {

        // --- Optional creation ---
        System.out.println("=== Optional creation ===");
        Optional<String> present = Optional.of("hello");       // throws if null
        Optional<String> empty   = Optional.empty();
        Optional<String> maybe   = Optional.ofNullable(null);  // safe for null input

        System.out.println("present: " + present.isPresent());
        System.out.println("empty:   " + empty.isEmpty());     // Java 11+
        System.out.println("maybe:   " + maybe.isPresent());

        // --- Optional terminal operations ---
        System.out.println("\n=== Optional usage ===");
        List<User> users = Arrays.asList(
            new User(1, "Alice", "alice@example.com"),
            new User(2, "Bob",   "bob@example.com")
        );

        // get() — throws if empty, avoid unless sure
        // orElse — always evaluates the fallback
        // orElseGet — lazy: evaluates only if empty
        // orElseThrow — throw custom exception if empty
        User found = findById(users, 1).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Found: " + found.name());

        String name = findById(users, 99)
            .map(User::name)            // transform if present
            .orElse("Unknown");         // fallback
        System.out.println("ID 99: " + name);

        // filter + map chaining on Optional
        findById(users, 2)
            .filter(u -> u.email().contains("bob"))
            .map(u -> "Email: " + u.email())
            .ifPresent(System.out::println);

        // orElseGet vs orElse — orElse always creates object
        Optional<List<String>> opt = Optional.empty();
        List<String> safeList = opt.orElseGet(ArrayList::new); // lazy: no list created if present
        System.out.println("Safe list: " + safeList);

        // ifPresentOrElse (Java 9+)
        findById(users, 99)
            .ifPresentOrElse(
                u  -> System.out.println("Found: " + u.name()),
                () -> System.out.println("User 99 not found")
            );

        // --- flatMap: Optional<Optional<T>> → Optional<T> ---
        System.out.println("\n=== Optional flatMap ===");
        Optional<String> result = Optional.of("42").flatMap(s -> parsePositiveInt(s));
        System.out.println("'42' → " + result.orElse("empty"));
        Optional<String> neg = Optional.of("-5").flatMap(s -> parsePositiveInt(s));
        System.out.println("'-5' → " + neg.orElse("empty"));

        // --- Method References ---
        System.out.println("\n=== Method References ===");

        // 1. Static method reference
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println("parse '42': " + parse.apply("42"));

        // 2. Bound instance method reference (specific instance)
        String prefix = "Hello";
        Predicate<String> startsWith = prefix::startsWith; // prefix is captured
        System.out.println("'Hello world' starts with 'Hello': " + startsWith.test("Hello"));

        // 3. Unbound instance method reference (any instance of type)
        Function<String, String> toUpper = String::toUpperCase;
        System.out.println(Arrays.asList("a","b","c").stream().map(toUpper).toList());

        // 4. Constructor reference
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("initial");
        System.out.println("StringBuilder via ref: " + sb);

        // Method refs in streams
        System.out.println("\n=== Method refs in streams ===");
        List<String> names = users.stream()
            .map(User::name)             // unbound instance: User::name is User → String
            .map(String::toLowerCase)   // unbound: String::toLowerCase
            .collect(Collectors.toList());
        System.out.println(names);

        names.stream()
            .filter(Predicate.not(String::isEmpty)) // Predicate.not (Java 11)
            .forEach(System.out::println);          // bound instance: System.out is captured

        // TRY THIS:
        // 1. Chain three Optional operations: find user by id, get their email,
        //    filter only @example.com emails, uppercase the domain part.
        // 2. Write a method lookupConfig(Map<String,String>, String key)
        //    that returns Optional<String>. Use Optional.ofNullable(map.get(key)).
        //    Chain it: lookupConfig(map, "timeout").map(Integer::parseInt).orElse(30)
        // 3. Can you use Optional as a method parameter? Why is it a code smell?
        //    Hint: it doubles every caller's branching logic with no benefit.
    }
}
