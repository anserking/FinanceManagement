/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module05;



import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAuditService {

    /**
     * Converts a full Exception stack trace into a String and logs it.
     */
    public static void logException(String email, Exception e) {
        // Convert the stack trace (the long error message) to a String
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Save it to your database using your existing AuditLogService
        // We use a substring to make sure it's not too long for the database column
        String shortDetail = "ERROR: " + e.getMessage() + " | Trace: " + 
                             (stackTrace.length() > 500 ? stackTrace.substring(0, 500) : stackTrace);

        auditLogService.logActivity(email, "SYSTEM_ERROR", shortDetail);
        
        // Still print it to console so you can see it while coding
        System.err.println("!!! SYSTEM ERROR LOGGED: " + e.getMessage());
    }
}