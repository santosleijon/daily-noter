package com.github.santosleijon.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;

public class TimestampUtil {

    public static OffsetDateTime getOffsetDateTime(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC);
    }

    public static Instant getInstantFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getObject(columnName, OffsetDateTime.class).toInstant();
    }

    public static OffsetDateTime now() {
        return Instant.now().atOffset(ZoneOffset.UTC);
    }
}
