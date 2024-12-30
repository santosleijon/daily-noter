package com.github.santosleijon.users;

public interface UsersDAO {
    UserDetailsForAuthentication getUserDetailsForAuthentication(String email) throws InvalidUserCredentialsException;
}
