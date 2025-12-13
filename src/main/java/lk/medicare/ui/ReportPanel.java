package lk.medicare.ui;

import lk.medicare.dao.ReportDao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.Vector;

public class ReportPanel extends JPanel {

    public ReportPanel() {
        setLayout(new BorderLayout());

        // Header
        JLabel title = new JLabel("Clinic Analytics & Reports", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Tabs for each report type
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // TAB 1: Status Overview (Your original report)
        tabs.addTab("Status Overview", createReportTab(
                "getAppointmentStats",
                new String[]{"Status", "Count"}
        ));

        // TAB 2: Monthly Volumes (Requirement 6a)
        tabs.addTab("Monthly Volume", createReportTab(
                "getMonthlyVolumes",
                new String[]{"Month", "Total Appointments"}
        ));

        // TAB 3: Doctor Performance (Requirement 6b)
        tabs.addTab("Doctor Performance", createReportTab(
                "getDoctorPerformance",
                new String[]{"ID", "First Name", "Last Name", "Specialization", "Total Appts"}
        ));

        // TAB 4: Patient Visits (Requirement 6c)
        tabs.addTab("Patient Activity", createReportTab(
                "getPatientVisitSummary",
                new String[]{"ID", "First Name", "Last Name", "Total Visits"}
        ));

        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Helper to create a tab with a Table and a Refresh button.
     * This avoids writing the same table code 4 times.
     */
    private JPanel createReportTab(String methodName, String[] columnHeaders) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table Setup
        DefaultTableModel model = new DefaultTableModel(columnHeaders, 0);
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // Load Button
        JButton btnLoad = new JButton("Refresh Report");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnLoad);
        p.add(south, BorderLayout.SOUTH);

        // Action Listener
        btnLoad.addActionListener(e -> {
            try {
                model.setRowCount(0); // Clear old data
                ResultSet rs = null;
                ReportDao dao = new ReportDao();

                // Select the correct DAO method
                if (methodName.equals("getMonthlyVolumes")) {
                    rs = dao.getMonthlyVolumes();
                } else if (methodName.equals("getDoctorPerformance")) {
                    rs = dao.getDoctorPerformance();
                } else if (methodName.equals("getPatientVisitSummary")) {
                    rs = dao.getPatientVisitSummary();
                } else {
                    rs = dao.getAppointmentStats();
                }

                // Populate Table
                if (rs != null) {
                    int colCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        for (int i = 1; i <= colCount; i++) {
                            row.add(rs.getObject(i));
                        }
                        model.addRow(row);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading report: " + ex.getMessage());
            }
        });

        // Auto-load on startup
        btnLoad.doClick();

        return p;
    }
}