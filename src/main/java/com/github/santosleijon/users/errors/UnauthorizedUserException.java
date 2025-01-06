package com.github.santosleijon.users.errors;

public class UnauthorizedUserException extends Exception {

    public UnauthorizedUserException() {
        super("User is not authorized");
    }

    public UnauthorizedUserException(String sessionId) {
        super("User is not logged in with a valid user session (" + sessionId + ")");
    }
}
