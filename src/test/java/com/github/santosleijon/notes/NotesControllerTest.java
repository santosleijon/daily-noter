package com.github.santosleijon.notes;

import com.github.santosleijon.common.ApplicationTest;
import com.github.santosleijon.common.ErrorResponse;
import com.github.santosleijon.common.TimeUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.MultipartBody;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotesControllerTest extends ApplicationTest {

    @Test
    void getAndInitializeNotesShouldReturnErrorIfUserIsNotLoggedIn() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createGetAndInitializeNotesRequest(server, "2024-01-01", "2024-01-03", null);

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(401);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("User is not authorized");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void getAndInitializeNotesShouldReturnErrorIfUserSessionIsInvalid() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var sessionId = login(server, client);
            logout(server, client, sessionId);

            var request = createGetAndInitializeNotesRequest(server, "2024-01-01", "2024-01-03", sessionId);

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(401);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("User is not logged in with a valid user session (" + sessionId + ")");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void getAndInitializeNotesShouldReturnErrorIfFromDateIsInvalid() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createGetAndInitializeNotesRequest(server, "invalid-date", "2024-01-03", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Invalid date format");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void getAndInitializeNotesShouldReturnErrorIfToDateIsInvalid() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createGetAndInitializeNotesRequest(server, "2024-01-01", "invalid-date", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Invalid date format");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void getAndInitializeNotesShouldReturnErrorIfDateIntervalIsInvalid() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createGetAndInitializeNotesRequest(server, "2024-01-03", "2024-01-01", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Invalid date interval");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void getNotesShouldInitializeAndInitializeNotesThatDontExist() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            assert notesDAOMock.notes.isEmpty();

            var request = createGetAndInitializeNotesRequest(server, "2024-01-01", "2024-01-03", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(200);

                assert response.body() != null;

                var notesResult = objectMapper.readValue(response.body().string(), Note[].class);

                assertThat(notesResult.length).isEqualTo(3);

                Arrays.stream(notesResult).forEach(note -> {
                    assertThat(note.noteId()).isNotNull();
                    assertThat(note.userId()).isEqualTo(usersDAOMock.user.userId());
                    assertThat(note.date()).isBetween(TimeUtils.getLocalDate("2024-01-01"), TimeUtils.getLocalDate("2024-01-03"));
                    assertThat(note.content()).isEqualTo("");
                    assertThat(note.createdAt()).isNotNull();
                    assertThat(note.updatedAt()).isNull();
                });

                assertThat(notesDAOMock.notes.size()).isEqualTo(3);
            }
        });
    }

    @Test
    void getNotesShouldRetrieveExistingNotes() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var userId = usersDAOMock.user.userId();
            var note1 = new Note(userId, TimeUtils.getLocalDate("2024-01-01"), "Note content 1.");
            var note2 = new Note(userId, TimeUtils.getLocalDate("2024-01-02"), "Note content 2.");
            var note3 = new Note(userId, TimeUtils.getLocalDate("2024-01-03"), "Note content 3.");
            notesDAOMock.upsert(note1);
            notesDAOMock.upsert(note2);
            notesDAOMock.upsert(note3);

            var request = createGetAndInitializeNotesRequest(server, "2024-01-01", "2024-01-03", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(200);

                assert response.body() != null;

                var notesResult = objectMapper.readValue(response.body().string(), Note[].class);

                assertThat(notesResult.length).isEqualTo(3);
                assertThat(notesResult[0]).isEqualTo(note3);
                assertThat(notesResult[1]).isEqualTo(note2);
                assertThat(notesResult[2]).isEqualTo(note1);
            }
        });
    }

    private Request createGetAndInitializeNotesRequest(Javalin server, String from, String to, UUID sessionId) {
        String url = "http://localhost:" + server.port() + "/api/notes/?from=" + from + "&to=" + to;

        if (sessionId == null) {
            return new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        }

        return new Request.Builder()
                .url(url)
                .addHeader("Cookie", "sessionId=" + sessionId)
                .get()
                .build();
    }

    @Test
    void updateNoteShouldReturnErrorIfUserIsNotLoggedIn() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createUpdateNoteRequest(server);

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(401);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("User is not authorized");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void updateNoteShouldReturnErrorIfUserSessionIsInvalid(){
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var sessionId = login(server, client);
            logout(server, client, sessionId);

            var request = createUpdateNoteRequest(server, sessionId);

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(401);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("User is not logged in with a valid user session (" + sessionId + ")");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void updateNoteShouldReturnErrorIfNoteDoesNotExist() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var noteId = UUID.randomUUID();
            var request = createUpdateNoteRequest(server, noteId, "Updated note content.", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Note not found (" + noteId + ")");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void updateNoteShouldReturnErrorIfNoteBelongsToAnotherUser() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var anotherUsersId = UUID.randomUUID();
            var anotherUsersNote = new Note(anotherUsersId, LocalDate.now(), "Note content.");
            notesDAOMock.upsert(anotherUsersNote);

            var request = createUpdateNoteRequest(server, anotherUsersNote.noteId(), "Updated note content.", login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Note not found (" + anotherUsersNote.noteId() + ")");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    void updateNoteShouldUpdateNoteContent() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var note = new Note(usersDAOMock.user.userId(), LocalDate.now(), "Note content.");
            notesDAOMock.upsert(note);

            var updatedNoteContent = "Updated note content.";

            var request = createUpdateNoteRequest(server, note.noteId(), updatedNoteContent, login(server, client));

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(200);

                assert response.body() != null;

                var updatedNote = notesDAOMock.find(note.noteId(), note.userId());
                assertThat(updatedNote.content()).isEqualTo(updatedNoteContent);
            }
        });
    }

    private Request createUpdateNoteRequest(Javalin server) {
        return createUpdateNoteRequest(server, UUID.randomUUID(), "Note content.", null);
    }

    private Request createUpdateNoteRequest(Javalin server, UUID sessionId) {
        return createUpdateNoteRequest(server, UUID.randomUUID(), "Note content.", sessionId);
    }

    private Request createUpdateNoteRequest(Javalin server, UUID noteId, String noteContent, UUID sessionId) {
        var requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", noteContent)
                .build();

        if (sessionId != null) {
            return new Request.Builder()
                    .url("http://localhost:" + server.port() + "/api/notes/" + noteId.toString())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Cookie", "sessionId=" + sessionId)
                    .post(requestBody)
                    .build();
        }

        return new Request.Builder()
                .url("http://localhost:" + server.port() + "/api/notes/" + noteId.toString())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
    }
}