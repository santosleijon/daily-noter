package com.github.santosleijon.users;

import com.github.santosleijon.users.errors.InvalidUserCredentialsException;

public interface UsersDAO {
    UserDetailsForAuthentication getUserDetailsForAuthentication(String email) throws InvalidUserCredentialsException;
}
