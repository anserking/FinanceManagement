package utils;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern CNIC_PATTERN = Pattern.compile("^[0-9]{13}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidCNIC(String cnic) {
        return cnic != null && CNIC_PATTERN.matcher(cnic).matches();
    }
}