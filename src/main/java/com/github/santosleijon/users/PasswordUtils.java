package com.github.santosleijon.users;

import com.password4j.Password;

public class PasswordUtils {

    // TODO: Create psw4j.properties file with config

    public static String hashPassword(String plainPassword) {
        return Password.hash(plainPassword)
                .addRandomSalt()
                .withArgon2()
                .getResult();
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return Password.check(plainPassword, hashedPassword).withArgon2();
    }
}