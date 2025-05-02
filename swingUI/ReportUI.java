import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportUI extends JFrame {

    private final JCheckBox[] queryCheckboxes = new JCheckBox[6];
    private final String[] queryNames = {"Query1", "Query2", "Query3", "Query4", "Query5", "Query6"};
    private final String[] queries = {
        "SELECT * FROM table1",
        "SELECT * FROM table2",
        "SELECT * FROM table3",
        "SELECT * FROM table4",
        "SELECT * FROM table5",
        "SELECT * FROM table6"
    };

    private final Connection connection;

    public ReportUI(Connection connection) {
        this.connection = connection;
        setTitle("SQL Report Generator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 5, 5));
        for (int i = 0; i < queryCheckboxes.length; i++) {
            queryCheckboxes[i] = new JCheckBox(queryNames[i]);
            panel.add(queryCheckboxes[i]);
        }

        JButton runButton = new JButton("Run Queries");
        runButton.addActionListener(this::handleRun);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this::handleLogout);

        panel.add(runButton);
        panel.add(logoutButton);
        add(panel);
    }

    private void handleRun(ActionEvent e) {
        Map<String, String> selectedQueries = new LinkedHashMap<>();
        for (int i = 0; i < queryCheckboxes.length; i++) {
            if (queryCheckboxes[i].isSelected()) {
                selectedQueries.put(queryNames[i], queries[i]);
            }
        }

        if (selectedQueries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one query.");
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            for (Map.Entry<String, String> entry : selectedQueries.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(entry.getValue())) {
                    ResultSetMetaData meta = rs.getMetaData();
                    Row header = sheet.createRow(0);
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        header.createCell(i - 1).setCellValue(meta.getColumnName(i));
                    }
                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                            row.createCell(i - 1).setCellValue(rs.getString(i));
                        }
                    }
                }
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Excel File");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileOutputStream out = new FileOutputStream(chooser.getSelectedFile() + ".xlsx")) {
                    workbook.write(out);
                }
                JOptionPane.showMessageDialog(this, "Report generated successfully.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while generating report.");
        }
    }

    private void handleLogout(ActionEvent e) {
        CredentialManager.deleteStoredCredentials();
        JOptionPane.showMessageDialog(this, "Logged out and credentials cleared.");
        dispose();
    }
}
