package com.github.santosleijon.users;

import com.github.santosleijon.Application;
import com.github.santosleijon.common.EnvironmentVariableReader;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.MultipartBody;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class UsersControllerTest {

    EnvironmentVariableReader mockedEnvironmentVariableReader = Mockito.mock(EnvironmentVariableReader.class);
    EnvironmentVariableReaderMock environmentVariableReaderMock = new EnvironmentVariableReaderMock();

    UsersDAO mockedUsersDAO = Mockito.mock(UsersDAO.class);
    UsersDAOMock usersDAOMock = new UsersDAOMock();

    @BeforeEach
    public void beforeEach() {
        environmentVariableReaderMock.setupMock(mockedEnvironmentVariableReader);
        usersDAOMock.setupMock(mockedUsersDAO);
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
            var requestBuilder = createLoginRequest(server.port(), "invalid-email", "my-secret-password");

            try (var response = client.request(requestBuilder)) {
                assertThat(response.code()).isEqualTo(401);
                assert response.body() != null;
                assertThat(response.body().string()).isEqualTo("{ \"error\": \"Invalid user credentials\" }");
            }
        });
    }

    @Test
    public void loginShouldReturnUnauthorizedWhenInvalidPasswordIsSubmitted() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var requestBuilder = createLoginRequest(server.port(), "user@example.com", "invalid-password");

            try (var response = client.request(requestBuilder)) {
                assertThat(response.code()).isEqualTo(401);
                assert response.body() != null;
                assertThat(response.body().string()).isEqualTo("{ \"error\": \"Invalid user credentials\" }");
            }
        });
    }

    @Test
    public void loginShouldReturnSuccessWhenInvalidPasswordIsSubmitted() {
        JavalinTest.test(getJavalinAppUnderTest(), (server, client) -> {
            var requestBuilder = createLoginRequest(server.port(), "user@example.com", "my-secret-password");

            try (var response = client.request(requestBuilder)) {
                assertThat(response.code()).isEqualTo(200);
                assert response.body() != null;
                assertThat(response.body().string()).isEqualTo("{ \"result\": \"ok\" }");
            }
        });
    }

    private Javalin getJavalinAppUnderTest() {
        return Application.getJavalinApp(mockedUsersDAO);
    }

    private static Request createLoginRequest(int serverPort, String email, String password) {
        var requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("EMAIL", email)
                .addFormDataPart("PASSWORD", password)
                .build();

        return new Request.Builder()
                .url("http://localhost:" + serverPort + "/api/users/login")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
    }
}