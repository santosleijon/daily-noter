package com.github.santosleijon;

import io.javalin.Javalin;

public class Application {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(PORT);
    }
}