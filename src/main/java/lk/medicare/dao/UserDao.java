package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.User;

import java.sql.*;

public class UserDao {

    // AUTHENTICATE USER FROM staffuser TABLE
    public User authenticate(String username, String password) throws Exception {

        String sql = """
            SELECT UserId, Username, FullName, Role, BranchId
            FROM staffuser
            WHERE Username = ? AND PasswordHash = ?
        """;

        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    User u = new User();
                    u.userId = rs.getInt("UserId");
                    u.username = rs.getString("Username");
                    u.fullName = rs.getString("FullName");
                    u.role = rs.getString("Role");
                    u.branchId = rs.getInt("BranchId");

                    return u;
                }
            }
        }

        return null; // login failed
    }
}
