/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Module1;

import module05.PasswordUtil;

public class temp {
    public static void main(String[] args) {
        String password = "ilyas";   // Change to a strong password
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        System.out.println("Salt: " + salt);
        System.out.println("Hash: " + hash);
        System.out.println("Use these values in your INSERT statement.");
    }
}

