/**
 * EXERCISE — Chapter 13: Work on Your Swing
 *
 * Problem: Login form with validation
 * -------------------------------------------------------
 *
 * Build a Swing login form with:
 *   - Username field (JTextField)
 *   - Password field (JPasswordField — shows ****)
 *   - Login button
 *   - Status label (shows validation errors or success)
 *
 * Validation rules:
 *   - Username: 3-20 chars, letters and digits only
 *   - Password: min 8 chars, must contain at least one digit
 *
 * Layout: Use GridBagLayout for proper form alignment.
 *
 * Design pattern:
 *   - FormValidator class with validate(String username, String password) → List<String> errors
 *   - LoginForm class builds the UI and wires up validation
 *
 * NOTE: headless mode tests FormValidator without UI.
 */
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Exercise {

    static class FormValidator {
        List<String> validate(String username, String password) {
            List<String> errors = new ArrayList<>();
            // TODO: validate username (3-20 chars, alphanumeric)
            // TODO: validate password (min 8 chars, at least one digit)
            return errors;
        }
    }

    static void buildAndShow() {
        FormValidator validator = new FormValidator();
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(350, 220);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField  usernameField = new JTextField(15);
        JPasswordField passField  = new JPasswordField(15);
        JButton loginBtn          = new JButton("Login");
        JLabel  statusLabel       = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC));

        // Row 0: username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(usernameField, gbc);

        // Row 1: password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(passField, gbc);

        // Row 2: button
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(loginBtn, gbc);

        // Row 3: status
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passField.getPassword());
            List<String> errors = validator.validate(username, password);
            if (errors.isEmpty()) {
                statusLabel.setText("Login successful!");
                statusLabel.setForeground(new Color(0, 128, 0));
            } else {
                statusLabel.setText("<html>" + String.join("<br>", errors) + "</html>");
                statusLabel.setForeground(Color.RED);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        FormValidator v = new FormValidator();

        System.out.println("=== Validator Tests ===");
        test(v, "al",          "pass1234", "too short username");
        test(v, "alice_bob",   "pass1234", "invalid char in username");
        test(v, "alice",       "pass",     "password too short");
        test(v, "alice",       "password", "password no digit");
        test(v, "alice",       "pass1234", "valid — should pass");

        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(Exercise::buildAndShow);
        }
    }

    static void test(FormValidator v, String user, String pass, String scenario) {
        List<String> errors = v.validate(user, pass);
        System.out.printf("  %-30s → %s%n", scenario,
            errors.isEmpty() ? "PASS" : "FAIL: " + errors);
    }
}
