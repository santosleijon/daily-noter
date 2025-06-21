package com.github.santosleijon.users;

import java.time.Instant;
import java.util.UUID;

public record UserSession(UUID sessionId, UUID userId, String userAgent, String ipAddress, Instant createdAt, Instant validTo, String userEmail) {

    public UserSession(UUID sessionId, UUID userId, String userAgent, String ipAddress, Instant createdAt, Instant validTo) {
        this(sessionId, userId, userAgent, ipAddress, createdAt, validTo, null);
    }

    public UserSession {
        if (userAgent == null) {
            userAgent = "";
        }

        if (ipAddress == null) {
            ipAddress = "";
        }
    }

    public UserSession withValidTo(Instant validTo) {
        return new UserSession(sessionId, userId, userAgent, ipAddress, createdAt, validTo);
    }
}

