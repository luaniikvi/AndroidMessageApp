package com.example.androidmessageapp.common;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeFormatter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    private TimeFormatter() {}

    public static String format(long longTimeStamp) {
        return FORMATTER.format(Instant.ofEpochMilli(longTimeStamp));
    }
    public static String getCurrentFormattedTime() {
        return format(System.currentTimeMillis());
    }

    public static long getCurrentLongTimeStamp() {
        return System.currentTimeMillis();
    }
}