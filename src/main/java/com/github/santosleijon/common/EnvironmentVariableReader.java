package com.github.santosleijon.common;

public class EnvironmentVariableReader {

    public String getDbHost() {
        return System.getenv("DB_HOST");
    }

    public Integer getDbPort() {
        return Integer.valueOf(System.getenv("DB_PORT"));
    }

    public String getDbUser() {
        return System.getenv("DB_USER");
    }

    public String getDbPassword() {
        return System.getenv("DB_PASSWORD");
    }
}
