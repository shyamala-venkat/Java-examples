/**
 * EXERCISE — Chapter 3: Know Your Variables
 *
 * Problem: Roman to Integer (LeetCode #13)
 * -----------------------------------------
 * Convert a Roman numeral string to an integer.
 *
 * Roman symbols:  I=1  V=5  X=10  L=50  C=100  D=500  M=1000
 *
 * Subtraction rule: if a smaller symbol appears BEFORE a larger one,
 * subtract it instead of adding:
 *   IV = 4   IX = 9   XL = 40   XC = 90   CD = 400   CM = 900
 *
 * Examples:
 *   "III"    → 3
 *   "LVIII"  → 58  (L=50, V=5, III=3)
 *   "MCMXCIV"→ 1994 (M=1000, CM=900, XC=90, IV=4)
 *
 * Constraints: 1 <= s.length <= 15, valid Roman numerals only, range 1-3999
 *
 * Hint: scan left to right. If current symbol's value < next symbol's value,
 * subtract it; otherwise add it. This handles all subtraction cases in one pass.
 *
 * What Java skills this tests:
 *   - char → int conversion and Character methods
 *   - Map/switch for symbol lookup
 *   - Single-pass algorithm design
 *
 * Bonus: Implement the reverse — Integer to Roman (#12)
 *   12    → "XII"
 *   1994  → "MCMXCIV"
 *   Hint: greedily subtract largest possible value, append symbol, repeat.
 */
import java.util.Map;

public class Chapter03Exercise {

    static int romanToInt(String s) {
        // TODO: implement this
        // Hint: Map<Character, Integer> values = Map.of('I',1, 'V',5, 'X',10, ...);
        return 0;
    }

    static String intToRoman(int num) {
        // TODO: implement bonus
        // Hint: build two arrays — values[] and symbols[] — in descending order
        // int[] values  = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        // String[] syms = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        return "";
    }

    public static void main(String[] args) {
        System.out.println("=== Roman to Integer ===");
        System.out.println(romanToInt("III")     + " (expected 3)");
        System.out.println(romanToInt("LVIII")   + " (expected 58)");
        System.out.println(romanToInt("MCMXCIV") + " (expected 1994)");
        System.out.println(romanToInt("IV")      + " (expected 4)");
        System.out.println(romanToInt("IX")      + " (expected 9)");

        System.out.println("\n=== Integer to Roman (Bonus) ===");
        System.out.println(intToRoman(3)    + " (expected III)");
        System.out.println(intToRoman(58)   + " (expected LVIII)");
        System.out.println(intToRoman(1994) + " (expected MCMXCIV)");
    }
}
