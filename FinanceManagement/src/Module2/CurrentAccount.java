package Module2;

import java.math.BigDecimal;

public class CurrentAccount extends Account {
    
    // Constructor updated to include status and type from DB
    public CurrentAccount(String accountNumber, String ownerName, BigDecimal initialBalance, AccountStatus status) {
        super(accountNumber, ownerName, initialBalance, status, AccountType.CURRENT);
    }

    @Override
    public void calculateMonthlyInterest() {
        System.out.println("No interest applicable for Current Account: " + getAccountNumber());
    }
}