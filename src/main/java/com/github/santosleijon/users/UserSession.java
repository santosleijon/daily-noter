package com.github.santosleijon.users;

import java.time.Instant;
import java.util.UUID;

public record UserSession(UUID sessionId, UUID userId, Instant createdAt, Instant validTo) {
}
