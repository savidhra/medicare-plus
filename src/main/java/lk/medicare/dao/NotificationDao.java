package lk.medicare.dao;
import lk.medicare.db.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao {

    public List<String> getDoctorAlerts(int doctorId) throws Exception {
        List<String> list = new ArrayList<>();
        String sql ="SELECT Message FROM Notification WHERE RecipientType='DOCTOR' AND RecipientId=? ORDER BY SentAt DESC";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(rs.getString("Message"));
        }
        return list;
    }
}
