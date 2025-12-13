package lk.medicare.model;

import java.sql.Date;

public class Patient {
    public int patientId;
    public int branchId;
    public String firstName;
    public String lastName;
    public String gender;
    public Date dateOfBirth;
    public String phone;
    public String email;
    public String address;
    public String bloodGroup;
    public String allergies;

    public Patient() {}

    public Patient(int branchId, String firstName, String lastName, String phone, String email, Date dob) {
        this.branchId = branchId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.dateOfBirth = dob;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}