/**
 * EXERCISE — Chapter 2: A Trip to Objectville
 *
 * Problem: Implement a Stack from Scratch
 * ----------------------------------------
 * Design and implement a generic Stack<T> class without using java.util.Stack,
 * Deque, or any collection. Use an array internally.
 *
 * API to implement:
 *   void push(T item)       — add to top; throw IllegalStateException if full
 *   T    pop()              — remove and return top; throw NoSuchElementException if empty
 *   T    peek()             — return top without removing; throw if empty
 *   boolean isEmpty()
 *   boolean isFull()
 *   int  size()
 *
 * Then use your Stack to solve:
 *   isBalanced(String s)    — returns true if brackets are balanced
 *   Examples:
 *     "({[]})"   → true
 *     "([)]"     → false
 *     "{{"       → false
 *     ""         → true
 *
 * Senior tip: The bracket-matching problem appears in many forms —
 * validating HTML tags, JSON structure, arithmetic expressions.
 * Recognizing it as a stack problem is the key insight.
 *
 * Bonus: Implement a MinStack that supports:
 *   void push(int val)
 *   int  pop()
 *   int  peek()
 *   int  getMin()   — returns the current minimum in O(1) time
 *   (LeetCode #155)
 */
import java.util.NoSuchElementException;

public class Chapter02Exercise {

    // --- Generic Stack backed by an array ---
    static class Stack<T> {
        private Object[] data;
        private int top;

        @SuppressWarnings("unchecked")
        Stack(int capacity) {
            data = new Object[capacity];
            top  = -1;
        }

        // TODO: implement push, pop, peek, isEmpty, isFull, size
        void push(T item)  { } // stub — replace with real implementation
        @SuppressWarnings("unchecked")
        T    pop()         { return (T) data[top]; } // stub
        @SuppressWarnings("unchecked")
        T    peek()        { return (T) data[top]; } // stub
        boolean isEmpty()  { return top == -1; }
        boolean isFull()   { return top == data.length - 1; }
        int  size()        { return top + 1; }
    }

    // --- Bracket balancing using Stack ---
    static boolean isBalanced(String s) {
        // TODO: use your Stack<Character> here
        // Hint: push opening brackets; on closing bracket, pop and check for match
        return false; // replace with real logic
    }

    // --- Bonus: MinStack ---
    static class MinStack {
        // TODO: maintain two stacks — one for values, one tracking current minimums
        void push(int val)  { }
        int  pop()          { return 0; }
        int  peek()         { return 0; }
        int  getMin()       { return 0; }
    }

    public static void main(String[] args) {

        System.out.println("=== Stack Implementation ===");
        Stack<Integer> stack = new Stack<>(5);
        stack.push(10);
        stack.push(20);
        stack.push(30);
        System.out.println("peek: " + stack.peek());   // 30
        System.out.println("pop:  " + stack.pop());    // 30
        System.out.println("size: " + stack.size());   // 2

        System.out.println("\n=== Bracket Balancing ===");
        System.out.println(isBalanced("({[]})") + " (expected true)");
        System.out.println(isBalanced("([)]")   + " (expected false)");
        System.out.println(isBalanced("{{")     + " (expected false)");
        System.out.println(isBalanced("")       + " (expected true)");

        System.out.println("\n=== MinStack ===");
        MinStack ms = new MinStack();
        ms.push(5);
        ms.push(3);
        ms.push(7);
        ms.push(2);
        System.out.println("min: " + ms.getMin()); // 2
        ms.pop();
        System.out.println("min: " + ms.getMin()); // 3
        ms.pop();
        System.out.println("min: " + ms.getMin()); // 3
    }
}
