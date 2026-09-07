/**
 * Chapter 13: Work on Your Swing — Layout Managers and Components
 *
 * Key Concepts:
 *  - Layout managers: FlowLayout, BorderLayout, GridLayout, BoxLayout, GridBagLayout
 *  - More Swing components: JCheckBox, JRadioButton, JComboBox, JList, JSlider, JSpinner
 *  - JPanel nesting — building complex UIs with nested panels
 *  - JScrollPane — scrollable containers
 *  - JTextArea — multi-line text
 *  - How to structure a Swing app for maintainability
 *
 * Note: This class creates a real GUI window. Close it to end the program.
 *
 * How to run:
 *   javac Chapter13Main.java
 *   java Chapter13Main
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chapter13Main {

    private JTextArea logArea;
    private JLabel statusLabel;

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // auto-scroll
    }

    private JPanel createLayoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Layout Manager Demo"));

        // -------------------------------------------------------
        // CONCEPT 1: FlowLayout — left-to-right, wraps on new line
        // -------------------------------------------------------
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        flowPanel.setBorder(BorderFactory.createTitledBorder("FlowLayout"));
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            JButton btn = new JButton("Btn " + i);
            btn.addActionListener(e -> log("FlowLayout Button " + num + " clicked"));
            flowPanel.add(btn);
        }

        // -------------------------------------------------------
        // CONCEPT 2: GridLayout — equal-sized cells in a grid
        // -------------------------------------------------------
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        gridPanel.setBorder(BorderFactory.createTitledBorder("GridLayout 2x3"));
        String[] cells = {"[0,0]", "[0,1]", "[0,2]", "[1,0]", "[1,1]", "[1,2]"};
        for (String cell : cells) {
            JLabel lbl = new JLabel(cell, JLabel.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            gridPanel.add(lbl);
        }

        JPanel topPanels = new JPanel(new GridLayout(1, 2, 5, 0));
        topPanels.add(flowPanel);
        topPanels.add(gridPanel);

        panel.add(topPanels, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createComponentsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Swing Components"));

        // -------------------------------------------------------
        // CONCEPT 3: JCheckBox — independent on/off toggle
        // -------------------------------------------------------
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkPanel.add(new JLabel("Toppings: "));
        String[] toppings = {"Cheese", "Pepperoni", "Mushrooms"};
        JCheckBox[] checkBoxes = new JCheckBox[toppings.length];
        for (int i = 0; i < toppings.length; i++) {
            checkBoxes[i] = new JCheckBox(toppings[i]);
            final String topping = toppings[i];
            checkBoxes[i].addActionListener(e ->
                log("Topping toggled: " + topping + " = " + ((JCheckBox)e.getSource()).isSelected()));
            checkPanel.add(checkBoxes[i]);
        }

        JButton orderBtn = new JButton("Order Pizza");
        orderBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("Pizza order: ");
            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) sb.append(cb.getText()).append(" ");
            }
            log(sb.toString().trim().equals("Pizza order:") ? "Plain pizza!" : sb.toString());
        });
        checkPanel.add(orderBtn);
        panel.add(checkPanel);

        // -------------------------------------------------------
        // CONCEPT 4: JRadioButton — mutually exclusive options
        // Must group them with ButtonGroup
        // -------------------------------------------------------
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(new JLabel("Size: "));
        ButtonGroup sizeGroup = new ButtonGroup();
        String[] sizes = {"Small", "Medium", "Large"};
        for (String size : sizes) {
            JRadioButton rb = new JRadioButton(size, size.equals("Medium"));
            rb.addActionListener(e -> log("Size selected: " + size));
            sizeGroup.add(rb);    // ensures mutual exclusivity
            radioPanel.add(rb);
        }
        panel.add(radioPanel);

        // -------------------------------------------------------
        // CONCEPT 5: JComboBox — dropdown selector
        // -------------------------------------------------------
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboPanel.add(new JLabel("Crust: "));
        JComboBox<String> crustCombo = new JComboBox<>(new String[]{"Thin", "Thick", "Stuffed"});
        crustCombo.addActionListener(e ->
            log("Crust selected: " + crustCombo.getSelectedItem()));
        comboPanel.add(crustCombo);
        panel.add(comboPanel);

        // -------------------------------------------------------
        // CONCEPT 6: JSlider — value within a range
        // -------------------------------------------------------
        JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sliderPanel.add(new JLabel("Spice level: "));
        JSlider spiceSlider = new JSlider(0, 10, 5);
        spiceSlider.setMajorTickSpacing(2);
        spiceSlider.setMinorTickSpacing(1);
        spiceSlider.setPaintTicks(true);
        spiceSlider.setPaintLabels(true);
        JLabel spiceValue = new JLabel("5");
        spiceSlider.addChangeListener(e -> {
            int val = spiceSlider.getValue();
            spiceValue.setText(String.valueOf(val));
            if (!spiceSlider.getValueIsAdjusting()) {
                log("Spice level set to: " + val);
            }
        });
        sliderPanel.add(spiceSlider);
        sliderPanel.add(spiceValue);
        panel.add(sliderPanel);

        // -------------------------------------------------------
        // CONCEPT 7: JSpinner — increment/decrement with spinner
        // -------------------------------------------------------
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.add(new JLabel("Quantity: "));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        quantitySpinner.addChangeListener(e ->
            log("Quantity: " + quantitySpinner.getValue()));
        spinnerPanel.add(quantitySpinner);
        panel.add(spinnerPanel);

        return panel;
    }

    public void buildAndShow() {
        JFrame frame = new JFrame("Chapter 13 - Swing Components & Layouts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 600);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Pizza Order System - Layout & Components Demo", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        // Center — layout demo + components demo side by side
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(createLayoutPanel());
        centerPanel.add(createComponentsPanel());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // -------------------------------------------------------
        // CONCEPT 8: JTextArea + JScrollPane — multi-line scrollable text
        // -------------------------------------------------------
        logArea = new JTextArea(6, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setText("Event log:\n");
        JScrollPane scrollPane = new JScrollPane(logArea); // wrap in scroll pane
        scrollPane.setBorder(BorderFactory.createTitledBorder("Event Log (JTextArea + JScrollPane)"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        log("GUI started. Try the controls above.");
    }

    public static void main(String[] args) {
        System.out.println("=== Chapter 13: Work on Your Swing ===");
        System.out.println("\nLayout Managers:");
        System.out.println("  FlowLayout   — left to right, wraps to next line");
        System.out.println("  BorderLayout — NORTH/SOUTH/EAST/WEST/CENTER regions");
        System.out.println("  GridLayout   — equal-size cells in rows/columns");
        System.out.println("  BoxLayout    — horizontal or vertical stack");
        System.out.println("  GridBagLayout— most flexible, most complex");

        System.out.println("\nSwing Components in this demo:");
        System.out.println("  JCheckBox    — independent boolean toggle");
        System.out.println("  JRadioButton + ButtonGroup — mutually exclusive choice");
        System.out.println("  JComboBox    — dropdown selection");
        System.out.println("  JSlider      — range value with tick marks");
        System.out.println("  JSpinner     — increment/decrement value");
        System.out.println("  JTextArea    — multi-line text display/input");
        System.out.println("  JScrollPane  — scroll wrapper for any component");

        System.out.println("\nOpening GUI window...");

        SwingUtilities.invokeLater(() -> new Chapter13Main().buildAndShow());

        // -------------------------------------------------------
        // EXERCISE
        // -------------------------------------------------------
        System.out.println("\n=== EXERCISE ===");
        System.out.println("Build a simple loan calculator GUI:");
        System.out.println("  - JSpinner or JTextField for: loan amount, interest rate, years");
        System.out.println("  - JButton 'Calculate' that computes monthly payment");
        System.out.println("    Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]");
        System.out.println("    where P=principal, r=monthly rate (annual/12/100), n=months");
        System.out.println("  - JLabel to show the result formatted as currency");
        System.out.println("  - Use GridLayout or GridBagLayout for the form");
    }
}
