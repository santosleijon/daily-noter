package com.github.santosleijon.users;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class UserSessionsDAOMock extends UserSessionsDAO {

    public void setupMock(UserSessionsDAO mockedUserSessionsDAO) {
        try {
            doNothing().when(mockedUserSessionsDAO).insert(any());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
