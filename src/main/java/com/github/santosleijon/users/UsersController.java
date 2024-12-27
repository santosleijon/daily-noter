package com.github.santosleijon.users;

import com.github.santosleijon.common.ErrorResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class UsersController {

    private final UsersDAO usersDAO;

    public UsersController(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    public void login(Context ctx) {
        var email = ctx.formParam("email");
        var password = ctx.formParam("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            ctx.result(new ErrorResponse("User credentials missing").toJson());
            ctx.status(HttpStatus.BAD_REQUEST);
            return;
        }

        try {
            UserDetailsForAuthentication userDetailsForAuthentication = usersDAO.getUserDetailsForAuthentication(email);

            if (!PasswordUtils.verifyPassword(password, userDetailsForAuthentication.hashedPassword())) {
                throw new InvalidUserCredentialsException();
            }

            // TODO: Create session, save to DB
            // TODO: Set cookie

            ctx.result("{ \"result\": \"ok\" }");
            ctx.status(HttpStatus.OK);
        } catch (InvalidUserCredentialsException e) {
            ctx.result(new ErrorResponse("Invalid user credentials").toJson());
            ctx.status(HttpStatus.UNAUTHORIZED);
        }
    };
}
