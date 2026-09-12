/**
 * CONCEPT: Event Handling — ActionListener, lambda listeners, inner classes
 *
 * Swing event model: observer pattern.
 *   Source (button, field) → fires events → registered Listener handles them
 *
 * Listener styles:
 *   1. Named class  — verbose, reusable if listener is complex
 *   2. Anonymous class — old Java 7 style, still seen in legacy code
 *   3. Lambda — modern (ActionListener is @FunctionalInterface)
 *
 * EDT (Event Dispatch Thread): ALL Swing UI updates must happen on the EDT.
 * SwingUtilities.invokeLater(Runnable) schedules work on the EDT from any thread.
 * Doing Swing work off-EDT causes random visual glitches — a classic bug.
 *
 * Why this matters for interviews:
 *   - Observer pattern in GUI is a concrete example of the design pattern
 *   - EDT + background work → SwingWorker — the threading model question
 *   - Lambda replacing anonymous class = Java 8 refactoring question
 *
 * NOTE: This file demonstrates concepts; running it opens a real Swing window.
 *       To run headlessly (e.g., in CI), use the non-GUI demo at the bottom.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class EventHandling {

    // Named listener class — use when logic is complex or reused
    static class SubmitListener implements ActionListener {
        private final JLabel feedback;
        private final JTextField input;

        SubmitListener(JTextField input, JLabel feedback) {
            this.input = input;
            this.feedback = feedback;
        }

        @Override public void actionPerformed(ActionEvent e) {
            String text = input.getText().trim();
            if (text.isEmpty()) {
                feedback.setText("Please enter something.");
                feedback.setForeground(Color.RED);
            } else {
                feedback.setText("Submitted: " + text.toUpperCase());
                feedback.setForeground(new Color(0, 128, 0));
                input.setText("");
            }
        }
    }

    static void buildAndShowGUI() {
        JFrame frame = new JFrame("Event Handling Demo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        frame.setSize(400, 200);

        JTextField inputField = new JTextField(20);
        JLabel feedbackLabel  = new JLabel("Enter text and click Submit");
        JButton submitBtn     = new JButton("Submit");
        JButton clearBtn      = new JButton("Clear");
        JButton countBtn      = new JButton("Count");
        JLabel  countLabel    = new JLabel("0 submissions");

        final int[] submitCount = {0}; // effectively final array trick for lambda capture

        // Style 1: Named class
        submitBtn.addActionListener(new SubmitListener(inputField, feedbackLabel));

        // Style 2: Lambda (ActionListener is @FunctionalInterface)
        submitBtn.addActionListener(e -> {
            submitCount[0]++;
            countLabel.setText(submitCount[0] + " submission(s)");
        });

        // Style 3: Lambda for clear
        clearBtn.addActionListener(e -> {
            inputField.setText("");
            feedbackLabel.setText("Cleared.");
            feedbackLabel.setForeground(Color.BLACK);
        });

        // KeyListener: respond on Enter key in text field
        inputField.addKeyListener(new KeyAdapter() { // anonymous class (extends KeyAdapter)
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitBtn.doClick(); // programmatically click submit
                }
            }
        });

        frame.add(new JLabel("Input:"));
        frame.add(inputField);
        frame.add(submitBtn);
        frame.add(clearBtn);
        frame.add(feedbackLabel);
        frame.add(countLabel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Swing: all UI creation must be on the EDT
        // invokeLater posts to EDT's queue — returns immediately
        boolean headless = GraphicsEnvironment.isHeadless();
        if (headless) {
            System.out.println("=== Event Handling Concepts (headless mode) ===");
            System.out.println("Listener types:");
            System.out.println("  1. Named class  — implement ActionListener, reusable");
            System.out.println("  2. Lambda       — ActionListener e -> { ... } (Java 8+)");
            System.out.println("  3. KeyAdapter   — anonymous subclass, override only needed methods");
            System.out.println("EDT rule: SwingUtilities.invokeLater(runnable)");
            System.out.println("  ALL Swing calls go through the EDT (Event Dispatch Thread).");
            System.out.println("  Non-EDT Swing calls → random painting bugs, data races.");
            System.out.println("SwingWorker: do slow work in background, update UI on EDT.");
        } else {
            SwingUtilities.invokeLater(() -> buildAndShowGUI());
        }

        // TRY THIS:
        // 1. Move feedback.setText() to a new Thread (not EDT). Does it crash?
        //    Use SwingUtilities.isEventDispatchThread() to log which thread you're on.
        // 2. Replace KeyAdapter anonymous class with a lambda + KeyListener directly.
        //    Why can't you use a plain lambda for KeyListener? (3 abstract methods)
        // 3. Add a JCheckBox "Dark mode" — when checked, change frame background to dark gray
        //    and label foreground to white. Attach an ItemListener via lambda.
    }
}
