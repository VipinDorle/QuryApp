import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConfigLoader config = new ConfigLoader("config.properties");
                boolean isSso = config.isSsoEnabled();
                String server = config.getServerName();
                String db = config.getDatabaseName();

                String connectionUrl;
                Connection connection;

                if (isSso) {
                    connectionUrl = String.format(
                        "jdbc:sqlserver://%s;databaseName=%s;integratedSecurity=true;encrypt=true;trustServerCertificate=true;",
                        server, db
                    );
                    connection = DriverManager.getConnection(connectionUrl);
                    new ReportUI(connection);
                } else {
                    LoginPanel loginPanel = new LoginPanel(() -> {});
                    JFrame loginFrame = new JFrame("Login");
                    loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    loginFrame.add(loginPanel);
                    loginFrame.pack();
                    loginFrame.setLocationRelativeTo(null);
                    loginFrame.setVisible(true);

                    // Wait for login window to close before proceeding
                    loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            try {
                                String username = loginPanel.getUsername();
                                String password = loginPanel.getPassword();
                                if (password == null || password.isEmpty()) {
                                    password = CredentialManager.loadDecryptedPassword();
                                }

                                String url = String.format(
                                    "jdbc:sqlserver://%s;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                                    server, db
                                );
                                Connection conn = DriverManager.getConnection(url, username, password);
                                new ReportUI(conn);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Login failed. Please restart.");
                            }
                        }
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing app: " + ex.getMessage());
            }
        });
    }
}
