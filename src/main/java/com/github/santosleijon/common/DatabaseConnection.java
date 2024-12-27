package com.github.santosleijon.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final EnvironmentVariableReader environmentVariableReader = new EnvironmentVariableReader();

    public static Connection getConnection() throws SQLException {
        var url = String.format("jdbc:postgresql://%s:%d/daily_noter", environmentVariableReader.getDbHost(), environmentVariableReader.getDbPort());

        return DriverManager.getConnection(url, environmentVariableReader.getDbUser(), environmentVariableReader.getDbPassword());
    }
}