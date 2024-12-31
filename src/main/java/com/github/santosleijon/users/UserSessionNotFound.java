package com.github.santosleijon.users;

import java.util.UUID;

public class UserSessionNotFound extends Exception {

    public UserSessionNotFound(UUID sessionId) {
        super("User session with ID " + sessionId.toString() + "not found");
    }
}
