package Module2;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingsAccount extends Account {
    private static final BigDecimal ANNUAL_RATE = new BigDecimal("0.05");

    public SavingsAccount(String accountNumber, String ownerName, BigDecimal initialBalance, AccountStatus status) {
        super(accountNumber, ownerName, initialBalance, status, AccountType.SAVINGS);
    }

    @Override
    public void calculateMonthlyInterest() {
        BigDecimal monthlyRate = ANNUAL_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        BigDecimal interest = balance.multiply(monthlyRate);
        
        // Update local balance
        balance = balance.add(interest).setScale(2, RoundingMode.HALF_UP);
        
        System.out.println("Interest of " + interest + " applied to account: " + getAccountNumber());
        System.out.println("New Local Balance (Needs DB Sync): " + balance);
    }
}