package com.gci.pickem.util;

import com.gci.pickem.model.mysportsfeeds.GameEntry;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ScheduleUtil {

    private static final Pattern GAME_TIME_PATTERN = Pattern.compile("(1?[0-9]):([0-5][0-9])([AP]M)");

    private ScheduleUtil() {
    }

    public static int getSeasonForDate(Instant instant) {
        /*
         * The "season" corresponds to the earliest year games were played. If this game
         * took place late in the year, then the year we have is the season. If the game
         * took place in the beginning of the year, then we need to subtract one from the
         * year we have to get the season.
         */
        LocalDate localDate = instant.atZone(ZoneId.of("America/New_York")).toLocalDate();
        return localDate.getMonthValue() >= Month.AUGUST.getValue() ? localDate.getYear() : localDate.getYear() - 1;
    }

    public static long getUtcGameTime(GameEntry gameEntry) {
        String date = gameEntry.getDate();
        String time = get24HourTime(gameEntry.getTime());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        TemporalAccessor temporalAccessor = formatter.parse(String.format("%s %s", date, time));
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("America/New_York"));
        return Instant.from(zonedDateTime).toEpochMilli();
    }

    static String get24HourTime(String time) {
        Matcher m = GAME_TIME_PATTERN.matcher(time);
        if (!m.matches()) {
            throw new RuntimeException(String.format("Unexpected game time format: %s", time));
        }

        String militaryTime = "";

        String hour = m.group(1);

        String timeOfDay = m.group(3);
        if ("PM".equals(timeOfDay) && !"12".equals(hour)) {
            militaryTime += Integer.toString(Integer.parseInt(hour) + 12);
        } else if ("AM".equals(timeOfDay) && "12".equals(hour)) {
            militaryTime += "00";
        } else {
            Integer intHour = Integer.parseInt(hour);
            if (intHour < 10) {
                militaryTime += "0";
            }

            militaryTime += hour;
        }

        militaryTime += ":" + m.group(2);

        return militaryTime;
    }
}
