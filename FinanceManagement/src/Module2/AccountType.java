package Module2;

public enum AccountType {
    SAVINGS, CURRENT, FIXED_DEPOSIT;

    public static AccountType fromString(String type) {
        return AccountType.valueOf(type.toUpperCase());
    }
}