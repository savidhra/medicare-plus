package lk.medicare.ui;

import lk.medicare.dao.AppointmentDao;
import lk.medicare.model.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientAppointmentsPanel extends JPanel {

    private final int patientId;
    private final DefaultTableModel model;
    private final JTable table;

    public PatientAppointmentsPanel(int patientId) {
        this.patientId = patientId;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // 1. Header
        JLabel title = new JLabel("My Appointment History", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // 2. Table
        model = new DefaultTableModel(
                new Object[]{"ID", "Doctor", "Date", "Time", "Status", "Reason"},
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
        JButton btnCancel = new JButton("Cancel Selected");

        south.add(btnRefresh);
        south.add(btnCancel);
        add(south, BorderLayout.SOUTH);

        // 4. Events
        btnRefresh.addActionListener(e -> loadAppointments());
        btnCancel.addActionListener(e -> cancelSelected());

        // Initial Load
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            model.setRowCount(0);
            List<Appointment> list = new AppointmentDao().getAppointmentsByPatient(patientId);

            for (Appointment a : list) {
                model.addRow(new Object[]{
                        a.getAppointmentId(),
                        a.getDoctorName(), // Shows "Doctor #ID" if name not joined
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

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int apptId = Integer.parseInt(model.getValueAt(row, 0).toString());

        if (JOptionPane.showConfirmDialog(this, "Cancel this appointment?") == JOptionPane.YES_OPTION) {
            try {
                // 1. Get details to find Doctor ID
                lk.medicare.model.Appointment appt = new AppointmentDao().getAppointment(apptId);

                // 2. Perform Cancel
                new AppointmentDao().cancelAppointment(apptId);

                // 3. SEND NOTIFICATION (Requirement 8)
                if(appt != null) {
                    // Notify Doctor
                    new lk.medicare.dao.NotificationDao().send("DOCTOR", appt.getDoctorId(),
                            "Cancellation: Patient " + appt.getPatientName() + " has cancelled appointment #" + apptId);
                }

                JOptionPane.showMessageDialog(this, "Appointment Cancelled.");
                loadAppointments();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}