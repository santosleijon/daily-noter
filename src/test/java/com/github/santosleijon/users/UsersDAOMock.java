package com.github.santosleijon.users;

import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UsersDAOMock implements UsersDAO {

    UserDetailsForAuthentication userDetailsForExistingUser = new UserDetailsForAuthentication(
            UUID.randomUUID(),
            "user@example.com",
            "$argon2id$v=19$m=15360,t=2,p=1$DVrE9DwcCKl7/w0skGW4tN+ZOIN6JX5xjvevGTcP1IEATXd050a55/WBEtWVtwB1TfydbVO5ty7uP3FfkT/8fw$fLsItA8hW0R7kg1f73ubPBzt7P2pn7rXdm9IclbCNR4",
            Instant.now());

    @Override
    public UserDetailsForAuthentication getUserDetailsForAuthentication(String email) throws InvalidUserCredentialsException {
        if (userDetailsForExistingUser.email().equals(email)) {
            return userDetailsForExistingUser;
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
