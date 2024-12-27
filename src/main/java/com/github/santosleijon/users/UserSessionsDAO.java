package com.github.santosleijon.users;

import com.github.santosleijon.common.DatabaseConnection;
import com.github.santosleijon.common.TimestampConverter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserSessionsDAO {

    public void insert(UserSession userSession) throws SQLException {
        try (var connection = DatabaseConnection.getConnection()) {
            var query = "INSERT INTO user_sessions (session_id, user_id, created_at, valid_to) VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, userSession.sessionId());
            preparedStatement.setObject(2, userSession.userId());
            preparedStatement.setObject(3, TimestampConverter.convertToZuluLocalDateTime(userSession.createdAt()));
            preparedStatement.setObject(4, TimestampConverter.convertToZuluLocalDateTime(userSession.validTo()));

            preparedStatement.executeUpdate();
        }
    }
}
