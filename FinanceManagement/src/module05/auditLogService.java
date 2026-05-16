package module05;

import moduledata.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class auditLogService { // Capitalized 'A' for standard Java naming

    /**
     * Logs an activity to the PostgreSQL database.
     */
    public static void logActivity(String email, String action, String details) {
        String sql = "INSERT INTO audit_logs (user_email, action_type, details) VALUES (?, ?, ?)";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, action);
            pstmt.setString(3, details);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Critical Error: Could not save audit log. " + e.getMessage());
        }
    }

    
    public static void displayLogsForUser(String email) {
        String sql = "SELECT created_at, action_type, details FROM audit_logs WHERE user_email = ? ORDER BY created_at DESC";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- SECURITY & ACTIVITY LOGS (" + email + ") ---");
            System.out.printf("%-20s | %-15s | %-30s%n", "Timestamp", "Action", "Details");
            System.out.println("--------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-20s | %-15s | %-30s%n", 
                    rs.getTimestamp("created_at"), 
                    rs.getString("action_type"), 
                    rs.getString("details"));
            }

            if (!found) {
                System.out.println("No activity logs found for this user.");
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
    }
}