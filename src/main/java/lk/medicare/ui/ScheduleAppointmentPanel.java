package lk.medicare.ui;

import lk.medicare.model.User;
import lk.medicare.db.Db;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ScheduleAppointmentPanel extends JPanel {

    private final User currentUser;

    // new: branch + specialization
    private final JComboBox<BranchItem> cboBranch;
    private final JComboBox<String>     cboSpec;

    // existing: doctor, date, slot, book
    private final JComboBox<DoctorItem> cboDoctor;
    private final JSpinner              spDate;   // uses java.util.Date
    private final JComboBox<String>     cboSlot;
    private final JButton               btnBook;

    public ScheduleAppointmentPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        // Header
        JLabel title = new JLabel("Schedule Appointment", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // Branch
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Branch:"), gc);

        cboBranch = new JComboBox<>(loadBranches().toArray(new BranchItem[0]));
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        form.add(cboBranch, gc);

        // Specialization
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Specialization:"), gc);

        cboSpec = new JComboBox<>();
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        form.add(cboSpec, gc);

        // Doctor
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        form.add(new JLabel("Doctor:"), gc);

        cboDoctor = new JComboBox<>();
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1;
        form.add(cboDoctor, gc);

        // Date
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0;
        form.add(new JLabel("Date:"), gc);

        spDate = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1;
        form.add(spDate, gc);

        // Slot (dynamic now)
        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0;
        form.add(new JLabel("Time slot:"), gc);

        cboSlot = new JComboBox<>();
        gc.gridx = 1; gc.gridy = 4; gc.weightx = 1;
        form.add(cboSlot, gc);

        add(form, BorderLayout.CENTER);

        // Book button
        btnBook = new JButton("Book");
        btnBook.addActionListener(e -> bookAppointment());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnBook);
        add(south, BorderLayout.SOUTH);

        // wire up cascading filters
        cboBranch.addActionListener(e -> reloadSpecsAndDoctors());
        cboSpec.addActionListener(e -> reloadDoctorsOnly());

        // when doctor or date changes, recompute slots
        cboDoctor.addActionListener(e -> reloadSlots());
        spDate.addChangeListener(e -> reloadSlots());

        // initial fill for spec + doctor + slots based on first branch
        reloadSpecsAndDoctors();
        reloadSlots();
    }

    /* ---------- Loaders & cascade ---------- */

    private List<BranchItem> loadBranches() {
        List<BranchItem> list = new ArrayList<>();
        String sql = "SELECT BranchId, Name FROM Branch ORDER BY Name";
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BranchItem(rs.getInt("BranchId"), rs.getString("Name")));
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return list;
    }

    private List<String> loadSpecializations(int branchId) {
        List<String> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT Specialization
            FROM Doctor
            WHERE BranchId = ?
            ORDER BY Specialization
            """;
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("Specialization"));
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return list;
    }

    private List<DoctorItem> loadDoctors(int branchId, String specialization) {
        List<DoctorItem> list = new ArrayList<>();
        String sql = """
            SELECT DoctorId, CONCAT(FirstName,' ',LastName) AS Name
            FROM Doctor
            WHERE BranchId = ? AND Specialization = ?
            ORDER BY Name
            """;
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, specialization);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DoctorItem(rs.getInt("DoctorId"), rs.getString("Name")));
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return list;
    }

    private void reloadSpecsAndDoctors() {
        BranchItem b = (BranchItem) cboBranch.getSelectedItem();
        if (b == null) return;

        // specializations
        List<String> specs = loadSpecializations(b.id());
        cboSpec.setModel(new DefaultComboBoxModel<>(specs.toArray(new String[0])));

        // doctors (for first/selected spec)
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

    /* ---------- Slot generation from DoctorSchedule & Appointment ---------- */

    private void reloadSlots() {
        cboSlot.removeAllItems();
        btnBook.setEnabled(false);

        DoctorItem doc = (DoctorItem) cboDoctor.getSelectedItem();
        if (doc == null) return;

        // selected date → LocalDate
        Date utilDate = (Date) spDate.getValue();
        LocalDate ld = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // We store MON/TUE/... in DB. Build that from LocalDate.
        String dow = toDbDow(ld.getDayOfWeek()); // e.g., "MON"

        // 1) read doctor's schedule for that day
        Time start = null, end = null;
        int slotMinutes = 15;

        String qSchedule = """
            SELECT StartTime, EndTime, SlotDurationMin
            FROM DoctorSchedule
            WHERE DoctorId = ? AND DayOfWeek = ?
            """;

        // 2) read already taken appointment times for that doctor on that date
        String qTaken = """
            SELECT TIME(AppointmentDateTime) AS T
            FROM Appointment
            WHERE DoctorId = ? AND DATE(AppointmentDateTime) = ?
            """;

        try (Connection con = Db.get()) {
            // fetch schedule
            try (PreparedStatement ps = con.prepareStatement(qSchedule)) {
                ps.setInt(1, doc.id());
                ps.setString(2, dow);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        start = rs.getTime("StartTime");
                        end   = rs.getTime("EndTime");
                        int s = rs.getInt("SlotDurationMin");
                        if (s > 0) slotMinutes = s;
                    } else {
                        // no clinic that day
                        cboSlot.addItem("No schedule for " + dow);
                        return;
                    }
                }
            }

            // collect taken times (as "HH:mm:ss")
            Set<String> taken = new HashSet<>();
            try (PreparedStatement ps = con.prepareStatement(qTaken)) {
                ps.setInt(1, doc.id());
                ps.setDate(2, java.sql.Date.valueOf(ld));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) taken.add(rs.getString("T")); // e.g., "09:30:00"
                }
            }

            // build slot list
            List<String> slots = buildSlots(start.toLocalTime(), end.toLocalTime(), slotMinutes);

            int count = 0;
            for (String s : slots) {
                // exclude taken ("HH:mm" → compare "HH:mm:00")
                if (!taken.contains(s + ":00")) {
                    cboSlot.addItem(s);
                    count++;
                }
            }

            if (count == 0) {
                cboSlot.addItem("No free slots");
            } else {
                btnBook.setEnabled(true);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private static String toDbDow(DayOfWeek dow) {
        // DB has 'MON','TUE','WED','THU','FRI','SAT','SUN'
        return dow.name().substring(0,3);
    }

    private static List<String> buildSlots(LocalTime start, LocalTime end, int minutes) {
        List<String> out = new ArrayList<>();
        LocalTime t = start;
        while (!t.plusMinutes(minutes).isAfter(end)) {
            out.add(t.toString().substring(0,5)); // "HH:mm"
            t = t.plusMinutes(minutes);
        }
        return out;
    }

    /* ---------- Booking ---------- */

    private void bookAppointment() {
        BranchItem branch = (BranchItem) cboBranch.getSelectedItem();
        String spec       = (String) cboSpec.getSelectedItem();
        DoctorItem doc    = (DoctorItem) cboDoctor.getSelectedItem();

        if (branch == null) { warn("Please select a branch."); return; }
        if (spec   == null || spec.isBlank()) { warn("Please select a specialization."); return; }
        if (doc    == null) { warn("Please select a doctor."); return; }

        // Ensure we actually have a slot (not "No schedule..." etc.)
        Object sel = cboSlot.getSelectedItem();
        if (sel == null) { warn("Please select a time slot."); return; }
        String slotStr = sel.toString();
        if (!slotStr.matches("\\d{2}:\\d{2}")) {
            warn("No valid time slot available for the selected date.");
            return;
        }

        // date+time -> Timestamp
        Date utilDate = (Date) spDate.getValue();
        LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localTime = LocalTime.parse(slotStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime ldt = LocalDateTime.of(localDate, localTime);
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf(ldt);

        Integer patientId = currentUser.patientId; // must be logged-in patient
        if (patientId == null) { error("Current user has no PatientId."); return; }

        String sql = """
            INSERT INTO Appointment
              (PatientId, DoctorId, BranchId, AppointmentDateTime, Status, Reason, UrgencyLevel)
            VALUES
              (?, ?, ?, ?, 'SCHEDULED', NULL, 'LOW')
            """;

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doc.id());
            ps.setInt(3, branch.id());
            ps.setTimestamp(4, ts);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment booked!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            // refresh slots to hide the one we just booked
            reloadSlots();
        } catch (Exception ex) {
            showError(ex);
        }
    }


    /* ---------- Helpers ---------- */

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void showError(Exception ex) {
        ex.printStackTrace();
        error(ex.getMessage());
    }

    /* value objects for combos */
    private record BranchItem(int id, String name) {
        @Override public String toString() { return name; }
    }
    private record DoctorItem(int id, String name) {
        @Override public String toString() { return name; }
    }
}
