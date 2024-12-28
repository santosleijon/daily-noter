package com.github.santosleijon.users;

import com.github.santosleijon.common.DatabaseConnection;
import com.github.santosleijon.common.TimestampConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserSessionsDAO {

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
            Instant createdAt = resultSet.getTimestamp("created_at").toInstant();
            Instant validTo = resultSet.getTimestamp("created_at").toInstant();

            return new UserSession(sessionId, userId, createdAt, validTo);
        }
    }

    public void upsert(UserSession userSession) throws SQLException {
        try (var connection = DatabaseConnection.getConnection()) {
            var query = "INSERT INTO user_sessions (session_id, user_id, created_at, valid_to)" +
                        "VALUES (?, ?, ?, ?)" +
                        "ON CONFLICT (session_id)" +
                        "DO UPDATE SET user_id = ?, created_at = ?, valid_to = ?";

            LocalDateTime createdAtZuluLocalDateTime = TimestampConverter.convertToZuluLocalDateTime(userSession.createdAt());
            LocalDateTime validToZuluLocalDateTime = TimestampConverter.convertToZuluLocalDateTime(userSession.validTo());

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, userSession.sessionId());
            preparedStatement.setObject(2, userSession.userId());
            preparedStatement.setObject(3, createdAtZuluLocalDateTime);
            preparedStatement.setObject(4, validToZuluLocalDateTime);
            preparedStatement.setObject(5, userSession.userId());
            preparedStatement.setObject(6, createdAtZuluLocalDateTime);
            preparedStatement.setObject(7, validToZuluLocalDateTime);

            preparedStatement.executeUpdate();
        }
    }
}
