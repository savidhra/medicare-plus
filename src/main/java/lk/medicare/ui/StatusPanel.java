package lk.medicare.ui;

import lk.medicare.dao.AppointmentDao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.Vector;

public class StatusPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public StatusPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // 1. Header (Matching Blue Style)
        JLabel title = new JLabel("Today's Appointments", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(33, 150, 243)); // Professional Blue
        add(title, BorderLayout.NORTH);

        // 2. Table Setup
        model = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Time", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Action Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnCheckIn = new JButton("Mark Checked In");
        JButton btnComplete = new JButton("Mark Completed");
        JButton btnCancel = new JButton("Cancel");

        south.add(btnRefresh);
        south.add(btnCheckIn);
        south.add(btnComplete);
        south.add(btnCancel);
        add(south, BorderLayout.SOUTH);

        // 4. Events
        btnRefresh.addActionListener(e -> loadData());
        btnCheckIn.addActionListener(e -> update("CHECKED_IN"));
        btnComplete.addActionListener(e -> update("COMPLETED"));
        btnCancel.addActionListener(e -> update("CANCELLED"));

        // Auto-load
        loadData();
    }

    private void update(String status) {
        int row = table.getSelectedRow();
        if(row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
            return;
        }

        int apptId = Integer.parseInt(model.getValueAt(row, 0).toString());

        try {
            // 1. Get Appointment details FIRST (so we know who to notify)
            lk.medicare.model.Appointment appt = new AppointmentDao().getAppointment(apptId);

            // 2. Update the status
            new AppointmentDao().updateStatus(apptId, status);

            // 3. SEND NOTIFICATION (Requirement 7)
            if(appt != null) {
                String msg = "Status Update: Your appointment with " + appt.getDoctorName() + " is now " + status;
                if(status.equals("DELAYED")) {
                    msg = "URGENT: Your appointment with " + appt.getDoctorName() + " has been DELAYED. Please check with front desk.";
                }
                new lk.medicare.dao.NotificationDao().send("PATIENT", appt.getPatientId(), msg);
            }

            loadData();
            JOptionPane.showMessageDialog(this, "Status updated & Patient Notified.");

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadData() {
        try {
            model.setRowCount(0); // Clear table

            // Calls the DAO method that returns a ResultSet
            ResultSet rs = new AppointmentDao().getTodayAppointments();

            while(rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("AppointmentId"));
                row.add(rs.getString("FirstName"));
                row.add(rs.getString("LastName"));
                row.add(rs.getTimestamp("AppointmentDateTime"));
                row.add(rs.getString("Status"));
                model.addRow(row);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}