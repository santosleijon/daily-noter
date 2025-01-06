package com.github.santosleijon.notes;

import com.github.santosleijon.common.TimeUtils;
import com.github.santosleijon.notes.errors.NoteNotFound;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class NotesDAOMock implements NotesDAO {

    List<Note> notes = new ArrayList<>();

    @Override
    public void upsert(Note note) {
        notes.stream()
                .filter(existingNote -> existingNote.noteId().equals(note.noteId()))
                .findFirst()
                .ifPresentOrElse(
                        existingNote -> {
                            notes.removeIf(n -> n.noteId().equals(note.noteId()));
                            notes.add(note);
                            notes.sort(Comparator.comparing(Note::createdAt));
                        },
                        () -> notes.add(note)
                );
    }

    @Override
    public void delete(Note note) {
        notes.removeIf(n -> n.noteId().equals(note.noteId()));
    }

    @Override
    public Note find(UUID noteId, UUID userId) throws NoteNotFound {
        return notes.stream()
                .filter(n -> n.noteId().equals(noteId) && n.userId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoteNotFound(noteId));
    }

    @Override
    public List<Note> findAndInitializeNotes(UUID userId, LocalDate from, LocalDate to) {
        List<LocalDate> dates = TimeUtils.getDatesBetween(from, to);

        return dates.stream().map(date ->
                notes.stream().filter(n -> n.date().equals(date)).findFirst().orElseGet(() -> new Note(userId, date, ""))
        ).collect(Collectors.toList());
    }

    public void setupMock(NotesDAO mockedNotesDAO) {
        notes.clear();

        try {
            doAnswer((Answer<Void>) invocationOnMock -> {
                upsert(invocationOnMock.getArgument(0));
                return null;
            })
                    .when(mockedNotesDAO)
                    .upsert(any());

            doAnswer((Answer<Void>) invocationOnMock -> {
                delete(invocationOnMock.getArgument(0));
                return null;
            })
                    .when(mockedNotesDAO)
                    .delete(any());

            doAnswer((Answer<Note>) invocationOnMock ->
                    find(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1))
            )
                    .when(mockedNotesDAO)
                    .find(any(), any());

            doAnswer((Answer<List<Note>>) invocationOnMock ->
                findAndInitializeNotes(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1), invocationOnMock.getArgument(2))
            )
                    .when(mockedNotesDAO)
                    .findAndInitializeNotes(any(), any(), any());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
