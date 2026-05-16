package Module3;

import Module2.Account;
import Module2.AccountStatus;
import moduledata.connection;
import java.math.BigDecimal;
import java.sql.*;

public class TransactionEngine {
    private static final BigDecimal DAILY_WITHDRAW_LIMIT = new BigDecimal("50000.00");

    public static void transfer(Account sender, Account receiver, BigDecimal amount) throws LimitExceededException {
        // 1. Basic Validation
        if (sender.getStatus() != AccountStatus.ACTIVE || receiver.getStatus() != AccountStatus.ACTIVE) {
            System.out.println("Transfer Failed: One or both accounts are not ACTIVE.");
            return;
        }
        if (amount.compareTo(DAILY_WITHDRAW_LIMIT) > 0) {
            throw new LimitExceededException("Transfer amount exceeds the daily limit!");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            System.out.println("Transfer failed: Insufficient funds.");
            return;
        }

        String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String logSql = "INSERT INTO transactions (transaction_id, sender_account, receiver_account, transaction_type, amount) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = connection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            // 1. Debit Sender
            try (PreparedStatement psDebit = conn.prepareStatement(debitSql)) {
                psDebit.setBigDecimal(1, amount);
                psDebit.setString(2, sender.getAccountNumber());
                psDebit.executeUpdate();
            }

            // 2. Credit Receiver
            try (PreparedStatement psCredit = conn.prepareStatement(creditSql)) {
                psCredit.setBigDecimal(1, amount);
                psCredit.setString(2, receiver.getAccountNumber());
                psCredit.executeUpdate();
            }

            // 3. Log Transaction Receipt
            TransactionReceipt receipt = new TransactionReceipt("TRANSFER", amount, sender.getAccountNumber(), receiver.getAccountNumber());
            try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                psLog.setString(1, receipt.getTransactionId());
                psLog.setString(2, receipt.getSenderAcc());
                psLog.setString(3, receipt.getReceiverAcc());
                psLog.setString(4, receipt.getType());
                psLog.setBigDecimal(5, receipt.getAmount());
                psLog.executeUpdate();
            }

            conn.commit(); // FINISH TRANSACTION
            module05.auditLogService.logActivity(
                sender.getAccountNumber(), 
                "TRANSFER", 
                "Sent " + amount + " to " + receiver.getAccountNumber());
            System.out.println("--- Transaction Successful ---");
            System.out.println(receipt.toString());

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            module05.ExceptionAuditService.logException(sender.getAccountNumber(), e);
            System.out.println("Database Error: Transaction Rolled Back. " + e.getMessage());
        }
    }

    public static void deposit(Account account, BigDecimal amount) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            System.out.println("Error: Account is not ACTIVE.");
            return;
        }

        String sqlUpdate = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String sqlLog = "INSERT INTO transactions (transaction_id, receiver_account, transaction_type, amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = connection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setBigDecimal(1, amount);
                psUpdate.setString(2, account.getAccountNumber());
                psUpdate.executeUpdate();
            }

            TransactionReceipt receipt = new TransactionReceipt("DEPOSIT", amount, null, account.getAccountNumber());
            try (PreparedStatement psLog = conn.prepareStatement(sqlLog)) {
                psLog.setString(1, receipt.getTransactionId());
                psLog.setString(2, receipt.getReceiverAcc());
                psLog.setString(3, receipt.getType());
                psLog.setBigDecimal(4, receipt.getAmount());
                psLog.executeUpdate();
            }

            conn.commit();
            module05.auditLogService.logActivity(
                account.getAccountNumber(), 
                "DEPOSIT", 
                "Deposited amount: " + amount
            );
            System.out.println("Successfully deposited: " + amount);
        } catch (SQLException e) {
            System.out.println("Deposit Failed: " + e.getMessage());
        }
    }
}