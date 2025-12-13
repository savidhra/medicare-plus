package lk.medicare.dao;
import lk.medicare.db.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    // Shared SEND method
    public void send(String type, int userId, String msg) throws Exception {
        String sql = "INSERT INTO Notification (RecipientType, RecipientId, Message) VALUES (?, ?, ?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type); ps.setInt(2, userId); ps.setString(3, msg);
            ps.executeUpdate();
        }

    }

    // Member 7 Method
    public List<String> getPatientMessages(int patientId) throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT Message FROM Notification WHERE RecipientType='PATIENT' AND RecipientId=? ORDER BY SentAt DESC";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(rs.getString("Message"));
        }
        return list;
    }

    public List<String> getDoctorAlerts(int doctorId) throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT Message FROM Notification WHERE RecipientType='DOCTOR' AND RecipientId=? ORDER BY SentAt DESC";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) list.add(rs.getString("Message"));
        }
        return list;
    }
}