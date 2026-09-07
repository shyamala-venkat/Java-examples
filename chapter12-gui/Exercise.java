/**
 * EXERCISE — Chapter 12: Getting a GUI
 *
 * Problem: Simple counter app — demonstrates Observer pattern from scratch
 * -------------------------------------------------------
 *
 * Build a Swing counter with:
 *   - A JLabel showing the current count (starts at 0)
 *   - An Increment button (+1)
 *   - A Decrement button (-1, minimum 0)
 *   - A Reset button (back to 0)
 *   - Color feedback: green if > 0, red if 0
 *
 * Design:
 *   - Separate CounterModel (state: int count) from the view
 *   - CounterModel.addObserver(Runnable) — observer pattern without java.util.Observer
 *   - Buttons call model methods; model notifies observers; view reads model and updates label
 *
 * This exercise demonstrates:
 *   - Model-View separation (MVC lite)
 *   - Observer pattern with lambdas as observers
 *   - Correct EDT usage (all Swing calls on EDT)
 *
 * NOTE: If running headlessly, print the observer notification chain instead of showing GUI.
 */
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Exercise {

    static class CounterModel {
        private int count = 0;
        private final List<Runnable> observers = new ArrayList<>();

        void addObserver(Runnable r) { observers.add(r); }
        private void notifyObservers() { observers.forEach(Runnable::run); }

        void increment() {
            // TODO: increment count, notifyObservers
        }

        void decrement() {
            // TODO: decrement count (min 0), notifyObservers
        }

        void reset() {
            // TODO: set count to 0, notifyObservers
        }

        int getCount() { return count; }
    }

    static void buildAndShow(CounterModel model) {
        JFrame frame = new JFrame("Counter");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        frame.setSize(300, 150);

        JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
        countLabel.setFont(new Font("Monospaced", Font.BOLD, 36));

        JButton incBtn  = new JButton("+");
        JButton decBtn  = new JButton("-");
        JButton rstBtn  = new JButton("Reset");

        // TODO: add action listeners
        // incBtn → model.increment()
        // decBtn → model.decrement()
        // rstBtn → model.reset()

        // TODO: register observer that updates countLabel text and color
        // green if count > 0, red if count == 0

        frame.add(countLabel);
        frame.add(incBtn); frame.add(decBtn); frame.add(rstBtn);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        CounterModel model = new CounterModel();

        boolean headless = GraphicsEnvironment.isHeadless();
        if (headless) {
            // Test the model without UI
            System.out.println("=== Headless Counter Test ===");
            model.addObserver(() -> System.out.println("Count changed: " + model.getCount()));
            model.increment(); // Count changed: 1
            model.increment(); // Count changed: 2
            model.decrement(); // Count changed: 1
            model.decrement(); // Count changed: 0
            model.decrement(); // Count changed: 0 (no below 0)
            model.reset();     // Count changed: 0
            System.out.println("Final: " + model.getCount() + " (expected 0)");
        } else {
            model.addObserver(() -> System.out.println("Observer notified: " + model.getCount()));
            SwingUtilities.invokeLater(() -> buildAndShow(model));
        }
    }
}
