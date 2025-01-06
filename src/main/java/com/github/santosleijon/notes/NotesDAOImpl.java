package com.github.santosleijon.notes;

import com.github.santosleijon.common.DatabaseConnection;
import com.github.santosleijon.common.TimeUtils;
import com.github.santosleijon.notes.errors.NoteNotFound;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotesDAOImpl implements NotesDAO {

    @Override
    public void upsert(Note note) throws SQLException {
        var query = "INSERT INTO notes (note_id, user_id, date, content, created_at, updated_at)" +
                    "VALUES (?, ?, ?, ?, ?, ?)" +
                    "ON CONFLICT (note_id)" +
                    "DO UPDATE SET user_id = ?, date = ?, content = ?, created_at = ?, updated_at = ?;";

        OffsetDateTime createdAt = TimeUtils.getOffsetDateTime(note.createdAt());
        OffsetDateTime updatedAt = TimeUtils.getOffsetDateTime(note.updatedAt());

        try (var connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, note.noteId());
            preparedStatement.setObject(2, note.userId());
            preparedStatement.setObject(3, note.date());
            preparedStatement.setString(4, note.content());
            preparedStatement.setObject(5, createdAt);
            preparedStatement.setObject(6, updatedAt);
            preparedStatement.setObject(7, note.userId());
            preparedStatement.setObject(8, note.date());
            preparedStatement.setObject(9, note.content());
            preparedStatement.setObject(10, createdAt);
            preparedStatement.setObject(11, updatedAt);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(Note note) throws SQLException {
        var query = "DELETE FROM notes WHERE note_id = ?";

        try (var connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, note.noteId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Note find(UUID noteId, UUID userId) throws NoteNotFound, SQLException {
        try (var connection = DatabaseConnection.getConnection()) {
            var query = "SELECT note_id, user_id, date, content, created_at, updated_at FROM notes WHERE note_id = ? AND user_id = ? LIMIT 1";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, noteId);
            preparedStatement.setObject(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new NoteNotFound(noteId);
            }

            LocalDate date = resultSet.getObject("date", LocalDate.class);
            String note = resultSet.getString("content");
            Instant createdAt = TimeUtils.getInstantFromResultSet(resultSet, "created_at");
            Instant updatedAt = TimeUtils.getInstantFromResultSet(resultSet, "updated_at");

            return new Note(noteId, userId, date, note, createdAt, updatedAt);
        }
    }

    @Override
    public List<Note> findAndInitializeNotes(UUID userId, LocalDate from, LocalDate to) throws SQLException {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Dates must be non-null");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date must not be after 'to' date");
        }

        var query = "SELECT note_id, user_id, date, content, created_at, updated_at FROM notes WHERE user_id = ? AND date BETWEEN ? AND ?";

        var notes = new ArrayList<Note>();

        try (var connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, userId);
            preparedStatement.setObject(2, from);
            preparedStatement.setObject(3, to);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UUID noteId = UUID.fromString(resultSet.getString("note_id"));
                LocalDate date = resultSet.getObject("date", LocalDate.class);
                String content = resultSet.getString("content");
                Instant createdAt = TimeUtils.getInstantFromResultSet(resultSet, "created_at");
                Instant updatedAt = TimeUtils.getInstantFromResultSet(resultSet, "updated_at");

                notes.add(new Note(noteId, userId, date, content, createdAt, updatedAt));
            }
        }

        var datesThatShouldBeInitialized = TimeUtils.getDatesBetween(from, to);

        for (LocalDate date : datesThatShouldBeInitialized) {
            if (notes.stream().noneMatch(note -> note.date().equals(date))) {
                var initializedNote = new Note(userId, date, "");
                upsert(initializedNote);
                notes.add(initializedNote);
            }
        }

        return notes.stream()
                .sorted(Comparator.comparing(Note::date).reversed())
                .collect(Collectors.toList());
    }
}
