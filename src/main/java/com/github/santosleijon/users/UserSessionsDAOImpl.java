package com.github.santosleijon.users;

import com.github.santosleijon.common.DatabaseConnection;
import com.github.santosleijon.common.TimestampUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.UUID;

public class UserSessionsDAOImpl implements UserSessionsDAO {

    @Override
    public UserSession find(UUID sessionId) throws SQLException, UserSessionNotFound {
        try (var connection = DatabaseConnection.getConnection()) {
            var query = "SELECT session_id, user_id, created_at, valid_to FROM user_sessions WHERE session_id = ? LIMIT 1";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, sessionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new UserSessionNotFound(sessionId);
            }

            UUID userId = UUID.fromString(resultSet.getString("user_id"));
            String userAgent = resultSet.getString("user_agent");
            String ipAddress = resultSet.getString("ip_address");
            Instant createdAt = TimestampUtil.getInstantFromResultSet(resultSet, "created_at");
            Instant validTo = TimestampUtil.getInstantFromResultSet(resultSet, "created_at");

            return new UserSession(sessionId, userId, userAgent, ipAddress, createdAt, validTo);
        }
    }

    @Override
    public void upsert(UserSession userSession) throws SQLException {
        try (var connection = DatabaseConnection.getConnection()) {
            var query = "INSERT INTO user_sessions (session_id, user_id, user_agent, ip_address, created_at, valid_to)" +
                        "VALUES (?, ?, ?, ?, ?, ?)" +
                        "ON CONFLICT (session_id)" +
                        "DO UPDATE SET user_id = ?, created_at = ?, valid_to = ?";

            OffsetDateTime createdAt = TimestampUtil.getOffsetDateTime(userSession.createdAt());
            OffsetDateTime validTo = TimestampUtil.getOffsetDateTime(userSession.validTo());

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, userSession.sessionId());
            preparedStatement.setObject(2, userSession.userId());
            preparedStatement.setObject(3, userSession.userAgent());
            preparedStatement.setObject(4, userSession.ipAddress());
            preparedStatement.setObject(5, createdAt);
            preparedStatement.setObject(6, validTo);
            preparedStatement.setObject(7, userSession.userId());
            preparedStatement.setObject(8, createdAt);
            preparedStatement.setObject(9, validTo);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void invalidateOldSessions(UUID userId) throws SQLException {
        var query = "UPDATE user_sessions SET valid_to = ? WHERE user_id = ? AND valid_to > ? AND session_id NOT IN" +
                " (SELECT session_id FROM user_sessions WHERE user_id = ? AND valid_to > ? ORDER BY created_at DESC LIMIT 3)";

        try (var connection = DatabaseConnection.getConnection()) {
            OffsetDateTime now = TimestampUtil.now();

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, now);
            preparedStatement.setObject(2, userId);
            preparedStatement.setObject(3, now);
            preparedStatement.setObject(4, userId);
            preparedStatement.setObject(5, now);
            preparedStatement.executeUpdate();
        }
    }
}
