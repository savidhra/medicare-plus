package lk.medicare.ui;

import lk.medicare.dao.AssignmentDao;
import lk.medicare.dao.AppointmentDao;
import lk.medicare.dao.PatientDao;
import lk.medicare.db.Db;
import lk.medicare.model.Patient;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

public class AutoAssignPanel extends JPanel {

    private JComboBox<String> cboSpec;
    private JComboBox<PatientItem> cboPatient; // NEW: Dropdown for Patients
    private JLabel lblResult;
    private JButton btnBookNow;
    private int foundDoctorId = -1;

    public AutoAssignPanel() {
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // 1. Header
        JLabel title = new JLabel("Smart Doctor Allocation", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(new Color(33, 150, 243));
        add(title, BorderLayout.NORTH);

        // 2. Center Form
        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // -- Row 0: Specialization --
        cboSpec = new JComboBox<>();
        loadSpecializations();

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        center.add(new JLabel("Required Specialization: "), gc);

        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        center.add(cboSpec, gc);

        // -- Row 1: Patient Selection (NEW) --
        cboPatient = new JComboBox<>();
        loadPatients(); // Load list on startup

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        center.add(new JLabel("Select Patient: "), gc);

        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        center.add(cboPatient, gc);

        // -- Row 2: Find Button --
        JButton btnFind = new JButton("Find Best Doctor");
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 0; gc.anchor = GridBagConstraints.EAST;
        center.add(btnFind, gc);

        add(center, BorderLayout.CENTER);

        // 3. Footer (Result & Book)
        JPanel south = new JPanel(new GridLayout(2,1, 5, 5));
        lblResult = new JLabel("Select criteria and click Find.", SwingConstants.CENTER);
        lblResult.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnBookNow = new JButton("Assign & Book Urgent Slot");
        btnBookNow.setEnabled(false);
        btnBookNow.setBackground(new Color(0, 128, 0));
        btnBookNow.setForeground(Color.WHITE);

        south.add(lblResult);
        south.add(btnBookNow);
        add(south, BorderLayout.SOUTH);

        // 4. Actions
        btnFind.addActionListener(e -> findDoctor());

        // Refresh patients button (optional helper)
        cboPatient.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) { /* could reload here */ }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });

        btnBookNow.addActionListener(e -> bookUrgentAssignment());
    }

    private void findDoctor() {
        try {
            foundDoctorId = new AssignmentDao().findBestDoctor((String)cboSpec.getSelectedItem());

            if (foundDoctorId != -1) {
                lblResult.setText("Best Match: Doctor ID " + foundDoctorId + " (Low Workload)");
                lblResult.setForeground(new Color(0, 100, 0));
                btnBookNow.setEnabled(true);
            } else {
                lblResult.setText("No doctors found for this specialization.");
                lblResult.setForeground(Color.RED);
                btnBookNow.setEnabled(false);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void bookUrgentAssignment() {
        PatientItem selectedPatient = (PatientItem) cboPatient.getSelectedItem();

        if (selectedPatient == null) {
            JOptionPane.showMessageDialog(this, "Please select a patient first.");
            return;
        }

        try {
            // Book for NOW + 30 mins
            new AppointmentDao().bookAppointment(
                    selectedPatient.id,
                    foundDoctorId,
                    1,
                    LocalDateTime.now().plusMinutes(30),
                    "Auto-Assigned Urgent Case",
                    "HIGH"
            );

            JOptionPane.showMessageDialog(this, "Successfully Assigned Doctor to " + selectedPatient.name);

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // --- Loaders ---

    private void loadSpecializations() {
        try (Connection c = Db.get(); ResultSet rs = c.createStatement().executeQuery("SELECT DISTINCT Specialization FROM Doctor")) {
            while(rs.next()) cboSpec.addItem(rs.getString(1));
        } catch(Exception e) {}
    }

    private void loadPatients() {
        cboPatient.removeAllItems();
        try {
            // Reusing existing PatientDao method
            List<Patient> list = new PatientDao().getAll();
            for(Patient p : list) {
                // Combine Name for display
                String fullName = p.firstName + " " + p.lastName;
                cboPatient.addItem(new PatientItem(p.patientId, fullName));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Helper class for Dropdown
    private record PatientItem(int id, String name) {
        @Override public String toString() { return name + " (ID: " + id + ")"; }
    }
}