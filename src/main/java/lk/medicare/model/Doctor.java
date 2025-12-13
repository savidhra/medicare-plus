package lk.medicare.model;

public class Doctor {
    public int doctorId;
    public int branchId;
    public String firstName;
    public String lastName;
    public String specialization;
    public String phone;
    public String email;
    public String roomNo;

    public Doctor() {}

    public Doctor(int doctorId, String firstName, String lastName, String specialization) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Dr. " + firstName + " " + lastName + " (" + specialization + ")";
    }
}