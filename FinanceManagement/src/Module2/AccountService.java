package Module2;

import moduledata.connection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    public static List<Account> searchAccounts(String searchTerm) {
    List<Account> accounts = new ArrayList<>();
    String sql = "SELECT * FROM accounts WHERE account_number = ? OR cnic = ?";
    try (Connection conn = connection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, searchTerm);
        pstmt.setString(2, searchTerm);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String accNum = rs.getString("account_number");
            String type = rs.getString("type");
            BigDecimal balance = rs.getBigDecimal("balance");
            String ownerEmail = rs.getString("owner_email");
            AccountStatus status = AccountStatus.valueOf(rs.getString("status"));
            Account acc = null;
            if ("SAVINGS".equals(type)) {
                acc = new SavingsAccount(accNum, ownerEmail, balance, status);
            } else if ("CURRENT".equals(type)) {
                acc = new CurrentAccount(accNum, ownerEmail, balance, status);
            } else if ("FIXED_DEPOSIT".equals(type)) {
                acc = new FixedDepositAccount(accNum, ownerEmail, balance, status, 12);
            }
            if (acc != null) accounts.add(acc);
        }
    } catch (SQLException e) {
        System.out.println("Search error: " + e.getMessage());
    }
    return accounts;
}
// Fetch a single active account by its account number
public static Account getAccountByNumber(String accountNumber) {
    String sql = "SELECT * FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";
    try (Connection conn = connection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, accountNumber);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            String type = rs.getString("account_type");
            BigDecimal balance = rs.getBigDecimal("balance");
            String ownerEmail = rs.getString("owner_email");
            AccountStatus status = AccountStatus.valueOf(rs.getString("status"));
            switch (type) {
                case "SAVINGS":
                    return new SavingsAccount(accountNumber, ownerEmail, balance, status);
                case "CURRENT":
                    return new CurrentAccount(accountNumber, ownerEmail, balance, status);
                case "FIXED_DEPOSIT":
                    // Use a default tenure (e.g., 12 months) – adjust if you store tenure in DB
                    return new FixedDepositAccount(accountNumber, ownerEmail, balance, status, 12);
                default:
                    return null;
            }
        }
    } catch (SQLException e) {
        System.out.println("Error fetching account: " + e.getMessage());
    }
    return null;
}
    // ----- ACCOUNT APPLICATIONS -----
    public static void applyForAccount(String userEmail, String accountType, BigDecimal initialDeposit) {
        String sql = "INSERT INTO account_applications (applicant_email, account_type, initial_deposit, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setString(2, accountType);
            pstmt.setBigDecimal(3, initialDeposit);
            pstmt.executeUpdate();
            System.out.println("Account application submitted for " + accountType + " account.");
        } catch (SQLException e) {
            System.out.println("Error applying for account: " + e.getMessage());
        }
    }

    public static List<AccountApplication> getPendingAccountApplications() {
        List<AccountApplication> list = new ArrayList<>();
        String sql = "SELECT * FROM account_applications WHERE status = 'PENDING'";
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AccountApplication app = new AccountApplication(
                    rs.getInt("application_id"),
                    rs.getString("applicant_email"),
                    rs.getString("account_type"),
                    rs.getBigDecimal("initial_deposit"),
                    rs.getString("status"),
                    rs.getTimestamp("applied_date")
                );
                list.add(app);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending applications: " + e.getMessage());
        }
        return list;
    }

    public static void approveAccountApplication(int applicationId, String adminEmail, String generatedAccountNumber) {
    String selectSql = "SELECT a.*, u.full_name, u.national_id FROM account_applications a JOIN users u ON a.applicant_email = u.email WHERE a.application_id = ?";
    try (Connection conn = connection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
        pstmt.setInt(1, applicationId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            String userEmail = rs.getString("applicant_email");
            String accType = rs.getString("account_type");
            BigDecimal initialDeposit = rs.getBigDecimal("initial_deposit");
            String ownerName = rs.getString("full_name");
            String cnic = rs.getString("national_id");

            // Insert into accounts table
            String insertAccount = "INSERT INTO accounts (account_number, owner_email, owner_name, balance, status, type, cnic) VALUES (?, ?, ?, ?, 'ACTIVE', ?, ?)";
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertAccount)) {
                pstmt2.setString(1, generatedAccountNumber);
                pstmt2.setString(2, userEmail);
                pstmt2.setString(3, ownerName);
                pstmt2.setBigDecimal(4, initialDeposit);
                pstmt2.setString(5, accType);
                pstmt2.setString(6, cnic);
                pstmt2.executeUpdate();
            }

            // Update application status
            String updateApp = "UPDATE account_applications SET status = 'APPROVED', approved_by = ?, approved_date = CURRENT_TIMESTAMP WHERE application_id = ?";
            try (PreparedStatement pstmt3 = conn.prepareStatement(updateApp)) {
                pstmt3.setString(1, adminEmail);
                pstmt3.setInt(2, applicationId);
                pstmt3.executeUpdate();
            }
            System.out.println("Account approved: " + generatedAccountNumber);
        }
    } catch (SQLException e) {
        System.out.println("Error approving application: " + e.getMessage());
    }
}

    public static void rejectAccountApplication(int applicationId, String adminEmail) {
        String sql = "UPDATE account_applications SET status = 'REJECTED', approved_by = ?, approved_date = CURRENT_TIMESTAMP WHERE application_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminEmail);
            pstmt.setInt(2, applicationId);
            pstmt.executeUpdate();
            System.out.println("Application rejected.");
        } catch (SQLException e) {
            System.out.println("Error rejecting application: " + e.getMessage());
        }
    }

    // ----- FETCH USER ACCOUNTS (approved) -----
    public static List<Account> getUserAccounts(String userEmail) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE owner_email = ? AND status = 'ACTIVE'";
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String accNum = rs.getString("account_number");
                String type = rs.getString("account_type");
                BigDecimal balance = rs.getBigDecimal("balance");
                AccountStatus status = AccountStatus.valueOf(rs.getString("status"));
                Account acc = null;
                if (type.equals("SAVINGS")) {
                    acc = new SavingsAccount(accNum, userEmail, balance, status);
                } else if (type.equals("CURRENT")) {
                    acc = new CurrentAccount(accNum, userEmail, balance, status);
                } else if (type.equals("FIXED_DEPOSIT")) {
                    acc = new FixedDepositAccount(accNum, userEmail, balance, status, 12); // default tenure
                }
                if (acc != null) accounts.add(acc);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts: " + e.getMessage());
        }
        return accounts;
    }

    // Simple DTO for pending applications
    public static class AccountApplication {
        public int id;
        public String email;
        public String type;
        public BigDecimal deposit;
        public String status;
        public Timestamp appliedDate;

        public AccountApplication(int id, String email, String type, BigDecimal deposit, String status, Timestamp appliedDate) {
            this.id = id; this.email = email; this.type = type; this.deposit = deposit; this.status = status; this.appliedDate = appliedDate;
        }

        @Override
        public String toString() {
            return String.format("ID:%d | %s | %s | Deposit: $%s | Applied: %s", id, email, type, deposit, appliedDate);
        }
    }
}