package lk.medicare.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Db {

    private static Connection conn;

    public static Connection get() throws Exception {
        if (conn == null || conn.isClosed()) {

            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Direct MySQL connection values
            String url = "jdbc:mysql://localhost:3306/medicareplus?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String user = "root";
            String pass = ""; // add your password if you have one

            // Connect
            conn = DriverManager.getConnection(url, user, pass);
        }

        return conn;
    }
}
