package com.github.santosleijon.users;

import java.util.UUID;

public class UserSessionNotFound extends Exception {

    public UserSessionNotFound(UUID sessionId) {
        super("Session with ID " + sessionId.toString() + " not found");
    }
}
