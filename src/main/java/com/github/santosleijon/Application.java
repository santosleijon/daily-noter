package com.github.santosleijon;

import com.github.santosleijon.users.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        // TODO: Replace with dependency injection using Guice?
        var usersDAO = new UsersDAOImpl();
        var userSessionsDAO = new UserSessionsDAOImpl();

        getJavalinApp(usersDAO, userSessionsDAO).start(PORT);
    }

    public static Javalin getJavalinApp(UsersDAO usersDAO, UserSessionsDAO userSessionsDAO) {
        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
            }));
        });

        var usersController = new UsersController(usersDAO, userSessionsDAO);

        app.post("/api/users/login", usersController::login);
        app.post("/api/users/logout", usersController::logout);
        app.get("/api/users/current-session", usersController::getCurrentSession);

        return app;
    }
}