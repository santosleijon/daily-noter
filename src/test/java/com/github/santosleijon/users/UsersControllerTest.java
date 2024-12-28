package com.github.santosleijon.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.santosleijon.Application;
import com.github.santosleijon.common.EnvironmentVariableReader;
import common.EnvironmentVariableReaderMock;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsersControllerTest {

    EnvironmentVariableReader mockedEnvironmentVariableReader = Mockito.mock(EnvironmentVariableReader.class);
    EnvironmentVariableReaderMock environmentVariableReaderMock = new EnvironmentVariableReaderMock();

    UsersDAO mockedUsersDAO = Mockito.mock(UsersDAO.class);
    UsersDAOMock usersDAOMock = new UsersDAOMock();
    UserSessionsDAO mockedUserSessionsDAO = Mockito.mock(UserSessionsDAO.class);
    UserSessionsDAOMock userSessionsDAOMock = new UserSessionsDAOMock();

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        environmentVariableReaderMock.setupMock(mockedEnvironmentVariableReader);
        usersDAOMock.setupMock(mockedUsersDAO);
        userSessionsDAOMock.setupMock(mockedUserSessionsDAO);
    }

    @Test
    public void loginShouldReturnBadRequestIfUserCredentialsAreMissing() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            try (var response = client.post("/api/users/login")) {
                assertThat(response.code()).isEqualTo(400);
                assert response.body() != null;
                assertThat(response.body().string()).isEqualTo("{ \"error\": \"User credentials missing\" }");
            }
        });
    }

    @Test
    public void loginShouldReturnUnauthorizedWhenInvalidEmailIsSubmitted() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createLoginRequest(server.port(), "invalid-email", "my-secret-password");

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(401);
                assert response.body() != null;
                assertThat(response.body().string()).isEqualTo("{ \"error\": \"Invalid user credentials\" }");
            }
        });
    }

    @Test
    public void loginShouldReturnUnauthorizedWhenInvalidPasswordIsSubmitted() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createLoginRequest(server.port(), "user@example.com", "invalid-password");

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(401);
                assert response.body() != null;
                assertThat(response.body().string()).isEqualTo("{ \"error\": \"Invalid user credentials\" }");
            }
        });
    }

    @Test
    public void loginShouldReturnSuccessWhenValidUserCredentialsAreSubmitted() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createLoginRequest(server.port(), "user@example.com", "my-secret-password");

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(200);
                var cookie = response.header("Set-Cookie");
                assertThat(cookie).contains("sessionId=");
                assertThat(cookie).contains("HttpOnly");
                assertThat(cookie).contains("SameSite=Strict");
                assertThat(cookie).contains("Path=/");
                assert response.body() != null;
                assertThat(response.body().string()).contains("sessionId");
            }
        });
    }

    @Test
    public void logoutShouldInvalidateUserSession() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var loginRequest = createLoginRequest(server.port(), "user@example.com", "my-secret-password");

            try (var loginResponse = client.request(loginRequest)) {
                assert loginResponse.body() != null;
                var jsonResponseBody = objectMapper.readTree(loginResponse.body().string());
                var sessionId = jsonResponseBody.get("sessionId").asText();

                var logoutRequest = new Request.Builder()
                        .url("http://localhost:" + server.port() + "/api/users/logout")
                        .header("Cookie", "sessionId=" + sessionId)
                        .post(RequestBody.create(new byte[0]))
                        .build();

                try (var logoutResponse = client.request(logoutRequest)) {
                    assertThat(logoutResponse.code()).isEqualTo(200);
                    assert logoutResponse.body() != null;
                    assertThat(logoutResponse.body().string()).contains("sessionId");

                    UserSession userSession = userSessionsDAOMock.find(UUID.fromString(sessionId));
                    assertThat(userSession.validTo().isBefore(Instant.now())).isTrue();
                }
            }
        });
    }

    private Javalin getJavalinAppUnderTest() {
        return Application.getJavalinApp(mockedUsersDAO, mockedUserSessionsDAO);
    }

    private static Request createLoginRequest(int serverPort, String email, String password) {
        var requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .build();

        return new Request.Builder()
                .url("http://localhost:" + serverPort + "/api/users/login")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
    }
}