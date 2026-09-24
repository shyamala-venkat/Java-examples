/**
 * EXERCISE — Chapter 13: Work on Your Swing
 *
 * Problem 1: Login form with validation
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
 *
 * Problem 2: SQL-style Injection — Find the Exploit, Then Fix It
 * -------------------------------------------------------------------
 * VulnerableUserStore below simulates a DB lookup by building a query STRING
 * via concatenation — the classic root cause of SQL injection. It's given
 * fully implemented as the vulnerable baseline; don't change it.
 *
 * 1. VulnerableUserStore.findByCredentials(username, password): builds
 *      "SELECT * FROM users WHERE username='" + username + "' AND password='" + password + "'"
 *    then "runs" it against an in-memory user list by checking whether the raw
 *    string would structurally bypass the password check (given below).
 *
 * 2. Exploit it: call findByCredentials("admin' --", "wrong-password"). The `--`
 *    comments out everything after it in the built query — including the
 *    password check — so it logs in as admin with NO valid password. Print
 *    whether the exploit succeeded.
 *
 * 3. Fix it: implement SafeUserStore.findByCredentials(username, password) that
 *    NEVER builds a query string from user input — compare fields directly
 *    (this is what a parameterized query does under the hood: the input is
 *    data, never part of the command). Also reject usernames containing
 *    anything outside [a-zA-Z0-9_] BEFORE the lookup even runs (input
 *    allow-listing — defense in depth, not a replacement for parameterization).
 *
 * 4. Re-run the same exploit attempt against SafeUserStore; confirm it
 *    correctly rejects the injection attempt and does NOT log in as admin.
 */
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Chapter13Exercise {

    static class FormValidator {
        List<String> validate(String username, String password) {
            List<String> errors = new ArrayList<>();
            // TODO: validate username (3-20 chars, alphanumeric)
            // TODO: validate password (min 8 chars, at least one digit)
            return errors;
        }
    }

    // ========= Problem 2: given vulnerable baseline — do not fix this class =========
    static class VulnerableUserStore {
        record User(String username, String password) {}
        private final List<User> users = List.of(
            new User("admin", "S3curePass!"),
            new User("alice", "alicepw123")
        );

        // Simulates running a concatenated SQL string against the DB.
        // A real DB would execute this string; here we approximate the same
        // vulnerability by checking whether an injected "--" comments out the
        // password clause, which is exactly what happens in a real SQL engine.
        boolean findByCredentials(String username, String password) {
            String query = "SELECT * FROM users WHERE username='" + username +
                "' AND password='" + password + "'";
            System.out.println("  [query] " + query);

            int commentIdx = username.indexOf("--");
            if (commentIdx >= 0) {
                String injectedUsername = username.substring(0, commentIdx);
                return users.stream().anyMatch(u -> u.username().equals(injectedUsername));
            }
            return users.stream().anyMatch(u ->
                u.username().equals(username) && u.password().equals(password));
        }
    }

    // ========= Problem 2: TODO — fix with parameterized-style lookup + allow-listing =========
    static class SafeUserStore {
        // TODO: same user list as VulnerableUserStore

        boolean findByCredentials(String username, String password) {
            // TODO: reject username if it contains anything outside [a-zA-Z0-9_]
            // TODO: compare fields directly — never build a query string from input
            return false;
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
            SwingUtilities.invokeLater(Chapter13Exercise::buildAndShow);
        }

        System.out.println("\n=== Problem 2: SQL Injection Exploit ===");
        VulnerableUserStore vulnerable = new VulnerableUserStore();
        boolean exploited = vulnerable.findByCredentials("admin' --", "wrong-password");
        System.out.println("Logged in as admin without password? " + exploited +
            " (expected true — VULNERABLE)");

        System.out.println("\n=== Problem 2: SafeUserStore (fixed) ===");
        SafeUserStore safe = new SafeUserStore();
        boolean blocked = safe.findByCredentials("admin' --", "wrong-password");
        System.out.println("Logged in as admin without password? " + blocked +
            " (expected false — exploit blocked)");
    }

    static void test(FormValidator v, String user, String pass, String scenario) {
        List<String> errors = v.validate(user, pass);
        System.out.printf("  %-30s → %s%n", scenario,
            errors.isEmpty() ? "PASS" : "FAIL: " + errors);
    }
}
