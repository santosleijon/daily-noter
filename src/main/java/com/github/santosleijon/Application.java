package com.github.santosleijon;

import com.github.santosleijon.notes.NotesController;
import com.github.santosleijon.notes.NotesModule;
import com.github.santosleijon.users.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        Injector usersModuleInjector = Guice.createInjector(new UsersModule());
        UsersController usersController = usersModuleInjector.getInstance(UsersController.class);

        Injector notesModuleInjector = Guice.createInjector(new NotesModule());
        NotesController notesController = notesModuleInjector.getInstance(NotesController.class);

        getJavalinApp(usersController, notesController).start(PORT);
    }

    public static Javalin getJavalinApp(UsersController usersController, NotesController notesController) {
        var app = Javalin.create(config -> config.jsonMapper(
                new JavalinJackson().updateMapper(mapper -> mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false))
        ));

        app.post("/api/users/login", usersController::login);
        app.post("/api/users/logout", usersController::logout);
        app.get("/api/users/current-session", usersController::getCurrentSession);

        app.get("/api/notes", notesController::getAndInitializeNotes);
        app.post("/api/notes/{noteId}", notesController::updateNote);
        app.delete("/api/notes/{noteId}", notesController::deleteNote);

        return app;
    }
}