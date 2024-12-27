package com.github.santosleijon.users;

public class InvalidUserCredentialsException extends Exception {

    public InvalidUserCredentialsException() {
        super("Invalid user email or password");
    }
}
