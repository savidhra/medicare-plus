package lk.medicare.model;

import java.sql.Date;

public class Patient {
    public int id;
    public int branchId;
    public String firstName;
    public String lastName;
    public String gender;
    public Date dob;
    public String phone;
    public String email;
    public String address;
    public String bloodGroup;
    public String allergies;
    public String medicalHistory;

    public Patient() {}

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
