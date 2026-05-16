package Module04;

import moduledata.connection;
import java.sql.*;

public class Loanhistory {
    public static void displayUserLoanhistory(String email) {
        String sql = "SELECT * FROM loans WHERE applicant_email = ?";

        System.out.println("\n--- Fetching Records from PostgreSQL ---");
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("Loan ID: " + rs.getString("loan_id") + 
                                   " | Amount: " + rs.getBigDecimal("principal") + 
                                   " | Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Query Error: " + e.getMessage());
        }
    }
}