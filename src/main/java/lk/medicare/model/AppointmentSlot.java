package lk.medicare.model;

import java.time.LocalDateTime;

public class AppointmentSlot {
    public LocalDateTime start;
    public LocalDateTime end;
    public String label;
    public AppointmentSlot(LocalDateTime s, LocalDateTime e) {
        this.start = s; this.end = e;
        this.label = s.toLocalTime().toString();
    }
    @Override public String toString() { return label; }
}
