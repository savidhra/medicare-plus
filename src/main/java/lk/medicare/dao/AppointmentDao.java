package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.AppointmentSlot;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    // Return slots for a given doctor on a given date, derived from DoctorSchedule and removing already-booked ones
    public List<AppointmentSlot> getAvailableSlots(int doctorId, LocalDate date) throws Exception {
        DayOfWeek dow = date.getDayOfWeek(); // MON..SUN
        String day3 = dow.name().substring(0,3); // "MON" etc.

        // 1) read schedule row(s) for this weekday
        String schSql = """
            SELECT StartTime, EndTime, SlotDurationMin
            FROM DoctorSchedule
            WHERE DoctorId = ? AND DayOfWeek = ?
        """;
        List<AppointmentSlot> slots = new ArrayList<>();
        try (PreparedStatement ps = Db.get().prepareStatement(schSql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, day3);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Time st = rs.getTime("StartTime");
                    Time en = rs.getTime("EndTime");
                    int mins = rs.getInt("SlotDurationMin");
                    LocalTime start = st.toLocalTime();
                    LocalTime end   = en.toLocalTime();

                    // 2) generate raw slots
                    LocalDateTime cursor = date.atTime(start);
                    while (cursor.plusMinutes(mins).isBefore(date.atTime(end).plusSeconds(1))) {
                        slots.add(new AppointmentSlot(cursor, cursor.plusMinutes(mins)));
                        cursor = cursor.plusMinutes(mins);
                    }
                }
            }
        }

        if (slots.isEmpty()) return slots;

        // 3) remove slots that are already booked
        String bookedSql = """
            SELECT AppointmentDateTime
            FROM Appointment
            WHERE DoctorId = ? AND CONVERT(date, AppointmentDateTime) = ?
              AND Status IN ('SCHEDULED','DELAYED') -- treat as occupied
        """;
        List<LocalDateTime> booked = new ArrayList<>();
        try (PreparedStatement ps = Db.get().prepareStatement(bookedSql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    booked.add(rs.getTimestamp(1).toLocalDateTime());
                }
            }
        }

        slots.removeIf(s -> booked.contains(s.start));
        return slots;
    }

    public void bookAppointment(int patientId, int doctorId, int branchId, LocalDateTime dateTime, String reason, String urgency) throws Exception {
        // quick guard: ensure not already booked
        String exists = """
            SELECT 1 FROM Appointment
            WHERE DoctorId=? AND AppointmentDateTime=? AND Status IN ('SCHEDULED','DELAYED')
        """;
        try (PreparedStatement chk = Db.get().prepareStatement(exists)) {
            chk.setInt(1, doctorId);
            chk.setTimestamp(2, Timestamp.valueOf(dateTime));
            try (ResultSet rs = chk.executeQuery()) {
                if (rs.next()) throw new IllegalStateException("Slot already booked");
            }
        }

        String ins = """
            INSERT INTO Appointment (PatientId, DoctorId, BranchId, AppointmentDateTime, Status, Reason, UrgencyLevel)
            VALUES (?,?,?,?, 'SCHEDULED', ?, ?)
        """;
        try (PreparedStatement ps = Db.get().prepareStatement(ins)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setInt(3, branchId);
            ps.setTimestamp(4, Timestamp.valueOf(dateTime));
            ps.setString(5, reason);
            ps.setString(6, urgency);
            ps.executeUpdate();
        }
    }
}
