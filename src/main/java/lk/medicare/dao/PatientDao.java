package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.Patient;
import lk.medicare.util.SecurityUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {


    public boolean registerPatient(Patient p, String username, String password) throws Exception {
        Connection c = null;
        try {
            c = Db.get();
            c.setAutoCommit(false);


            String sqlPatient = "INSERT INTO Patient (BranchId, FirstName, LastName, Phone, Email, DateOfBirth, Gender) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";


            PreparedStatement psPat = c.prepareStatement(sqlPatient, Statement.RETURN_GENERATED_KEYS);
            psPat.setInt(1, 1);
            psPat.setString(2, p.firstName);
            psPat.setString(3, p.lastName);
            psPat.setString(4, p.phone);
            psPat.setString(5, p.email);
            psPat.setDate(6, p.dateOfBirth);
            psPat.setString(7, "OTHER");
            psPat.executeUpdate();


            ResultSet rs = psPat.getGeneratedKeys();
            int newPatientId = 0;
            if (rs.next()) {
                newPatientId = rs.getInt(1);
            } else {
                throw new SQLException("Creating patient failed, no ID obtained.");
            }


            String sqlUser = "INSERT INTO Users (Username, PasswordHash, Role, FullName, PatientId, BranchId) " +
                    "VALUES (?, ?, 'PATIENT', ?, ?, 1)";

            PreparedStatement psUser = c.prepareStatement(sqlUser);
            psUser.setString(1, username);
            psUser.setString(2, SecurityUtil.hashPassword(password)); // Hash it!
            psUser.setString(3, p.firstName + " " + p.lastName);
            psUser.setInt(4, newPatientId);
            psUser.executeUpdate();

            c.commit();
            return true;

        } catch (Exception e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            throw e;
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }


    public List<Patient> getAll() throws Exception {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient";

        try (Connection c = Db.get();
             ResultSet rs = c.createStatement().executeQuery(sql)) {

            while(rs.next()) {
                Patient p = new Patient();
                p.patientId = rs.getInt("PatientId");
                p.firstName = rs.getString("FirstName");
                p.lastName = rs.getString("LastName");
                p.phone = rs.getString("Phone");
                p.email = rs.getString("Email");
                list.add(p);
            }
        }
        return list;
    }

    // Update Patient Details
    public void updatePatient(int id, String phone, String email, String history) throws Exception {
        String sql = "UPDATE Patient SET Phone=?, Email=?, MedicalHistory=? WHERE PatientId=?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, email);
            ps.setString(3, history);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    // Delete Patient (And their User Login)
    public void deletePatient(int id) throws Exception {

        String sql = "DELETE FROM Patient WHERE PatientId=?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updatePatient(int id, String fName, String lName, String phone, String email) throws Exception {
        String sql = "UPDATE Patient SET FirstName=?, LastName=?, Phone=?, Email=? WHERE PatientId=?";
        try (java.sql.Connection c = lk.medicare.db.Db.get();
             java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fName);
            ps.setString(2, lName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }
}