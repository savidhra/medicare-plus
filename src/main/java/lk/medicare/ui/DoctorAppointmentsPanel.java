package lk.medicare.ui;

import lk.medicare.dao.AppointmentDao;
import lk.medicare.model.Appointment;
import lk.medicare.dao.NotificationDao; // Import NotificationDao

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorAppointmentsPanel extends JPanel {

    private final int doctorId;
    private final DefaultTableModel model;
    private final JTable table;

    public DoctorAppointmentsPanel(int doctorId) {
        this.doctorId = doctorId;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // 1. Header
        JLabel title = new JLabel("My Appointments", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // 2. Table
        model = new DefaultTableModel(
                new Object[]{"ID", "Patient", "Date", "Time", "Status", "Reason"},
                0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnCompleted = new JButton("Mark Completed");
        JButton btnDelayed = new JButton("Mark Delayed"); // <--- NEW BUTTON
        JButton btnCancelled = new JButton("Mark Cancelled");

        // Styling (Optional)
        btnDelayed.setForeground(new Color(255, 140, 0)); // Orange color for warning

        south.add(btnRefresh);
        south.add(btnCompleted);
        south.add(btnDelayed);    // Add to layout
        south.add(btnCancelled);
        add(south, BorderLayout.SOUTH);

        // 4. Events
        btnRefresh.addActionListener(e -> loadAppointments());
        btnCompleted.addActionListener(e -> updateSelectedStatus("COMPLETED"));
        btnDelayed.addActionListener(e -> updateSelectedStatus("DELAYED")); // <--- NEW ACTION
        btnCancelled.addActionListener(e -> updateSelectedStatus("CANCELLED"));

        // Initial Load
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            model.setRowCount(0);
            List<Appointment> list = new AppointmentDao().getAppointmentsByDoctor(doctorId);

            for (Appointment a : list) {
                model.addRow(new Object[]{
                        a.getAppointmentId(),
                        a.getPatientName(),
                        a.getDate(),
                        a.getTime(),
                        a.getStatus(),
                        a.getReason()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage());
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int apptId = Integer.parseInt(model.getValueAt(row, 0).toString());

        try {
            AppointmentDao dao = new AppointmentDao();

            // 1. Update Status
            dao.updateAppointmentStatus(apptId, newStatus);

            // 2. Notify Patient (Requirement 7)
            Appointment appt = dao.getAppointment(apptId);
            if(appt != null) {
                String msg = "Update: Your appointment #" + apptId + " is now " + newStatus;
                if(newStatus.equals("DELAYED")) {
                    msg = "URGENT: Your appointment with " + appt.getDoctorName() + " is running late (DELAYED).";
                }
                new NotificationDao().send("PATIENT", appt.getPatientId(), msg);
            }

            JOptionPane.showMessageDialog(this, "Appointment marked as " + newStatus);
            loadAppointments(); // Refresh table
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}