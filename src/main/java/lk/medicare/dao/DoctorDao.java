package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.Doctor;
import lk.medicare.util.SecurityUtil; // Ensure you have this for hashing
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDao {

    // 1. Register Doctor (Transactional)
    public void registerDoctor(String fName, String lName, String spec, String username, String password) throws Exception {
        Connection c = null;
        try {
            c = Db.get();
            c.setAutoCommit(false); // Start Transaction

            // Insert Doctor
            String sqlDoc = "INSERT INTO Doctor (BranchId, FirstName, LastName, Specialization) VALUES (1, ?, ?, ?)";
            PreparedStatement psDoc = c.prepareStatement(sqlDoc, Statement.RETURN_GENERATED_KEYS);
            psDoc.setString(1, fName);
            psDoc.setString(2, lName);
            psDoc.setString(3, spec);
            psDoc.executeUpdate();

            // Get ID
            ResultSet rs = psDoc.getGeneratedKeys();
            int newDocId = 0;
            if (rs.next()) newDocId = rs.getInt(1);

            // Insert User
            String sqlUser = "INSERT INTO Users (Username, PasswordHash, Role, FullName, DoctorId, BranchId) VALUES (?, ?, 'DOCTOR', ?, ?, 1)";
            PreparedStatement psUser = c.prepareStatement(sqlUser);
            psUser.setString(1, username);
            psUser.setString(2, SecurityUtil.hashPassword(password));
            psUser.setString(3, "Dr. " + fName + " " + lName);
            psUser.setInt(4, newDocId);
            psUser.executeUpdate();

            c.commit();
        } catch (Exception e) {
            if (c != null) try { c.rollback(); } catch (Exception ex){}
            throw e;
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (Exception ex){}
        }
    }

    // 2. Add Schedule
    public void addSchedule(int doctorId, String dayOfWeek, String startTime, String endTime) throws Exception {
        String sql = "INSERT INTO DoctorSchedule (DoctorId, DayOfWeek, StartTime, EndTime, SlotDurationMin) VALUES (?,?,?,?, 15)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setString(2, dayOfWeek);
            ps.setTime(3, Time.valueOf(startTime));
            ps.setTime(4, Time.valueOf(endTime));
            ps.executeUpdate();
        }
    }

    // 3. Get All Doctors (For Dropdowns - Strings)
    public List<String> getAllDoctors() throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DoctorId, FirstName, LastName FROM Doctor ORDER BY FirstName";
        try (Connection c = Db.get(); ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getInt("DoctorId") + " - " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }
        }
        return list;
    }

    // 4. Get All Doctors (For Table View - Objects) -> THIS WAS MISSING
    public List<Doctor> getAllDoctorsList() throws Exception {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM Doctor ORDER BY FirstName";
        try (Connection c = Db.get(); ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("DoctorId"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Specialization")
                ));
            }
        }
        return list;
    }

    // 5. Update Doctor -> THIS WAS MISSING
    public void updateDoctor(int id, String fName, String lName, String spec) throws Exception {
        String sql = "UPDATE Doctor SET FirstName=?, LastName=?, Specialization=? WHERE DoctorId=?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fName);
            ps.setString(2, lName);
            ps.setString(3, spec);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    // 6. Delete Doctor -> THIS WAS MISSING
    public void deleteDoctor(int id) throws Exception {
        String sql = "DELETE FROM Doctor WHERE DoctorId=?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // 7. Get Schedule List
    public List<String> getScheduleForDoctor(int doctorId) throws Exception {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT DayOfWeek, StartTime, EndTime FROM DoctorSchedule WHERE DoctorId = ? ORDER BY DayOfWeek";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(rs.getString("DayOfWeek") + " : " + rs.getTime("StartTime") + " - " + rs.getTime("EndTime"));
                }
            }
        }
        return rows;
    }
}