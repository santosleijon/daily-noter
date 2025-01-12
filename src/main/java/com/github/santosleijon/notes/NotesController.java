package com.github.santosleijon.notes;

import com.github.santosleijon.common.ErrorResponse;
import com.github.santosleijon.common.TimeUtils;
import com.github.santosleijon.notes.dto.UpdateNoteDTO;
import com.github.santosleijon.notes.errors.NoteNotFound;
import com.github.santosleijon.users.UserAuthenticator;
import com.github.santosleijon.users.errors.UnauthorizedUserException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotesController {

    private final NotesDAO notesDAO;
    private final UserAuthenticator userAuthenticator;

    @Inject
    public NotesController(NotesDAO notesDAO, UserAuthenticator userAuthenticator) {
        this.notesDAO = notesDAO;
        this.userAuthenticator = userAuthenticator;
    }

    public void getAndInitializeNotes(Context ctx) {
        try {
            var userId = userAuthenticator.authenticateUserAndGetUserId(ctx);

            var from = ctx.queryParam("from");
            var to = ctx.queryParam("to");

            if (from == null || to == null) {
                throw new IllegalArgumentException("Date parameters are missing");
            }

            var fromDate = TimeUtils.getLocalDate(from);
            var toDate = TimeUtils.getLocalDate(to);

            if (fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException("Invalid date interval");
            }

            var existingNotes = notesDAO.find(userId, fromDate, toDate);

            var existingAndInitializedNotes = new ArrayList<>(existingNotes);

            var datesThatShouldBeInitialized = TimeUtils.getDatesBetween(fromDate, toDate);

            for (LocalDate date : datesThatShouldBeInitialized) {
                if (existingAndInitializedNotes.stream().noneMatch(note -> note.date().equals(date))) {
                    var initializedNote = new Note(userId, date, "");
                    notesDAO.upsert(initializedNote);
                    existingAndInitializedNotes.add(initializedNote);
                }
            }

            var result = existingAndInitializedNotes.stream()
                    .sorted(Comparator.comparing(Note::date).reversed())
                    .collect(Collectors.toList());

            ctx.json(result);
        } catch (UnauthorizedUserException e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (DateTimeParseException e) {
            ctx.json(new ErrorResponse("Invalid date format"));
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Internal server error"));
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateNote(Context ctx) {
        try {
            UpdateNoteDTO requestDTO;

            try {
                requestDTO = ctx.bodyAsClass(UpdateNoteDTO.class);
            } catch (Exception e) {
                ctx.json(new ErrorResponse("Invalid request body"));
                ctx.status(HttpStatus.BAD_REQUEST);
                return;
            }

            var userId = userAuthenticator.authenticateUserAndGetUserId(ctx);

            var noteId = UUID.fromString(ctx.pathParam("noteId"));

            var note = notesDAO.find(noteId, userId);

            var updatedNote = note.withContent(requestDTO.content()).withUpdatedAt(Instant.now());

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

    public void deleteNote(Context ctx) {
        try {
            var userId = userAuthenticator.authenticateUserAndGetUserId(ctx);

            var noteId = UUID.fromString(ctx.pathParam("noteId"));

            var note = notesDAO.find(noteId, userId);

            notesDAO.delete(note);
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
}
