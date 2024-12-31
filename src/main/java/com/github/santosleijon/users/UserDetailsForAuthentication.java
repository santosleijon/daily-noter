package com.github.santosleijon.users;

import java.time.Instant;
import java.util.UUID;

public record UserDetailsForAuthentication(UUID userId, String email, String hashedPassword, Instant createdAt) {}
