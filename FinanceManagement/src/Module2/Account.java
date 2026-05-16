package Module2;

import java.math.BigDecimal;

public abstract class Account {
    private final String accountNumber;
    private final String ownerName;
    protected BigDecimal balance;
    private AccountStatus status;
    private AccountType type; // Added to map to the 'type' column in DB

    public Account(String accountNumber, String ownerName, BigDecimal initialBalance, AccountStatus status, AccountType type) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.status = status;
        this.type = type;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public AccountType getType() { return type; }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public abstract void calculateMonthlyInterest();

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public String toString() {
        return String.format("Acc No: %s | Owner: %s | Balance: %s | Status: %s | Type: %s", 
                accountNumber, ownerName, balance, status, type);
    }
}