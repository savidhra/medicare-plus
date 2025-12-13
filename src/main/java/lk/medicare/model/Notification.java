package lk.medicare.model;

import java.time.LocalDateTime;

public class Notification {
    public int notificationId;
    public int appointmentId;
    public String recipientType;
    public int recipientId;
    public String message;
    public LocalDateTime sentAt;
    public String status;

    public Notification() {}

    public Notification(String message, LocalDateTime sentAt) {
        this.message = message;
        this.sentAt = sentAt;
    }
}