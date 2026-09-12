/**
 * EXERCISE — Chapter 5: Extra-Strength Methods
 *
 * Problem: Evaluate Reverse Polish Notation (LeetCode #150)
 * ----------------------------------------------------------
 * Evaluate a mathematical expression in Reverse Polish Notation (postfix).
 *
 * Valid operators: +, -, *, /
 * Each operand may be an integer or the result of another expression.
 * Division truncates toward zero.
 *
 * Examples:
 *   ["2","1","+","3","*"]    → 9   (( 2 + 1 ) * 3)
 *   ["4","13","5","/","+"]   → 6   (4 + (13 / 5))
 *   ["10","6","9","3","+","-11","*","/","*","17","+","5","+"] → 22
 *
 * Algorithm:
 *   - Scan tokens left to right
 *   - If number → push onto stack
 *   - If operator → pop two operands, apply operator, push result
 *   - Final answer is the single value remaining on stack
 *
 * What this tests:
 *   - Stack usage (conceptually from ch02 exercise)
 *   - switch expressions for operator dispatch
 *   - Integer.parseInt and type conversions
 *   - Error handling (division by zero, malformed input)
 *
 * Bonus: Extend to support ^ (power) and % (modulo) operators.
 * Bonus 2: Convert an infix expression to postfix (Shunting Yard algorithm).
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class Chapter05Exercise {

    static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : tokens) {
            switch (token) {
                case "+" -> {
                    // TODO: pop two values, add, push result
                }
                case "-" -> {
                    // TODO: pop two values, subtract (first popped is right operand), push result
                }
                case "*" -> {
                    // TODO: pop two values, multiply, push result
                }
                case "/" -> {
                    // TODO: pop two values, divide (truncate toward zero), push result
                }
                default  -> {
                    // TODO: it's a number — parse and push
                }
            }
        }

        return stack.pop(); // final result
    }

    public static void main(String[] args) {

        System.out.println("=== Evaluate RPN ===");

        System.out.println(evalRPN(new String[]{"2","1","+","3","*"})
            + " (expected 9)");

        System.out.println(evalRPN(new String[]{"4","13","5","/","+"})
            + " (expected 6)");

        System.out.println(evalRPN(new String[]{"10","6","9","3","+","-11","*","/","*","17","+","5","+"})
            + " (expected 22)");

        // Edge cases
        System.out.println(evalRPN(new String[]{"3"})
            + " (expected 3)");

        System.out.println(evalRPN(new String[]{"7","-3","/"})
            + " (expected -2)");  // truncates toward zero

        // Bonus: extend evalRPN to handle ^ and %
        // System.out.println(evalRPN(new String[]{"2","10","^"}) + " (expected 1024)");
    }
}
