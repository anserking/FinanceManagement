package Module3;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionReceipt {
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final String type; 
    private final BigDecimal amount;
    private String senderAcc;
    private String receiverAcc;

    // Updated constructor to handle sender/receiver for the DB
    public TransactionReceipt(String type, BigDecimal amount, String senderAcc, String receiverAcc) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.senderAcc = senderAcc;
        this.receiverAcc = receiverAcc;
    }

    // Getters for Database insertion
    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getSenderAcc() { return senderAcc; }
    public String getReceiverAcc() { return receiverAcc; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s | ID: %s | From: %s To: %s", 
                timestamp, type, amount, transactionId, senderAcc, receiverAcc);
    }
}