package Module1;

public class User {
    private String fullName;
    private String email;
    private String nationalID;
    private String passwordHash;   // now stores hash, not plain password
    private String salt;           // new field
    private String role; 
    private boolean isLocked;

    // Constructor with salt
    public User(String fullName, String email, String nationalID, String passwordHash, 
                String salt, String role, boolean isLocked) {
        this.fullName = fullName;
        this.email = email;
        this.nationalID = nationalID;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.isLocked = isLocked;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getRole() { return role; }
    public String getNationalID() { return nationalID; }
    public boolean isLocked() { return isLocked; }
}