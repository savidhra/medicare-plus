package ui;
import service.NotificationService;
public class PatientNotificationUI {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        service.sendReminder(101, "Test notification");
        System.out.println("✅ Notification system ready!");
    }
}
