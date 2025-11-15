package lk.medicare.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Db {
    private static Connection conn;

    public static Connection get() throws Exception {
        if (conn == null || conn.isClosed()) {
            Properties p = new Properties();
            try (InputStream in = Db.class.getClassLoader().getResourceAsStream("db.properties")) {
                p.load(in);
            }
            conn = DriverManager.getConnection(
                    p.getProperty("db.url"),
                    p.getProperty("db.user"),
                    p.getProperty("db.password")
            );
        }
        return conn;
    }
}
