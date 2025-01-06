package com.github.santosleijon.notes.errors;

import java.util.UUID;

public class NoteNotFound extends Exception {

    public NoteNotFound(UUID noteId) {
        super("Note not found (" + noteId.toString() + ")");
    }
}
