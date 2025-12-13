package lk.medicare.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentSlot {
    public LocalDateTime start;
    public LocalDateTime end;
    public String label;

    public AppointmentSlot(LocalDateTime s, LocalDateTime e) {
        this.start = s;
        this.end = e;

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        this.label = s.format(timeFmt) + " - " + e.format(timeFmt);
    }

    @Override
    public String toString() { return label; }
}