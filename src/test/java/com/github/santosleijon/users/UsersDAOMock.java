package com.github.santosleijon.users;

import com.github.santosleijon.users.errors.InvalidUserCredentialsException;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UsersDAOMock implements UsersDAO {

    public UserDetailsForAuthentication user = new UserDetailsForAuthentication(
            UUID.randomUUID(),
            "user@example.com",
            "$argon2id$v=19$m=15360,t=3,p=1$s0dJtvaK3gQHBi+gAmCbJmuUQTcFnR8XRYodPA7GnNvOKdjMCrF+AzXPBmcfebL2vgQmlC/kN2wPXHSn8L1BQg$pSih8HiG40aJJiskrfR7gS2cAjFusjuPJhJ4GXmjLdQ",
            Instant.now());

    @Override
    public UserDetailsForAuthentication getUserDetailsForAuthentication(String email) throws InvalidUserCredentialsException {
        if (user.email().equals(email)) {
            return user;
        }

        throw new InvalidUserCredentialsException();
    }

    public void setupMock(UsersDAO mockedUsersDAO) {
        try {
            when(mockedUsersDAO.getUserDetailsForAuthentication(any()))
                    .thenAnswer((Answer<UserDetailsForAuthentication>) invocationOnMock ->
                            getUserDetailsForAuthentication(invocationOnMock.getArgument(0)));
        } catch (InvalidUserCredentialsException e) {
            throw new RuntimeException(e);
        }
    }
}
