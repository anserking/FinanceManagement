package Module1;

import Module2.Account;
import Module2.AccountStatus;
import Module2.CurrentAccount;
import Module2.FixedDepositAccount;
import Module2.SavingsAccount;
import moduledata.connection;
import module05.PasswordUtil;
import module05.auditLogService;
import java.sql.*;
import java.math.BigDecimal; // Required for balance
import utils.ValidationUtil;
// Import your Account classes here if they are in a different package
// import module02.Account; 
// import module02.SavingsAccount; 

public class AuthService {

    /**
     * Registers a new user with the "CUSTOMER" role by default.
     */
    public void register(String name, String email, String nationalId, String password) {
    // Input validation
    if (!ValidationUtil.isValidEmail(email)) {
        System.out.println("Registration Failed: Invalid email format.");
        return;
    }
    if (!ValidationUtil.isValidCNIC(nationalId)) {
        System.out.println("Registration Failed: CNIC must be exactly 13 digits.");
        return;
    }
    if (password == null || password.length() < 4) {
        System.out.println("Registration Failed: Password must be at least 4 characters.");
        return;
    }
        String enforcedRole = "CUSTOMER";
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        String sql = "INSERT INTO users (full_name, email, national_id, password_hash, salt, role, is_locked, login_attempts) " +
                     "VALUES (?, ?, ?, ?, ?, ?, FALSE, 0)";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, nationalId);
            pstmt.setString(4, hash);
            pstmt.setString(5, salt);
            pstmt.setString(6, enforcedRole);

            pstmt.executeUpdate();
            System.out.println("Registration successful. You can now login with email or CNIC.");
            auditLogService.logActivity(email, "REGISTER", "New user registered with role: CUSTOMER");

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Registration Failed: Email or CNIC already exists.");
                auditLogService.logActivity(email, "REGISTER_FAIL", "Duplicate entry attempt");
            } else {
                System.out.println("Database Error: " + e.getMessage());
            }
        }
    }

    /**
     * Authenticates a user using either their Email or National ID.
     */
    public User login(String identifier, String password) {
        String selectSql = "SELECT * FROM users WHERE email = ? OR national_id = ?";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {

            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("User not found.");
                auditLogService.logActivity(identifier, "LOGIN_FAIL", "Attempt with non-existent email/CNIC");
                return null;
            }

            String email = rs.getString("email");
            boolean dbIsLocked = rs.getBoolean("is_locked");

            if (dbIsLocked) {
                System.out.println("SECURITY ALERT: This account is LOCKED.");
                auditLogService.logActivity(email, "SECURITY_ALERT", "Locked account login attempt");
                return null;
            }

            String storedHash = rs.getString("password_hash");
            String storedSalt = rs.getString("salt");
            String computedHash = PasswordUtil.hashPassword(password, storedSalt);

            if (computedHash.equals(storedHash)) {
                resetAttemptsInDB(email);
                System.out.println("Login successful! Welcome, " + email);
                auditLogService.logActivity(email, "LOGIN_SUCCESS", "User authenticated successfully");

                return new User(
                    rs.getString("full_name"),
                    email,
                    rs.getString("national_id"),
                    storedHash,
                    storedSalt,
                    rs.getString("role"),
                    dbIsLocked
                );
            } else {
                incrementAttemptsInDB(email);
                System.out.println("Invalid password.");
                auditLogService.logActivity(email, "LOGIN_FAIL", "Incorrect password");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Fetches a single account by its number (for transfers, etc.)
     * @param accountNumber
     * @return 
     */
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
                        // Assuming 12 months is a default term if not in DB
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

    private void incrementAttemptsInDB(String email) {
        String sql = "UPDATE users SET login_attempts = login_attempts + 1, " +
                     "is_locked = CASE WHEN login_attempts + 1 >= 5 THEN TRUE ELSE FALSE END " +
                     "WHERE email = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetAttemptsInDB(String email) {
        String sql = "UPDATE users SET login_attempts = 0 WHERE email = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}