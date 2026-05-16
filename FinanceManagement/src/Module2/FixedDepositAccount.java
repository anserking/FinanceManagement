package Module2;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedDepositAccount extends Account {
    private static final BigDecimal ANNUAL_RATE = new BigDecimal("0.07"); // 7% p.a.
    private int tenureMonths; // optional, could be stored separately

    public FixedDepositAccount(String accountNumber, String ownerName, BigDecimal initialBalance, 
                               AccountStatus status, int tenureMonths) {
        super(accountNumber, ownerName, initialBalance, status, AccountType.FIXED_DEPOSIT);
        this.tenureMonths = tenureMonths;
    }

    @Override
    public void calculateMonthlyInterest() {
        // Fixed deposit usually compounds quarterly or at maturity.
        // For simplicity, we apply monthly compound interest.
        BigDecimal monthlyRate = ANNUAL_RATE.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        BigDecimal interest = balance.multiply(monthlyRate);
        balance = balance.add(interest).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Interest of " + interest + " applied to Fixed Deposit: " + getAccountNumber());
    }

    public int getTenureMonths() { return tenureMonths; }
}