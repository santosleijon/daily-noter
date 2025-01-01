package com.github.santosleijon.users;

import java.util.UUID;

public record UserSessionResponse(String sessionId) {

    public UserSessionResponse(UUID sessionId) {
        this(sessionId.toString());
    }
}
