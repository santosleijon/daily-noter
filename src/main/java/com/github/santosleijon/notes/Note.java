package com.github.santosleijon.notes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Note(UUID noteId, UUID userId, LocalDate date, String content, Instant createdAt, Instant updatedAt) {

    public Note(UUID userId, LocalDate date, String content) {
        this(UUID.randomUUID(), userId, date, content, Instant.now(), null);
    }

    public Note withContent(String content) {
        return new Note(noteId, userId, date, content, createdAt, updatedAt);
    }

    public Note withUpdatedAt(Instant updatedAt) {
        return new Note(noteId, userId, date, content, createdAt, updatedAt);
    }
}
