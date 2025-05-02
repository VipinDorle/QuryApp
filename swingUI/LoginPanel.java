import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JCheckBox rememberPasswordBox = new JCheckBox("Remember password");

    private String username;
    private String password;

    public LoginPanel(Runnable onLoginSuccess) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy++;
        add(rememberPasswordBox, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::handleLogin);

        gbc.gridy++;
        add(loginButton, gbc);
    }

    private void handleLogin(ActionEvent e) {
        username = usernameField.getText();
        password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try {
            if (rememberPasswordBox.isSelected()) {
                CredentialManager.saveEncryptedPassword(password);
            }
            SwingUtilities.getWindowAncestor(this).dispose(); // Close login window
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save password securely.");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
