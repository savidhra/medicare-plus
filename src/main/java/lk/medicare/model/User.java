package lk.medicare.model;

public class User {
    public int userId;
    public String username;
    public String fullName;
    public String role;
    public Integer branchId;

    // These two fields are required by ScheduleAppointmentPanel
    public Integer patientId;
    public Integer doctorId;

    public User() {}
}
