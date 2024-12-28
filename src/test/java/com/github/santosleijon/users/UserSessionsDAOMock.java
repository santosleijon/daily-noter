package com.github.santosleijon.users;

import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class UserSessionsDAOMock extends UserSessionsDAO {

    private final List<UserSession> userSessions = new ArrayList<>();

    @Override
    public UserSession find(UUID sessionId) {
        return userSessions.stream()
                .filter(session -> session.sessionId().equals(sessionId))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void upsert(UserSession userSession) {
        userSessions.removeIf(session -> session.sessionId().equals(userSession.sessionId()));
        userSessions.add(userSession);
    }

    public void setupMock(UserSessionsDAO mockedUserSessionsDAO) {
        try {
            doAnswer((Answer<UserSession>) invocationOnMock -> find(invocationOnMock.getArgument(0)))
                    .when(mockedUserSessionsDAO)
                    .find(any());

            doAnswer((Answer<Void>) invocationOnMock -> {
                upsert(invocationOnMock.getArgument(0));
                return null;
            })
                    .when(mockedUserSessionsDAO)
                    .upsert(any());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
