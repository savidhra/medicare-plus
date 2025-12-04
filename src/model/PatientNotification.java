package model;

public class PatientNotification {
     private int notificationId;
    private int patientId;
    private String message;
    
    // Constructor
    public PatientNotification(int patientId, String message) {
        this.patientId = patientId;
        this.message = message;
    }
    
    // Getters and Setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int id) { this.notificationId = id; }
    
    public int getPatientId() { return patientId; }
    public void setPatientId(int id) { this.patientId = id; }
    
    public String getMessage() { return message; }
    public void setMessage(String msg) { this.message = msg; }
}
