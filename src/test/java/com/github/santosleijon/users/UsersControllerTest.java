package com.github.santosleijon.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.santosleijon.common.ApplicationTest;
import com.github.santosleijon.common.ErrorResponse;
import com.github.santosleijon.users.dto.LoginRequestDTO;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsersControllerTest extends ApplicationTest {

    @Test
    public void loginShouldReturnBadRequestIfUserCredentialsAreMissing() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            try (var response = client.post("/api/users/login")) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("User credentials missing");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
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

                var expectedResponse = new ErrorResponse("Invalid user credentials");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
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

                var expectedResponse = new ErrorResponse("Invalid user credentials");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    public void loginShouldCreateUserSessionWhenValidUserCredentialsAreSubmitted() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var request = createValidLoginRequest(server);

            try (var response = client.request(request)) {
                assertThat(response.code()).isEqualTo(200);
                var cookie = response.header("Set-Cookie");
                assertThat(cookie).contains("sessionId=");
                assertThat(cookie).contains("HttpOnly");
                assertThat(cookie).contains("SameSite=Strict");
                assertThat(cookie).contains("Path=/");

                assert response.body() != null;
                var userSessionResponse = objectMapper.readValue(response.body().string(), UserSessionResponse.class);
                assertThat(userSessionResponse.sessionId()).isNotBlank();
            }
        });
    }

    @Test
    public void loginShouldInvalidateAllButTheLatestThreeValidSessions() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            // Create three user sessions
            for (int i = 0; i<3; i++) {
                var loginRequest = createValidLoginRequest(server);

                try (var loginResponse = client.request(loginRequest)) {
                    assertThat(loginResponse.code()).isEqualTo(200);
                }
            }

            var userId = usersDAOMock.user.userId();
            assertThat(userSessionsDAOMock.getValidSessionsCount(userId)).isEqualTo(3);

            // Create a fourth user session that will invalidate the oldest previous one
            var loginRequest = createValidLoginRequest(server);

            try (var loginResponse = client.request(loginRequest)) {
                assertThat(loginResponse.code()).isEqualTo(200);
            }

            assertThat(userSessionsDAOMock.getValidSessionsCount(userId)).isEqualTo(3);
        });
    }

    @Test
    public void logoutShouldInvalidateUserSession() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var loginRequest = createValidLoginRequest(server);

            try (var loginResponse = client.request(loginRequest)) {
                assert loginResponse.body() != null;
                var sessionResponse = objectMapper.readValue(loginResponse.body().string(), UserSessionResponse.class);
                var sessionId = sessionResponse.sessionId();

                var logoutRequest = new Request.Builder()
                        .url("http://localhost:" + server.port() + "/api/users/logout")
                        .header("Cookie", "sessionId=" + sessionId)
                        .post(RequestBody.create(new byte[0]))
                        .build();

                try (var logoutResponse = client.request(logoutRequest)) {
                    assertThat(logoutResponse.code()).isEqualTo(200);

                    assert logoutResponse.body() != null;

                    var expectedResponse = new UserSessionResponse(sessionId);
                    var actualResponse = objectMapper.readValue(logoutResponse.body().string(), UserSessionResponse.class);
                    assertThat(actualResponse).isEqualTo(expectedResponse);

                    UserSession userSession = userSessionsDAOMock.find(UUID.fromString(sessionId));
                    assertThat(userSession.validTo().isBefore(Instant.now())).isTrue();
                }
            }
        });
    }

    @Test
    public void getCurrentSessionShouldReturnErrorIfCookieIsMissing() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            try (var response = client.get("/api/users/current-session")) {
                assertThat(response.code()).isEqualTo(400);

                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Session cookie missing");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
            }
        });
    }

    @Test
    public void getCurrentSessionShouldReturnErrorIfSessionIsNotFound() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var sessionId = UUID.randomUUID();

            var request = new Request.Builder()
                    .url("http://localhost:" + server.port() + "/api/users/current-session")
                    .header("Cookie", "sessionId=" + sessionId)
                    .get()
                    .build();

            try (var response = client.request(request)) {
                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Session with ID " + sessionId + " not found");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
                assertThat(response.code()).isEqualTo(401);
            }
        });
    }

    @Test
    public void getCurrentSessionShouldReturnErrorIfSessionHasExpired() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var sessionId = UUID.randomUUID();
            var userSession = new UserSession(
                    sessionId,
                    UUID.randomUUID(),
                    "user-agent",
                    "127.0.0.1",
                    Instant.now(),
                    Instant.now().minus(Duration.ofSeconds(1))
            );

            userSessionsDAOMock.userSessions.add(userSession);

            var request = new Request.Builder()
                    .url("http://localhost:" + server.port() + "/api/users/current-session")
                    .header("Cookie", "sessionId=" + sessionId)
                    .get()
                    .build();

            try (var response = client.request(request)) {
                assert response.body() != null;

                var expectedResponse = new ErrorResponse("Session has expired");
                var actualResponse = objectMapper.readValue(response.body().string(), ErrorResponse.class);

                assertThat(actualResponse).isEqualTo(expectedResponse);
                assertThat(response.code()).isEqualTo(401);
            }
        });
    }

    @Test
    public void getCurrentSessionShouldReturnSessionIfValid() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var loginRequest = createValidLoginRequest(server);

            try (var loginResponse = client.request(loginRequest)) {
                assert loginResponse.body() != null;
                var sessionResponse = objectMapper.readValue(loginResponse.body().string(), UserSessionResponse.class);
                var sessionId = sessionResponse.sessionId();

                var getCurrentSessionRequest = new Request.Builder()
                        .url("http://localhost:" + server.port() + "/api/users/current-session")
                        .header("Cookie", "sessionId=" + sessionId)
                        .get()
                        .build();

                try (var getCurrentSessionResponse = client.request(getCurrentSessionRequest)) {
                    assertThat(getCurrentSessionResponse.code()).isEqualTo(200);

                    assert getCurrentSessionResponse.body() != null;

                    var expectedResponse = userSessionsDAOMock.find(UUID.fromString(sessionId));
                    var actualResponse = objectMapper.readValue(getCurrentSessionResponse.body().string(), UserSession.class);
                    assertThat(actualResponse).isEqualTo(expectedResponse);
                }
            }
        });
    }

    private Request createLoginRequest(int serverPort, String email, String password) throws JsonProcessingException {
        var loginRequestDTO = new LoginRequestDTO(email, password);

        var requestJson = objectMapper.writeValueAsString(loginRequestDTO);

        var requestBody = RequestBody.create(requestJson.getBytes(StandardCharsets.UTF_8));

        return new Request.Builder()
                .url("http://localhost:" + serverPort + "/api/users/login")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();
    }

    private Request createValidLoginRequest(Javalin server) throws JsonProcessingException {
        return createLoginRequest(server.port(), "user@example.com", "my-secret-password");
    }
}