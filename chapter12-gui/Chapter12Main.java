/**
 * Chapter 12: Getting a GUI — Swing Basics and Event Handling
 *
 * Key Concepts:
 *  - Swing framework basics: JFrame, JButton, JLabel, JPanel
 *  - Event-driven programming model
 *  - ActionListener interface and event handling
 *  - Anonymous classes (traditional event handling)
 *  - Lambda expressions for event handling (Java 8+, modern approach)
 *  - Inner classes in context of GUI
 *  - Layout managers: FlowLayout basics
 *
 * Note: This class creates a real GUI window. Close it to end the program.
 *
 * How to run:
 *   javac Chapter12Main.java
 *   java Chapter12Main
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chapter12Main {

    // -------------------------------------------------------
    // CONCEPT 1: Event-Driven Programming
    //
    // GUI programs work differently from top-to-bottom programs.
    // The program:
    //   1. Creates the GUI (widgets, layout)
    //   2. Registers event listeners (who handles button clicks, etc.)
    //   3. Starts the event loop (waits for user input)
    //
    // When user clicks a button -> ActionEvent is created ->
    // ActionListener.actionPerformed() is called
    //
    // Key interface: ActionListener
    //   void actionPerformed(ActionEvent e)
    // -------------------------------------------------------

    // -------------------------------------------------------
    // CONCEPT 2: Inner class implementing ActionListener (traditional approach)
    //
    // Inner class = class defined INSIDE another class.
    // Non-static inner class has access to outer class's fields.
    // -------------------------------------------------------
    class ColorButtonListener implements ActionListener {
        private JLabel label;
        private String colorName;
        private Color color;

        public ColorButtonListener(JLabel label, String colorName, Color color) {
            this.label = label;
            this.colorName = colorName;
            this.color = color;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            label.setForeground(color);
            label.setText("Color changed to " + colorName + "!");
        }
    }

    // -------------------------------------------------------
    // The main GUI class — builds and shows the window
    // -------------------------------------------------------
    private JFrame frame;
    private JLabel statusLabel;
    private int clickCount = 0;

    public void buildGUI() {

        // -------------------------------------------------------
        // CONCEPT 3: JFrame — the main application window
        // -------------------------------------------------------
        frame = new JFrame("Chapter 12 - GUI Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null); // center on screen

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // -------------------------------------------------------
        // CONCEPT 4: JLabel — displays text
        // -------------------------------------------------------
        JLabel titleLabel = new JLabel("Head First Java - GUI Demo", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // -------------------------------------------------------
        // Center panel — shows various event handling techniques
        // -------------------------------------------------------
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        // STATUS label — updated by button clicks
        statusLabel = new JLabel("Click any button to see events in action!", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        centerPanel.add(statusLabel);

        // -------------------------------------------------------
        // CONCEPT 5a: Lambda for ActionListener (Java 8+ preferred approach)
        //
        // ActionListener is a functional interface (one abstract method).
        // Lambda (e -> ...) replaces the need for an anonymous class.
        // Much more concise!
        // -------------------------------------------------------
        JPanel buttonPanel1 = new JPanel(new FlowLayout());

        JButton countButton = new JButton("Click Counter");
        countButton.addActionListener(e -> {
            // Lambda captures `this` and `clickCount` from enclosing scope
            clickCount++;
            statusLabel.setText("Clicked " + clickCount + " time" + (clickCount != 1 ? "s" : "") + "!");
        });
        buttonPanel1.add(countButton);

        JButton resetButton = new JButton("Reset Counter");
        resetButton.addActionListener(e -> {
            clickCount = 0;
            statusLabel.setText("Counter reset to 0.");
        });
        buttonPanel1.add(resetButton);
        centerPanel.add(buttonPanel1);

        // -------------------------------------------------------
        // CONCEPT 5b: Anonymous class (classic approach, pre-Java 8)
        //
        // An anonymous class is a class defined AND instantiated in one expression.
        // No name — used when you need an object that implements an interface
        // but you only use it once.
        // -------------------------------------------------------
        JPanel buttonPanel2 = new JPanel(new FlowLayout());

        JButton anonymousBtn = new JButton("Anonymous Class");
        anonymousBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Anonymous class handled this event!");
            }
        });
        buttonPanel2.add(anonymousBtn);

        // -------------------------------------------------------
        // CONCEPT 5c: Named inner class (ColorButtonListener defined above)
        // -------------------------------------------------------
        JButton innerClassBtn = new JButton("Inner Class (Blue)");
        innerClassBtn.addActionListener(new ColorButtonListener(statusLabel, "Blue", Color.BLUE));
        buttonPanel2.add(innerClassBtn);

        centerPanel.add(buttonPanel2);

        // -------------------------------------------------------
        // CONCEPT 6: JTextField — text input
        // -------------------------------------------------------
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel inputLabel = new JLabel("Type something:");
        JTextField textField = new JTextField(15);
        JButton echoButton = new JButton("Echo");

        echoButton.addActionListener(e -> {
            String text = textField.getText();
            if (text.isBlank()) {
                statusLabel.setText("(field is empty)");
            } else {
                statusLabel.setText("You typed: \"" + text + "\"");
                statusLabel.setForeground(Color.DARK_GRAY);
                textField.setText(""); // clear the field
            }
        });

        // Also handle pressing Enter in the text field
        textField.addActionListener(e -> echoButton.doClick());

        inputPanel.add(inputLabel);
        inputPanel.add(textField);
        inputPanel.add(echoButton);
        centerPanel.add(inputPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // -------------------------------------------------------
        // CONCEPT 7: Menus
        // -------------------------------------------------------
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e ->
            JOptionPane.showMessageDialog(frame,
                "Chapter 12 GUI Demo\nHead First Java Study",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);
        frame.add(mainPanel);
    }

    public void show() {
        // Swing components must be created and modified on the Event Dispatch Thread (EDT)
        // SwingUtilities.invokeLater ensures this
        SwingUtilities.invokeLater(() -> {
            buildGUI();
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        System.out.println("=== Chapter 12: Getting a GUI ===");
        System.out.println("Key concepts demonstrated in the GUI window:");
        System.out.println("  1. JFrame — main window");
        System.out.println("  2. JLabel, JButton, JTextField — basic widgets");
        System.out.println("  3. Lambda for ActionListener (modern, concise)");
        System.out.println("  4. Anonymous class for ActionListener (classic)");
        System.out.println("  5. Named inner class for ActionListener");
        System.out.println("  6. JMenuBar and menus");
        System.out.println("  7. JOptionPane — simple dialog boxes");
        System.out.println("\nOpening GUI window...\n");

        // -------------------------------------------------------
        // CONCEPT 8: Comparison of listener approaches
        // -------------------------------------------------------
        System.out.println("--- Three Ways to Handle Events ---");

        System.out.println("\n1. Lambda (simplest, modern Java 8+):");
        System.out.println("   button.addActionListener(e -> statusLabel.setText(\"clicked!\"));");

        System.out.println("\n2. Anonymous class (classic, verbose):");
        System.out.println("   button.addActionListener(new ActionListener() {");
        System.out.println("       @Override");
        System.out.println("       public void actionPerformed(ActionEvent e) {");
        System.out.println("           statusLabel.setText(\"clicked!\");");
        System.out.println("       }");
        System.out.println("   });");

        System.out.println("\n3. Named inner class (reusable, testable):");
        System.out.println("   class MyListener implements ActionListener { ... }");
        System.out.println("   button.addActionListener(new MyListener(label, \"Blue\", Color.BLUE));");

        System.out.println("\n>>> Lambda is preferred today. Use named inner class when");
        System.out.println("    the handler is complex or needs to be reused.");

        new Chapter12Main().show();

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Extend the GUI with:");
        System.out.println("  - A 'Color Changer' button that changes the window background randomly");
        System.out.println("  - A slider (JSlider) that controls font size of the status label");
        System.out.println("  - A 'Clear' button that resets status label text and counter");
        System.out.println("  Hint: JSlider fires ChangeEvents, not ActionEvents");
        System.out.println("        Use slider.addChangeListener(e -> { int val = slider.getValue(); ... })");
    }
}
