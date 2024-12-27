package com.github.santosleijon.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimestampConverter {

    public static LocalDateTime convertToZuluLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of("Z"));
    }
}
