package dao;
import model.PatientNotification;
public class NotificationDAO {
    public void saveNotification(PatientNotification n) {
        System.out.println("Saving to DB: " + n.getMessage());
    }
}
