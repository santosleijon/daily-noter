package com.github.santosleijon.users;

import com.github.santosleijon.common.DatabaseConnection;
import com.github.santosleijon.users.errors.InvalidUserCredentialsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class UsersDAOImpl implements UsersDAO {

    @Override
    public UserDetailsForAuthentication getUserDetailsForAuthentication(String email) throws InvalidUserCredentialsException {
        try (var connection = DatabaseConnection.getConnection()) {
            var selectUserWithEmail = "SELECT user_id, email, password, created_at FROM users WHERE email = ? LIMIT 1";

            PreparedStatement preparedStatement = connection.prepareStatement(selectUserWithEmail);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InvalidUserCredentialsException();
            }

            UUID dbUserId = UUID.fromString(resultSet.getString("user_id"));
            String dbEmail = resultSet.getString("email");
            String dbHashedPassword = resultSet.getString("password");
            Instant dbCreatedAt = resultSet.getTimestamp("created_at").toInstant();

            return new UserDetailsForAuthentication(dbUserId, dbEmail, dbHashedPassword, dbCreatedAt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
