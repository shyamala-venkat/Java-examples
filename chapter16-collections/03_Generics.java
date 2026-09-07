/**
 * CONCEPT: Generics — type-safe containers and methods
 *
 * Generics move type errors from runtime to compile time.
 * Without generics, you need casts everywhere and ClassCastException lurks.
 *
 * Key rules:
 *   PECS — Producer Extends, Consumer Super
 *   <? extends T> — read-only source (producer): accept List<Dog>, List<Cat> as List<? extends Animal>
 *   <? super T>   — write-only sink (consumer): accept List<Animal>, List<Object> as List<? super Dog>
 *
 * Type erasure: generics are compile-time only. At runtime, List<String> and List<Integer>
 * are both just List. You cannot do new T[] or instanceof List<String>.
 *
 * Why this matters:
 *   - Senior-level: explain PECS to a junior
 *   - Generic methods and bounded wildcards appear in library design
 *   - Understanding erasure explains why some generic patterns don't compile
 */
import java.util.*;
import java.util.function.Function;

class Generics {

    // Generic class: type parameter T
    static class Pair<A, B> {
        private final A first;
        private final B second;

        Pair(A first, B second) { this.first = first; this.second = second; }
        A first()  { return first; }
        B second() { return second; }

        @Override public String toString() { return "(" + first + ", " + second + ")"; }

        // Generic method on a generic class — T can differ from A, B
        <T> Pair<T, B> mapFirst(Function<A, T> fn) {
            return new Pair<>(fn.apply(first), second);
        }
    }

    // Bounded type parameter: T must be Comparable to itself
    static <T extends Comparable<T>> T max(List<T> list) {
        if (list.isEmpty()) throw new NoSuchElementException("empty list");
        T m = list.get(0);
        for (T item : list) if (item.compareTo(m) > 0) m = item;
        return m;
    }

    // PECS producer: read from source (? extends T) — can accept List<Integer>, List<Double>
    static double sumProducer(List<? extends Number> source) {
        double total = 0;
        for (Number n : source) total += n.doubleValue(); // read OK
        // source.add(1.5); // COMPILE ERROR — can't write to ? extends
        return total;
    }

    // PECS consumer: write to sink (? super T) — can accept List<Number>, List<Object>
    static void fillConsumer(List<? super Integer> sink, int count) {
        for (int i = 0; i < count; i++) sink.add(i * 10); // write OK
        // Integer x = sink.get(0); // COMPILE ERROR — can only read as Object
    }

    // Generic method: copy from producer to consumer
    static <T> void copy(List<? extends T> src, List<? super T> dst) {
        for (T item : src) dst.add(item);
    }

    public static void main(String[] args) {

        System.out.println("=== Generic Pair ===");
        Pair<String, Integer> p = new Pair<>("Alice", 30);
        System.out.println("Pair: " + p);
        System.out.println("Map first to length: " + p.mapFirst(String::length));

        System.out.println("\n=== Bounded type parameter ===");
        List<Integer> ints = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        System.out.println("Max int: " + max(ints));
        List<String> strs = Arrays.asList("banana", "apple", "cherry");
        System.out.println("Max str: " + max(strs));

        System.out.println("\n=== PECS ===");
        List<Integer> intList   = Arrays.asList(1, 2, 3);
        List<Double>  dblList   = Arrays.asList(1.1, 2.2, 3.3);
        System.out.println("Sum ints:    " + sumProducer(intList));   // List<Integer> is-a List<? extends Number>
        System.out.println("Sum doubles: " + sumProducer(dblList));   // List<Double>  is-a List<? extends Number>

        List<Number> numSink = new ArrayList<>();
        fillConsumer(numSink, 5);  // List<Number> is-a List<? super Integer>
        System.out.println("Filled sink: " + numSink);

        List<Object> objSink = new ArrayList<>();
        fillConsumer(objSink, 3); // List<Object> is also a List<? super Integer>
        System.out.println("Object sink: " + objSink);

        System.out.println("\n=== copy producer → consumer ===");
        List<Integer> source = Arrays.asList(10, 20, 30);
        List<Number>  dest   = new ArrayList<>();
        copy(source, dest); // <T=Integer>: source is ? extends Integer, dest is ? super Integer
        System.out.println("Copied: " + dest);

        // --- Type erasure demo ---
        System.out.println("\n=== Type Erasure ===");
        List<String>  strList = new ArrayList<>();
        List<Integer> intList2 = new ArrayList<>();
        System.out.println("Same class at runtime: " + (strList.getClass() == intList2.getClass()));
        // instanceof List<String> is a COMPILE ERROR — type info erased at runtime

        // TRY THIS:
        // 1. Write a generic method <T> Optional<T> findFirst(List<T>, Predicate<T>).
        //    Test it on a List<String> finding the first string longer than 5 chars.
        // 2. Create a generic Stack<T> class (array-backed) with push/pop/peek/isEmpty.
        //    Note: you cannot do new T[n] due to erasure — use (T[]) new Object[n].
        // 3. What happens if you call max() with an empty list? Handle it with Optional<T>.
    }
}
