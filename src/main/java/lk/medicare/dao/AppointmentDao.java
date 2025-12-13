package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.Appointment;
import lk.medicare.model.AppointmentSlot;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    public Appointment getAppointment(int appointmentId) throws Exception {
        String sql = """
            SELECT a.*, p.FirstName AS PatFirst, p.LastName AS PatLast, d.FirstName AS DocFirst, d.LastName AS DocLast
            FROM Appointment a
            LEFT JOIN Patient p ON a.PatientId = p.PatientId
            LEFT JOIN Doctor d ON a.DoctorId = d.DoctorId
            WHERE a.AppointmentId = ?
        """;
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment a = mapAppointment(rs);
                    if (rs.getString("PatFirst") != null)
                        a.setPatientName(rs.getString("PatFirst") + " " + rs.getString("PatLast"));
                    if (rs.getString("DocFirst") != null)
                        a.setDoctorName("Dr. " + rs.getString("DocFirst") + " " + rs.getString("DocLast"));
                    return a;
                }
            }
        }
        return null;
    }

    public List<AppointmentSlot> getAvailableSlots(int doctorId, LocalDate date) throws Exception {
        DayOfWeek dow = date.getDayOfWeek();
        String day3 = dow.name().substring(0, 3);
        List<AppointmentSlot> slots = new ArrayList<>();
        String schSql = "SELECT StartTime, EndTime, SlotDurationMin FROM DoctorSchedule WHERE DoctorId = ? AND DayOfWeek = ?";

        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(schSql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, day3);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalTime start = rs.getTime("StartTime").toLocalTime();
                    LocalTime end   = rs.getTime("EndTime").toLocalTime();
                    int mins = rs.getInt("SlotDurationMin");
                    LocalDateTime cursor = date.atTime(start);
                    LocalDateTime endDT  = date.atTime(end);
                    while (!cursor.plusMinutes(mins).isAfter(endDT)) {
                        slots.add(new AppointmentSlot(cursor, cursor.plusMinutes(mins)));
                        cursor = cursor.plusMinutes(mins);
                    }
                }
            }
        }
        if (slots.isEmpty()) return slots;

        String bookedSql = "SELECT AppointmentDateTime FROM Appointment WHERE DoctorId = ? AND DATE(AppointmentDateTime) = ? AND Status != 'CANCELLED'";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(bookedSql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("AppointmentDateTime");
                    if(ts != null) slots.removeIf(s -> s.start.equals(ts.toLocalDateTime()));
                }
            }
        }
        return slots;
    }

    public void bookAppointment(int patientId, int doctorId, int branchId, LocalDateTime dateTime, String reason, String urgency) throws Exception {
        if (!isSlotAvailable(doctorId, dateTime)) throw new Exception("This slot has already been booked!");
        String sql = "INSERT INTO Appointment (PatientId, DoctorId, BranchId, AppointmentDateTime, Status, Reason, UrgencyLevel) VALUES (?,?,?,?, 'SCHEDULED', ?, ?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setInt(3, branchId);
            ps.setTimestamp(4, Timestamp.valueOf(dateTime));
            ps.setString(5, reason);
            ps.setString(6, urgency);
            ps.executeUpdate();
        }
    }

    private boolean isSlotAvailable(int doctorId, LocalDateTime dt) throws Exception {
        String sql = "SELECT 1 FROM Appointment WHERE DoctorId=? AND AppointmentDateTime=? AND Status!='CANCELLED'";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setTimestamp(2, Timestamp.valueOf(dt));
            try (ResultSet rs = ps.executeQuery()) { return !rs.next(); }
        }
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) throws Exception {
        String sql = "SELECT a.*, p.FirstName, p.LastName FROM Appointment a JOIN Patient p ON a.PatientId = p.PatientId WHERE a.DoctorId = ? ORDER BY a.AppointmentDateTime DESC";
        List<Appointment> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment a = mapAppointment(rs);
                    a.setPatientName(rs.getString("FirstName") + " " + rs.getString("LastName"));
                    list.add(a);
                }
            }
        }
        return list;
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) throws Exception {
        String sql = "SELECT a.*, d.FirstName, d.LastName FROM Appointment a JOIN Doctor d ON a.DoctorId = d.DoctorId WHERE a.PatientId = ? ORDER BY a.AppointmentDateTime DESC";
        List<Appointment> list = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment a = mapAppointment(rs);
                    a.setDoctorName("Dr. " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                    list.add(a);
                }
            }
        }
        return list;
    }

    // --- THESE ARE THE METHODS YOUR StatusPanel WAS MISSING ---
    public ResultSet getTodayAppointments() throws Exception {
        String sql = """
            SELECT a.AppointmentId, p.FirstName, p.LastName, a.AppointmentDateTime, a.Status
            FROM Appointment a
            JOIN Patient p ON a.PatientId = p.PatientId
            WHERE DATE(a.AppointmentDateTime) = CURDATE()
            ORDER BY a.AppointmentDateTime
        """;
        return Db.get().createStatement().executeQuery(sql);
    }

    public void updateStatus(int appointmentId, String newStatus) throws Exception {
        String sql = "UPDATE Appointment SET Status = ? WHERE AppointmentId = ?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
        }
    }

    public void updateAppointmentStatus(int id, String status) throws Exception {
        updateStatus(id, status);
    }

    public void cancelAppointment(int appointmentId) throws Exception {
        updateStatus(appointmentId, "CANCELLED");
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("AppointmentId"));
        a.setPatientId(rs.getInt("PatientId"));
        a.setDoctorId(rs.getInt("DoctorId"));
        // IMPORTANT: We use getTimestamp for safety
        Timestamp ts = rs.getTimestamp("AppointmentDateTime");
        if(ts != null) a.setAppointmentDateTime(ts);
        a.setStatus(rs.getString("Status"));
        a.setReason(rs.getString("Reason"));
        a.setUrgencyLevel(rs.getString("UrgencyLevel"));
        return a;
    }
}