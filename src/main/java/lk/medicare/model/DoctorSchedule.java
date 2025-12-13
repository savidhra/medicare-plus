package lk.medicare.model;

import java.time.LocalTime;

public class DoctorSchedule {
    public int scheduleId;
    public int doctorId;
    public String dayOfWeek;
    public LocalTime startTime;
    public LocalTime endTime;
    public int slotDurationMin;

    public DoctorSchedule() {}

    public DoctorSchedule(int doctorId, String dayOfWeek, LocalTime start, LocalTime end, int duration) {
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = start;
        this.endTime = end;
        this.slotDurationMin = duration;
    }
}