package Module2;

public enum AccountStatus {
    ACTIVE, SUSPENDED, CLOSED;

    // Helper method to convert Database string to Enum
    public static AccountStatus fromString(String status) {
        return AccountStatus.valueOf(status.toUpperCase());
    }
}