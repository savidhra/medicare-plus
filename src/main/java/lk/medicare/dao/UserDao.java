package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.User;
import lk.medicare.util.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {

    public User authenticate(String username, String plainPassword) throws Exception {


        String hashedPassword = SecurityUtil.hashPassword(plainPassword);


        String sql = "SELECT UserId, Username, Role, FullName, PatientId, DoctorId, BranchId " +
                "FROM Users WHERE Username = ? AND PasswordHash = ?";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    return new User(
                            rs.getInt("UserId"),
                            rs.getString("Username"),
                            rs.getString("Role"),
                            rs.getString("FullName"),
                            rs.getInt("PatientId"),
                            rs.getInt("DoctorId"),
                            rs.getInt("BranchId")
                    );
                }
            }
        }
        return null;
    }
}