package com.github.santosleijon.users;

import com.github.santosleijon.common.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.HttpStatus;
import io.javalin.http.SameSite;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class UsersController {

    private final UsersDAO usersDAO;
    private final UserSessionsDAO userSessionsDAO;

    public UsersController(UsersDAO usersDAO, UserSessionsDAO userSessionsDAO) {
        this.usersDAO = usersDAO;
        this.userSessionsDAO = userSessionsDAO;
    }

    public void login(Context ctx) {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            ctx.json(new ErrorResponse("User credentials missing"));
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }

        try {
            UserDetailsForAuthentication userDetailsForAuthentication = usersDAO.getUserDetailsForAuthentication(email);

            if (!PasswordUtils.verifyPassword(password, userDetailsForAuthentication.hashedPassword())) {
                throw new InvalidUserCredentialsException();
            }

            UserSession userSession = new UserSession(
                    UUID.randomUUID(),
                    userDetailsForAuthentication.userId(),
                    ctx.userAgent(),
                    ctx.ip(),
                    Instant.now(),
                    Instant.now().plus(Duration.ofDays(365))
            );

            userSessionsDAO.upsert(userSession);
            userSessionsDAO.invalidateOldSessions(userDetailsForAuthentication.userId());

            ctx.cookie(createSessionCookie(userSession));
            ctx.json(new UserSessionResponse(userSession.sessionId()));
            ctx.status(HttpStatus.OK);
        } catch (InvalidUserCredentialsException e) {
            ctx.json(new ErrorResponse("Invalid user credentials"));
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout(Context ctx) {
        var sessionId = ctx.cookie("sessionId");

        if (sessionId == null || sessionId.isBlank()) {
            ctx.json(new ErrorResponse("Session cookie missing"));
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }

        try {
            UserSession userSession = userSessionsDAO.find(UUID.fromString(sessionId));
            UserSession invalidatedUserSession = userSession.withValidTo(Instant.now());
            userSessionsDAO.upsert(invalidatedUserSession);
            ctx.removeCookie("sessionId");
            ctx.json(new UserSessionResponse(sessionId));
        } catch (UserSessionNotFound e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getCurrentSession(Context ctx) {
        var sessionId = ctx.cookie("sessionId");

        if (sessionId == null || sessionId.isBlank()) {
            ctx.json(new ErrorResponse("Session cookie missing"));
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }

        try {
            var session = userSessionsDAO.find(UUID.fromString(sessionId));

            if (session.validTo().isBefore(Instant.now())) {
                ctx.json(new ErrorResponse("Session has expired"));
                ctx.status(HttpStatus.UNAUTHORIZED);
                return;
            }

            ctx.json(session);
        } catch (UserSessionNotFound e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            ctx.json(new ErrorResponse("Invalid session ID format"));
            ctx.status(HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Cookie createSessionCookie(UserSession userSession) {
        var cookie = new Cookie("sessionId", userSession.sessionId().toString());

        cookie.setMaxAge((int) Duration.between(Instant.now(), userSession.validTo()).getSeconds());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSameSite(SameSite.STRICT);
        cookie.setSecure(false); // Disabled to allow cookies when running locally without HTTPS

        return cookie;
    }
}
