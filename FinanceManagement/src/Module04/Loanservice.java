 package Module04;

import Module1.User;
import moduledata.connection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Loanservice {

    public static void processLoanApplication(User user, BigDecimal amount, int months) throws LoanInvalidException {
    if (user.isLocked()) {
        throw new LoanInvalidException("Account is locked. Cannot apply for loans.");
    }
    BigDecimal rate = (months <= 12) ? new BigDecimal("0.05") : new BigDecimal("0.10");
    String id = "LNT-" + System.currentTimeMillis() % 100000;
    loan newLoan = new loan(id, user, amount, rate, months);
    // No auto-approval – always PENDING
    newLoan.setStatus("PENDING");

    String sql = "INSERT INTO loans (loan_id, applicant_email, principal, interest_rate, months, status) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = connection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, newLoan.getLoanId());
        pstmt.setString(2, user.getEmail());
        pstmt.setBigDecimal(3, newLoan.getPrincipal());
        pstmt.setBigDecimal(4, newLoan.getInterestRate());
        pstmt.setInt(5, newLoan.getMonths());
        pstmt.setString(6, newLoan.getStatus());
        pstmt.executeUpdate();
        System.out.println("Loan application submitted (PENDING).");
    } catch (SQLException e) {
        System.out.println("Database Error: " + e.getMessage());
        throw new LoanInvalidException("Could not save loan application.");
    }
}
    public static List<loan> getPendingLoans() {
    List<loan> pendingLoans = new ArrayList<>();
    String sql = "SELECT * FROM loans WHERE status = 'PENDING'";
    try (Connection conn = connection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            // We need a User object for the applicant; we can create a minimal one
            String email = rs.getString("applicant_email");
            User minimalUser = new User("", email, "", "", "", "CUSTOMER", false);
            loan l = new loan(
                rs.getString("loan_id"),
                minimalUser,
                rs.getBigDecimal("principal"),
                rs.getBigDecimal("interest_rate"),
                rs.getInt("months")
            );
            l.setStatus(rs.getString("status"));
            pendingLoans.add(l);
        }
    } catch (SQLException e) {
        System.out.println("Error fetching pending loans: " + e.getMessage());
    }
    return pendingLoans;
}

public static void updateLoanStatus(String loanId, String newStatus) {
    String sql = "UPDATE loans SET status = ? WHERE loan_id = ?";
    try (Connection conn = connection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, newStatus);
        pstmt.setString(2, loanId);
        int rows = pstmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Loan " + loanId + " status updated to " + newStatus);
            // Log the action (optional)
            module05.auditLogService.logActivity("ADMIN", "LOAN_APPROVAL", 
                "Loan " + loanId + " set to " + newStatus);
        } else {
            System.out.println("Loan ID not found.");
        }
    } catch (SQLException e) {
        System.out.println("Error updating loan: " + e.getMessage());
    }
}
}