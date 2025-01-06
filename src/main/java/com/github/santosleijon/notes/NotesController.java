package com.github.santosleijon.notes;

import com.github.santosleijon.common.ErrorResponse;
import com.github.santosleijon.notes.errors.NoteNotFound;
import com.github.santosleijon.users.UserAuthenticator;
import com.github.santosleijon.users.errors.UnauthorizedUserException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

public class NotesController {

    private final NotesDAO notesDAO;
    private final UserAuthenticator userAuthenticator;

    @Inject
    public NotesController(NotesDAO notesDAO, UserAuthenticator userAuthenticator) {
        this.notesDAO = notesDAO;
        this.userAuthenticator = userAuthenticator;
    }

    public void updateNote(Context ctx) {
        try {
            var userId = userAuthenticator.authenticateUserAndGetUserId(ctx);

            var noteId = UUID.fromString(ctx.pathParam("noteId"));

            var note = notesDAO.find(noteId, userId);

            var updatedNoteContent = ctx.formParam("content");

            var updatedNote = note.withContent(updatedNoteContent).withUpdatedAt(Instant.now());

            notesDAO.upsert(updatedNote);
        } catch (IllegalArgumentException e) {
            ctx.json(new ErrorResponse("Invalid content ID format"));
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (NoteNotFound e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedUserException e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Internal server error"));
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void getNotes(Context ctx) {
        // TODO
    }
}
