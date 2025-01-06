package com.github.santosleijon.notes;

import com.github.santosleijon.common.ApplicationTest;
import com.github.santosleijon.common.ErrorResponse;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.MultipartBody;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotesControllerTest extends ApplicationTest {

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

                var expectedResponse = new ErrorResponse("Note content found (" + noteId + ")");
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

                var expectedResponse = new ErrorResponse("Note content found (" + anotherUsersNote.noteId() + ")");
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