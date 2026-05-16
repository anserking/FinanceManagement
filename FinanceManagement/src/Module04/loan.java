package Module04;

import Module1.User;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class loan {
    private String loanId;
    private User applicant;
    private BigDecimal principal;
    private BigDecimal interestRate; 
    private int months;
    private String status; 

    public loan(String loanId, User applicant, BigDecimal principal, BigDecimal interestRate, int months) {
        this.loanId = loanId;
        this.applicant = applicant;
        this.principal = principal;
        this.interestRate = interestRate;
        this.months = months;
        this.status = "PENDING";
    }

    public BigDecimal calculateTotalRepayment() {
        BigDecimal interest = principal.multiply(interestRate);
        return principal.add(interest).setScale(2, RoundingMode.HALF_UP);
    }

    // Getters and Setters
    public String getLoanId() { return loanId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApplicantEmail() { return applicant.getEmail(); }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getInterestRate() { return interestRate; }
    public int getMonths() { return months; }

    @Override
    public String toString() {
        return String.format("ID: %s | Amount: $%s | Status: %s", loanId, principal, status);
    }
}