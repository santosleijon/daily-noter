package com.github.santosleijon.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.santosleijon.Application;
import com.github.santosleijon.notes.NotesController;
import com.github.santosleijon.notes.NotesDAO;
import com.github.santosleijon.notes.NotesDAOMock;
import com.github.santosleijon.users.*;
import com.github.santosleijon.users.dto.LoginRequestDTO;
import io.javalin.Javalin;
import io.javalin.testtools.HttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTest {

    protected EnvironmentVariableReader mockedEnvironmentVariableReader = Mockito.mock(EnvironmentVariableReader.class);
    protected EnvironmentVariableReaderMock environmentVariableReaderMock = new EnvironmentVariableReaderMock();

    protected UsersDAO mockedUsersDAO = Mockito.mock(UsersDAO.class);
    protected UsersDAOMock usersDAOMock = new UsersDAOMock();

    protected UserSessionsDAO mockedUserSessionsDAO = Mockito.mock(UserSessionsDAO.class);
    protected UserSessionsDAOMock userSessionsDAOMock = new UserSessionsDAOMock();

    protected NotesDAO mockedNotesDAO = Mockito.mock(NotesDAO.class);
    protected NotesDAOMock notesDAOMock = new NotesDAOMock();

    protected ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(WRITE_DATES_AS_TIMESTAMPS, false);

    @BeforeEach
    public void beforeEach() {
        environmentVariableReaderMock.setupMock(mockedEnvironmentVariableReader);
        usersDAOMock.setupMock(mockedUsersDAO);
        userSessionsDAOMock.setupMock(mockedUserSessionsDAO);
        notesDAOMock.setupMock(mockedNotesDAO);
    }

    protected Javalin getJavalinAppUnderTest() {
        var usersController = new UsersController(mockedUsersDAO, mockedUserSessionsDAO);
        var notesController = new NotesController(mockedNotesDAO, new UserAuthenticator(mockedUserSessionsDAO));

        return Application.getJavalinApp(usersController, notesController);
    }

    protected UUID login(Javalin server, HttpClient client) throws IOException {
        var loginRequestDTO = new LoginRequestDTO("user@example.com", "my-secret-password");

        var requestJson = objectMapper.writeValueAsString(loginRequestDTO);

        var requestBody = RequestBody.create(requestJson.getBytes(StandardCharsets.UTF_8));

        var request = new Request.Builder()
                .url("http://localhost:" + server.port() + "/api/users/login")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (var response = client.request(request)) {
            assertThat(response.code()).isEqualTo(200);

            assert response.body() != null;

            return UUID.fromString(objectMapper.readValue(response.body().string(), UserSessionResponse.class).sessionId());
        }
    }

    protected void logout(Javalin server, HttpClient client, UUID sessionId) {
        var request = new Request.Builder()
                .url("http://localhost:" + server.port() + "/api/users/logout")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", "sessionId=" + sessionId.toString())
                .post(RequestBody.create(new byte[0]))
                .build();

        try (var response = client.request(request)) {
            assertThat(response.code()).isEqualTo(200);
        }
    }
}
