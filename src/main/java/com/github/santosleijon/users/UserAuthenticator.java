package com.github.santosleijon.users;

import com.github.santosleijon.users.errors.UnauthorizedUserException;
import com.github.santosleijon.users.errors.UserSessionNotFound;
import io.javalin.http.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

@Singleton
public class UserAuthenticator {

    private final UserSessionsDAO userSessionsDAO;

    @Inject
    public UserAuthenticator(UserSessionsDAO userSessionsDAO) {
        this.userSessionsDAO = userSessionsDAO;
    }

    public UUID authenticateUserAndGetUserId(Context ctx) throws UnauthorizedUserException {
        var sessionId = ctx.cookie("sessionId");

        if (sessionId == null || sessionId.isEmpty()) {
            throw new UnauthorizedUserException();
        }

        try {
            var userSession = userSessionsDAO.find(UUID.fromString(sessionId));

            if (userSession.validTo().isBefore(Instant.now())) {
                throw new UnauthorizedUserException(sessionId);
            }

            return userSession.userId();
        } catch (UserSessionNotFound e) {
            throw new UnauthorizedUserException(sessionId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
