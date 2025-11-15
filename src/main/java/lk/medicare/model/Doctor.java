package lk.medicare.model;

public class Doctor {
    public int doctorId;
    public String fullName;
    public String specialization;

    @Override public String toString() { return fullName + " (" + specialization + ")"; }
}
