package service;
import dao.NotificationDAO;
import model.PatientNotification;
public class NotificationService {
    private NotificationDAO dao = new NotificationDAO();
    public void sendReminder(int patientId, String msg) {
        PatientNotification n = new PatientNotification(patientId, msg);
        dao.saveNotification(n);
    }
}

