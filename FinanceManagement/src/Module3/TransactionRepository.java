package Module3;

import moduledata.connection;
import java.sql.*;
import java.math.BigDecimal;

public class TransactionRepository {

    // This replaces the old printMiniStatement() that used an ArrayList
    public static void printMiniStatement(String accountNumber) {
        String sql = "SELECT * FROM transactions WHERE sender_account = ? OR receiver_account = ? ORDER BY timestamp DESC";

        System.out.println("\n===== TRANSACTION HISTORY FOR: " + accountNumber + " =====");
        
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountNumber);
            
            ResultSet rs = pstmt.executeQuery();
            boolean hasTransactions = false;

            while (rs.next()) {
                hasTransactions = true;
                String id = rs.getString("transaction_id");
                String type = rs.getString("transaction_type");
                BigDecimal amount = rs.getBigDecimal("amount");
                Timestamp time = rs.getTimestamp("timestamp");
                String sender = rs.getString("sender_account");
                String receiver = rs.getString("receiver_account");

                System.out.printf("[%s] %s: $%.2f | ID: %s | From: %s -> To: %s%n", 
                                  time, type, amount, id, 
                                  (sender == null ? "N/A" : sender), 
                                  (receiver == null ? "N/A" : receiver));
            }

            if (!hasTransactions) {
                System.out.println("No transactions found for this account.");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
        System.out.println("===============================================\n");
    }

    // Helper for Admin to see everything (Global History)
    public static void printAllTransactions() {
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        
        System.out.println("\n===== GLOBAL TRANSACTION RECORDS =====");
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("[%s] %-10s | %-8s | $%10.2f%n", 
                                  rs.getTimestamp("timestamp"),
                                  rs.getString("transaction_type"),
                                  rs.getString("transaction_id"),
                                  rs.getBigDecimal("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}