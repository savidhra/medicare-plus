package lk.medicare.dao;

import lk.medicare.db.Db;
import java.sql.ResultSet;

public class ReportDao {

    public ResultSet getAppointmentStats() throws Exception {
        String sql = "SELECT Status, COUNT(*) AS Total FROM Appointment GROUP BY Status";
        return Db.get().createStatement().executeQuery(sql);
    }

    public ResultSet getMonthlyVolumes() throws Exception {
        // FIXED SQL: Orders by MIN date to allow strict Grouping
        String sql = """
            SELECT 
                DATE_FORMAT(AppointmentDateTime, '%Y-%M') AS Month, 
                COUNT(*) AS Total_Appointments
            FROM Appointment
            GROUP BY DATE_FORMAT(AppointmentDateTime, '%Y-%M')
            ORDER BY MIN(AppointmentDateTime)
        """;
        return Db.get().createStatement().executeQuery(sql);
    }

    public ResultSet getDoctorPerformance() throws Exception {
        String sql = """
            SELECT 
                d.DoctorId, d.FirstName, d.LastName, d.Specialization,
                COUNT(a.AppointmentId) AS Total_Appointments
            FROM Doctor d
            LEFT JOIN Appointment a ON d.DoctorId = a.DoctorId
            GROUP BY d.DoctorId
            ORDER BY Total_Appointments DESC
        """;
        return Db.get().createStatement().executeQuery(sql);
    }

    public ResultSet getPatientVisitSummary() throws Exception {
        String sql = """
            SELECT 
                p.PatientId, p.FirstName, p.LastName, 
                COUNT(a.AppointmentId) AS Total_Visits
            FROM Patient p
            LEFT JOIN Appointment a ON p.PatientId = a.PatientId
            GROUP BY p.PatientId
            ORDER BY Total_Visits DESC
        """;
        return Db.get().createStatement().executeQuery(sql);
    }
}