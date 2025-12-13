package lk.medicare.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {

    private int appointmentId;
    private int patientId;
    private int doctorId;
    private int branchId;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String reason;
    private String urgencyLevel;


    private String patientName;
    private String doctorName;

    public Appointment() {}


    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public void setAppointmentDateTime(Timestamp ts) {
        this.appointmentDateTime = (ts == null) ? null : ts.toLocalDateTime();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public LocalDate getDate() {
        return (appointmentDateTime == null) ? null : appointmentDateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return (appointmentDateTime == null) ? null : appointmentDateTime.toLocalTime();
    }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
}