package com.github.santosleijon;

import com.github.santosleijon.users.UsersDAO;
import com.github.santosleijon.users.UsersController;
import io.javalin.Javalin;

public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        // TODO: Replace with dependency injection using Guice?
        var usersDAO = new UsersDAO();

        getJavalinApp(usersDAO).start(PORT);
    }

    public static Javalin getJavalinApp(UsersDAO usersDAO) {
        var app = Javalin.create(/*config*/);

        var usersController = new UsersController(usersDAO);

        app.post("/api/users/login", usersController::login);

        return app;
    }
}