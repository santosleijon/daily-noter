package com.github.santosleijon.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeUtils {

    public static OffsetDateTime getOffsetDateTime(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC);
    }

    public static Instant getInstantFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getObject(columnName, OffsetDateTime.class).toInstant();
    }

    public static OffsetDateTime now() {
        return Instant.now().atOffset(ZoneOffset.UTC);
    }

    public static List<LocalDate> getDatesBetween(LocalDate from, LocalDate to) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = from;

        while (!currentDate.isAfter(to)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    public static LocalDate getLocalDate(String localDateString) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        return LocalDate.parse(localDateString, pattern);
    }
}
