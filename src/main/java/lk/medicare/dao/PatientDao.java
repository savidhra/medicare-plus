package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    // ADD
    public void add(Patient p) throws Exception {
        String sql = """
            INSERT INTO Patient
              (BranchId, FirstName, LastName, Gender, DateOfBirth,
               Phone, Email, Address, BloodGroup, Allergies, MedicalHistory)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.branchId);
            ps.setString(2, p.firstName);
            ps.setString(3, p.lastName);
            ps.setString(4, p.gender);
            ps.setDate(5, p.dob);
            ps.setString(6, p.phone);
            ps.setString(7, p.email);
            ps.setString(8, p.address);
            ps.setString(9, p.bloodGroup);
            ps.setString(10, p.allergies);
            ps.setString(11, p.medicalHistory);

            ps.executeUpdate();
        }
    }

    // UPDATE
    public void update(Patient p) throws Exception {
        String sql = """
            UPDATE Patient SET
              BranchId=?, FirstName=?, LastName=?, Gender=?, DateOfBirth=?,
              Phone=?, Email=?, Address=?, BloodGroup=?, Allergies=?, MedicalHistory=?,
              UpdatedAt = NOW()
            WHERE PatientId = ?
        """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.branchId);
            ps.setString(2, p.firstName);
            ps.setString(3, p.lastName);
            ps.setString(4, p.gender);
            ps.setDate(5, p.dob);
            ps.setString(6, p.phone);
            ps.setString(7, p.email);
            ps.setString(8, p.address);
            ps.setString(9, p.bloodGroup);
            ps.setString(10, p.allergies);
            ps.setString(11, p.medicalHistory);
            ps.setInt(12, p.id);

            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM Patient WHERE PatientId = ?";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // GET ONE
    public Patient getById(int id) throws Exception {
        String sql = "SELECT * FROM Patient WHERE PatientId = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    // LIST ALL
    public List<Patient> getAll() throws Exception {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient ORDER BY PatientId DESC";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    // map ResultSet → Patient object
    private Patient map(ResultSet rs) throws Exception {
        Patient p = new Patient();
        p.id             = rs.getInt("PatientId");
        p.branchId       = rs.getInt("BranchId");
        p.firstName      = rs.getString("FirstName");
        p.lastName       = rs.getString("LastName");
        p.gender         = rs.getString("Gender");
        p.dob            = rs.getDate("DateOfBirth");
        p.phone          = rs.getString("Phone");
        p.email          = rs.getString("Email");
        p.address        = rs.getString("Address");
        p.bloodGroup     = rs.getString("BloodGroup");
        p.allergies      = rs.getString("Allergies");
        p.medicalHistory = rs.getString("MedicalHistory");
        return p;
    }
}
