package com.github.santosleijon.users;

import java.sql.SQLException;
import java.util.UUID;

public interface UserSessionsDAO {
    UserSession find(UUID sessionId) throws SQLException, UserSessionNotFound;
    void upsert(UserSession userSession) throws SQLException;
    void invalidateOldSessions(UUID userId) throws SQLException;
}
