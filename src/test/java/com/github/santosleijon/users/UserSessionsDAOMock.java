package com.github.santosleijon.users;

import com.github.santosleijon.users.errors.UserSessionNotFound;
import org.mockito.stubbing.Answer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class UserSessionsDAOMock implements UserSessionsDAO {

    public final List<UserSession> userSessions = new ArrayList<>();

    @Override
    public UserSession find(UUID sessionId) throws UserSessionNotFound {
        return userSessions.stream()
                .filter(session -> session.sessionId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new UserSessionNotFound(sessionId));
    }

    @Override
    public void upsert(UserSession userSession) {
        userSessions.removeIf(session -> session.sessionId().equals(userSession.sessionId()));
        userSessions.add(userSession);
    }

    @Override
    public void invalidateOldSessions(UUID userId) {
        List<UUID> validSessionsForUser = userSessions.stream()
                .filter(session -> session.userId().equals(userId) && session.validTo().isAfter(Instant.now()))
                .sorted(Comparator.comparing(UserSession::createdAt).reversed())
                .map(UserSession::sessionId)
                .toList();

        List<UUID> sessionIdsToKeep = validSessionsForUser.subList(0, Math.min(validSessionsForUser.size(), 3));

        userSessions.removeIf(session -> !sessionIdsToKeep.contains(session.sessionId()));
    }

    public long getValidSessionsCount(UUID userId) {
        return userSessions.stream().filter(us -> us.userId().equals(userId) && us.validTo().isAfter(Instant.now())).count();
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

            doAnswer((Answer<Void>) invocationOnMock -> {
                invalidateOldSessions(invocationOnMock.getArgument(0));
                return null;
            })
                    .when(mockedUserSessionsDAO)
                    .invalidateOldSessions(any());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
