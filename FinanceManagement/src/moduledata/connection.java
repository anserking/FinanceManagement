package moduledata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection {
    // REMOVED "neondb_owner:npg_CvARFY4D0cuX@" and ADDED ":5432" port
    private static final String URL = "jdbc:postgresql://ep-fragrant-resonance-aphz3jky-pooler.c-7.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_CvARFY4D0cuX";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}