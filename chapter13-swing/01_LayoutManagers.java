/**
 * CONCEPT: Layout Managers — how Swing positions components
 *
 * Swing uses layout managers rather than fixed pixel positions.
 * This makes UIs resizable and look correct on different screen sizes.
 *
 * Key layout managers:
 *   FlowLayout    — left to right, wraps on next line (default for JPanel)
 *   BorderLayout  — 5 regions: NORTH/SOUTH/EAST/WEST/CENTER (default for JFrame)
 *   GridLayout    — equal-size cells in rows/cols
 *   GridBagLayout — flexible grid, most powerful (also most complex)
 *   BoxLayout     — single row or column (respects preferred sizes)
 *   CardLayout    — show one panel at a time (tabs, wizards)
 *
 * Why this matters:
 *   - Most real-world panels nest layouts (outer BorderLayout, inner GridLayout, etc.)
 *   - setLayout(null) + setBounds() is a red flag — brittle at different DPIs
 *   - Understanding preferred/minimum/maximum size helps debug layout issues
 *
 * NOTE: Opens a real Swing window; runs headless demo if no display available.
 */
import javax.swing.*;
import java.awt.*;

class LayoutManagers {

    static JPanel buildBorderLayoutDemo() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("BorderLayout"));
        p.add(new JButton("NORTH"),  BorderLayout.NORTH);
        p.add(new JButton("SOUTH"),  BorderLayout.SOUTH);
        p.add(new JButton("EAST"),   BorderLayout.EAST);
        p.add(new JButton("WEST"),   BorderLayout.WEST);
        JTextArea center = new JTextArea("CENTER\n(takes remaining space)");
        center.setEditable(false);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    static JPanel buildGridLayoutDemo() {
        JPanel p = new JPanel(new GridLayout(3, 3, 4, 4));
        p.setBorder(BorderFactory.createTitledBorder("GridLayout 3x3"));
        for (int i = 1; i <= 9; i++) p.add(new JButton(String.valueOf(i)));
        return p;
    }

    static JPanel buildBoxLayoutDemo() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("BoxLayout (vertical)"));
        p.add(new JLabel("Label 1"));
        p.add(Box.createVerticalStrut(8)); // fixed gap
        p.add(new JTextField("Input", 15));
        p.add(Box.createVerticalStrut(8));
        JButton btn = new JButton("Button");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(btn);
        p.add(Box.createVerticalGlue()); // pushes content up
        return p;
    }

    static void buildAndShow() {
        JFrame frame = new JFrame("Layout Managers Demo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new GridLayout(1, 3, 10, 10));
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        frame.add(buildBorderLayoutDemo());
        frame.add(buildGridLayoutDemo());
        frame.add(buildBoxLayoutDemo());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("=== Layout Managers (headless) ===");
            System.out.println("FlowLayout:    left-to-right, wraps. Good for toolbars.");
            System.out.println("BorderLayout:  5 regions. Good for main app frame.");
            System.out.println("GridLayout:    equal cells. Good for calculators, keypads.");
            System.out.println("BoxLayout:     1D row or col. Good for form fields.");
            System.out.println("GridBagLayout: fully flexible grid. Use when nothing else works.");
            System.out.println("CardLayout:    one panel visible at a time. Good for wizards.");
        } else {
            SwingUtilities.invokeLater(LayoutManagers::buildAndShow);
        }

        // TRY THIS:
        // 1. Nest a GridLayout(2,2) panel inside a BorderLayout's CENTER.
        //    This is the most common real-world pattern for forms.
        // 2. Use GridBagLayout with GridBagConstraints to create a login form:
        //    label+field pairs aligned on a grid. Set weightx/weighty for stretching.
        // 3. Use CardLayout for a 3-step wizard: Next/Back buttons switch panels.
        //    CardLayout.show(parent, cardName) switches the visible card.
    }
}
