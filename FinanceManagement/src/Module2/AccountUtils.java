package Module2;

import java.util.Random;

public class AccountUtils {
    public static String generateAccountNumber() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(rand.nextInt(9) + 1); 
        for (int i = 0; i < 9; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}