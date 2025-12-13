package lk.medicare.dao;

import lk.medicare.db.Db;
import lk.medicare.model.Branch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDao {
    public List<Branch> getAllBranches() throws Exception {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT BranchId, Name FROM Branch";
        try (Connection c = Db.get();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Branch(rs.getInt("BranchId"), rs.getString("Name")));
            }
        }
        return list;
    }
}