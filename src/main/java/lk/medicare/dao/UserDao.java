package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.User;

import java.sql.*;

public class UserDao {
    // DEMO ONLY: PasswordHash is plain. Replace with BCrypt in real use.
    public User authenticate(String username, String password) throws Exception {
        String sql = """
            SELECT UserId, Username, Role, PatientId, DoctorId 
            FROM UserAccount 
            WHERE Username = ? AND `Password` = ?
        """;
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.userId = rs.getInt("UserId");
                    u.username = rs.getString("Username");
                    u.role = rs.getString("Role");
                    int p = rs.getInt("PatientId");   u.patientId = rs.wasNull() ? null : p;
                    int d = rs.getInt("DoctorId");    u.doctorId = rs.wasNull() ? null : d;
                    return u;
                }
            }
        }
        return null;
    }
}
