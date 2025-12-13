package lk.medicare.dao;

import lk.medicare.db.Db;
import java.sql.*;

public class AssignmentDao {



    public int findBestDoctor(String specialization) throws Exception {


        String sql = """
            SELECT d.DoctorId, COUNT(a.AppointmentId) as ApptCount
            FROM Doctor d
            LEFT JOIN Appointment a ON d.DoctorId = a.DoctorId 
                AND a.Status IN ('SCHEDULED', 'DELAYED') 
                AND a.AppointmentDateTime >= NOW()
            WHERE d.Specialization = ?
            GROUP BY d.DoctorId
            ORDER BY ApptCount ASC
            LIMIT 1
        """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, specialization);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("DoctorId");
                }
            }
        }
        return -1;
    }
}