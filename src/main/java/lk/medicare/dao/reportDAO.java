package lk.medicare.dao;
import lk.medicare.db.Db;
import java.sql.*;


public class reportDAO {
    public ResultSet getAppointmentStats() throws Exception {
        String sql = "SELECT Status, COUNT(*) as Total FROM Appointment GROUP BY Status";
        return Db.get().createStatement().executeQuery(sql);
    }


}
