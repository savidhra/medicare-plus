package lk.medicare.model;

public class Branch {
    public int branchId;
    public String name;
    public String address;
    public String phone;
    public String email;

    public Branch() {}

    public Branch(int branchId, String name) {
        this.branchId = branchId;
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}