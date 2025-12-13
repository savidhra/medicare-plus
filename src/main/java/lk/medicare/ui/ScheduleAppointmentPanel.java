package lk.medicare.ui;

import lk.medicare.dao.AppointmentDao;
import lk.medicare.dao.NotificationDao;
import lk.medicare.db.Db;
import lk.medicare.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.List;

public class ScheduleAppointmentPanel extends JPanel {

    private final User currentPatient;

    private final JComboBox<BranchItem> cboBranch;
    private final JComboBox<String>     cboSpec;
    private final JComboBox<DoctorItem> cboDoctor;
    private final JSpinner              spDate;
    private final JComboBox<String>     cboSlot;
    private final JButton               btnBook;

    public ScheduleAppointmentPanel(User user) {
        this.currentPatient = user;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Schedule Appointment", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Branch:"), gc);

        cboBranch = new JComboBox<>(loadBranches().toArray(new BranchItem[0]));
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        form.add(cboBranch, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Specialization:"), gc);

        cboSpec = new JComboBox<>();
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        form.add(cboSpec, gc);

        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        form.add(new JLabel("Doctor:"), gc);

        cboDoctor = new JComboBox<>();
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1;
        form.add(cboDoctor, gc);

        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0;
        form.add(new JLabel("Date:"), gc);

        spDate = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1;
        form.add(spDate, gc);

        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0;
        form.add(new JLabel("Time slot:"), gc);

        cboSlot = new JComboBox<>();
        gc.gridx = 1; gc.gridy = 4; gc.weightx = 1;
        form.add(cboSlot, gc);

        add(form, BorderLayout.CENTER);

        btnBook = new JButton("Book Appointment");
        btnBook.addActionListener(e -> bookAppointment());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnBook);
        add(south, BorderLayout.SOUTH);

        cboBranch.addActionListener(e -> reloadSpecsAndDoctors());
        cboSpec.addActionListener(e -> reloadDoctorsOnly());
        cboDoctor.addActionListener(e -> reloadSlots());
        spDate.addChangeListener(e -> reloadSlots());

        reloadSpecsAndDoctors();
        reloadSlots();
    }

    private List<BranchItem> loadBranches() {
        List<BranchItem> list = new ArrayList<>();
        String sql = "SELECT BranchId, Name FROM Branch ORDER BY Name";
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BranchItem(rs.getInt("BranchId"), rs.getString("Name")));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    private List<String> loadSpecializations(int branchId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT Specialization FROM Doctor WHERE BranchId = ? ORDER BY Specialization";
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getString("Specialization"));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    private List<DoctorItem> loadDoctors(int branchId, String specialization) {
        List<DoctorItem> list = new ArrayList<>();
        String sql = "SELECT DoctorId, FirstName, LastName FROM Doctor WHERE BranchId = ? AND Specialization = ? ORDER BY FirstName";
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, specialization);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fullName = rs.getString("FirstName") + " " + rs.getString("LastName");
                    list.add(new DoctorItem(rs.getInt("DoctorId"), fullName));
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    private void reloadSpecsAndDoctors() {
        BranchItem b = (BranchItem) cboBranch.getSelectedItem();
        if (b == null) return;
        List<String> specs = loadSpecializations(b.id());
        cboSpec.setModel(new DefaultComboBoxModel<>(specs.toArray(new String[0])));
        reloadDoctorsOnly();
    }

    private void reloadDoctorsOnly() {
        BranchItem b = (BranchItem) cboBranch.getSelectedItem();
        String spec   = (String) cboSpec.getSelectedItem();
        if (b == null || spec == null) {
            cboDoctor.setModel(new DefaultComboBoxModel<>());
            cboSlot.setModel(new DefaultComboBoxModel<>());
            btnBook.setEnabled(false);
            return;
        }
        List<DoctorItem> docs = loadDoctors(b.id(), spec);
        cboDoctor.setModel(new DefaultComboBoxModel<>(docs.toArray(new DoctorItem[0])));
        reloadSlots();
    }

    private void reloadSlots() {
        cboSlot.removeAllItems();
        btnBook.setEnabled(false);

        DoctorItem doc = (DoctorItem) cboDoctor.getSelectedItem();
        if (doc == null) return;

        Date utilDate = (Date) spDate.getValue();
        LocalDate ld = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String dow = ld.getDayOfWeek().name().substring(0,3); // MON, TUE

        String qSchedule = "SELECT StartTime, EndTime, SlotDurationMin FROM DoctorSchedule WHERE DoctorId = ? AND DayOfWeek = ?";
        String qTaken = "SELECT AppointmentDateTime FROM Appointment WHERE DoctorId = ? AND DATE(AppointmentDateTime) = ? AND Status != 'CANCELLED'";

        try (Connection con = Db.get()) {
            LocalTime start = null, end = null;
            int slotMinutes = 15;

            try (PreparedStatement ps = con.prepareStatement(qSchedule)) {
                ps.setInt(1, doc.id());
                ps.setString(2, dow);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        start = rs.getTime("StartTime").toLocalTime();
                        end   = rs.getTime("EndTime").toLocalTime();
                        slotMinutes = rs.getInt("SlotDurationMin");
                    } else {
                        cboSlot.addItem("No schedule for " + dow);
                        return;
                    }
                }
            }

            Set<LocalTime> taken = new HashSet<>();
            try (PreparedStatement ps = con.prepareStatement(qTaken)) {
                ps.setInt(1, doc.id());
                ps.setDate(2, java.sql.Date.valueOf(ld));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        taken.add(rs.getTimestamp("AppointmentDateTime").toLocalDateTime().toLocalTime());
                    }
                }
            }

            LocalTime t = start;
            int count = 0;
            while (!t.plusMinutes(slotMinutes).isAfter(end)) {
                if (!taken.contains(t)) {
                    cboSlot.addItem(t.toString());
                    count++;
                }
                t = t.plusMinutes(slotMinutes);
            }

            if (count == 0) cboSlot.addItem("No free slots");
            else btnBook.setEnabled(true);

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void bookAppointment() {
        BranchItem branch = (BranchItem) cboBranch.getSelectedItem();
        String spec       = (String) cboSpec.getSelectedItem();
        DoctorItem doc    = (DoctorItem) cboDoctor.getSelectedItem();
        String slotStr    = (String) cboSlot.getSelectedItem();

        if (branch == null || doc == null || slotStr == null || !slotStr.contains(":")) {
            JOptionPane.showMessageDialog(this, "Please select all valid fields.");
            return;
        }

        Date utilDate = (Date) spDate.getValue();
        LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = LocalTime.parse(slotStr);
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);

        int patientId = currentPatient.patientId;
        if (patientId == 0) {
            JOptionPane.showMessageDialog(this, "Error: Current user is not linked to a Patient Profile.");
            return;
        }

        try {
            // 1. Book Appointment
            AppointmentDao dao = new AppointmentDao();
            dao.bookAppointment(patientId, doc.id(), branch.id(), ldt, "Routine Checkup", "LOW");

            // 2. Notify Both Parties
            NotificationDao notify = new NotificationDao();

            // Notify Patient
            notify.send("PATIENT", patientId, "Confirmed: Appointment with " + doc.name() + " on " + ldt.toLocalDate() + " at " + ldt.toLocalTime());

            // Notify Doctor
            notify.send("DOCTOR", doc.id(), "New Appointment: " + currentPatient.fullName + " has booked a slot on " + ldt.toLocalDate() + " at " + ldt.toLocalTime());

            JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!");
            reloadSlots();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking Failed: " + ex.getMessage());
        }
    }

    private record BranchItem(int id, String name) { @Override public String toString() { return name; } }
    private record DoctorItem(int id, String name) { @Override public String toString() { return name; } }
}