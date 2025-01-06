package com.github.santosleijon.notes;

import com.github.santosleijon.notes.errors.NoteNotFound;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface NotesDAO {
    void upsert(Note note) throws SQLException;
    void delete(Note note) throws SQLException;
    Note find(UUID noteId, UUID userId) throws NoteNotFound, SQLException;
    List<Note> findAndInitializeNotes(UUID userId, LocalDate from, LocalDate to) throws SQLException;
}
