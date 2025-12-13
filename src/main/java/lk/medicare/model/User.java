package lk.medicare.model;

public class User {
    public int userId;
    public String username;
    public String role;
    public String fullName;

    // --- NEW LINKING FIELDS ---
    public int patientId;
    public int doctorId;
    public int branchId;

    public User() {}

    public User(int userId, String username, String role, String fullName, int patientId, int doctorId, int branchId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.branchId = branchId;
    }
}