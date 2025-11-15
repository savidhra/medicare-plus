package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDao {
    public List<Doctor> getAll() throws Exception {
        String sql = "SELECT DoctorId, FirstName, LastName, Specialization FROM Doctor ORDER BY LastName, FirstName";
        List<Doctor> list = new ArrayList<>();
        try (PreparedStatement ps = Db.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doctor d = new Doctor();
                d.doctorId = rs.getInt("DoctorId");
                d.fullName = rs.getString("FirstName") + " " + rs.getString("LastName");
                d.specialization = rs.getString("Specialization");
                list.add(d);
            }
        }
        return list;
    }
}
