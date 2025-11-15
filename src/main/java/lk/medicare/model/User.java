package lk.medicare.model;

public class User {
    public int userId;
    public String username;
    public String role; // PATIENT / DOCTOR / ADMIN / RECEPTIONIST
    public Integer patientId; // nullable
    public Integer doctorId;  // nullable
}
